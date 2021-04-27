package com.owl.owlserver.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.owl.owlserver.DTO.Deserialize.NewSale.NewSaleDTO;
import com.owl.owlserver.Service.SaleService;
import com.owl.owlserver.Service.ShipmentService;
import com.owl.owlserver.model.*;
import com.owl.owlserver.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.*;
import java.util.List;

@PreAuthorize("hasRole('WAREHOUSE') or hasRole('STORE') or hasRole('OFFICE') or hasRole('ADMIN')")
@RestController
@RequestMapping("/posEndpoint")
public class posEndpoint {

    //injecting repositories for database access
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    PromotionRepository promotionRepository;
    @Autowired
    SaleDetailRepository saleDetailRepository;
    @Autowired
    SaleRepository saleRepository;
    @Autowired
    StoreQuantityRepository storeQuantityRepository;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    ShipmentRepository shipmentRepository;
    @Autowired
    ShipmentDetailRepository shipmentDetailRepository;
    @Autowired
    WarehouseRepository warehouseRepository;
    @Autowired
    WarehouseQuantityRepository warehouseQuantityRepository;

    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private SaleService saleService;

    //JACKSON object Mapper
    private static final ObjectMapper objectMapper = new ObjectMapper();

    //REST endpoints
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> ping() {
        return new ResponseEntity<>("Hello world! :), GET request successfully received", HttpStatus.OK);
    }

    @GetMapping("/getCustomerByName")
    public ResponseEntity<List<Customer>> getCustomerByName(@RequestParam String firstName, String lastName) {
        return new ResponseEntity<>(customerRepository.findAllByFirstNameAndLastName(firstName, lastName), HttpStatus.OK);
    }

    @GetMapping("/getCustomerByPhoneNumber")
    public ResponseEntity<List<Customer>> getCustomerByPhoneNumber(@RequestParam String phoneNumber) {
        return new ResponseEntity<>(customerRepository.findAllByPhoneNumber(phoneNumber), HttpStatus.OK);
    }

    @GetMapping("/getInStoreProductQuantity")
    public ResponseEntity<JsonNode> getInStoreProductQuantity(@RequestParam int storeId, String productId) {
        if(!storeRepository.existsById(storeId)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with specified ID exists");
        }
        StoreQuantity storeQuantity = storeQuantityRepository.findByStore_StoreIdAndProduct_ProductId(storeId,productId);
        if (storeQuantity==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with specified ID not in stock in store");
        }
        JsonNode jsonNode = objectMapper.convertValue(storeQuantity.getProduct(), JsonNode.class);
        ((ObjectNode)jsonNode).put("quantity", storeQuantity.getInstoreQuantity());
        return new ResponseEntity<>(jsonNode, HttpStatus.OK);
    }

    @GetMapping("/getCustomLensList")
    public ResponseEntity<List<Product>> getCustomLensList() {
        return new ResponseEntity<>(productRepository.findAllByProductIdStartsWith("CL"), HttpStatus.OK);
    }

    @GetMapping("/getStorePromotions")
    public ResponseEntity<List<Promotion>> getStorePromotions(@RequestParam int storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with specified ID exists"));
        return new ResponseEntity<>(store.getPromotionList(), HttpStatus.OK);
    }

    @GetMapping("/getStoreEmployees")
    public ResponseEntity<List<Employee>> getStoreEmployees(@RequestParam int storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with specified ID exists"));
        return new ResponseEntity<>(store.getEmployeesList(), HttpStatus.OK);
    }

    @GetMapping("/getPendingSaleList")
    public List<Sale> getPendingSaleList(@RequestParam int storeId) {
        storeRepository.findById(storeId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with specified ID exists"));
        return saleRepository.getAllByStoreStoreIdAndPickupDateEquals(storeId, null);
    }

    @Transactional
    @PostMapping(value = "/newSale")
    public ResponseEntity<Integer> newSaleTest(@RequestBody NewSaleDTO newSaleDTO)  {
        return new ResponseEntity<>(saleService.newSale(newSaleDTO), HttpStatus.OK);
    }

    @Transactional
    @PostMapping(value = "/updateSale")
    public ResponseEntity<String> updateSale(@RequestBody String jsonString) throws JsonProcessingException {
        JsonNode wholeJSON = objectMapper.readTree(jsonString);
        Sale sale = saleRepository.findById(wholeJSON.get("saleId").asInt()).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Sale with specified ID exists"));
        saleService.updateSale(sale, wholeJSON);
        return new ResponseEntity<>("Successfully updated Sale", HttpStatus.OK);
    }

    @Transactional
    @PostMapping(value = "/receiveShipment")
    public ResponseEntity<String> receiveShipment(@RequestBody String jsonString) throws JsonProcessingException {
        JsonNode wholeJSON = objectMapper.readTree(jsonString);
        Shipment shipment = shipmentRepository.findById(wholeJSON.get("shipmentId").asInt()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No shipment with specified ID exists"));
        shipmentService.receiveInternalShipment(shipment, 3 , wholeJSON.get("storeId").asInt());
        return new ResponseEntity<>("Shipment received by store!", HttpStatus.OK);
    }

    @GetMapping("/getRecentSalesList")
    public ResponseEntity<List<Sale>> getRecentSalesList(int storeId) {
        List<Sale> saleList = saleRepository.getAllByInitialDepositDateIsBetweenAndStoreStoreIdOrderByInitialDepositDate(LocalDate.now().minusDays(2).atStartOfDay(), LocalDate.now().atTime(LocalTime.MAX), storeId);
        return new ResponseEntity<>(saleList, HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/refundSale")
    public ResponseEntity<String> refundSale(@RequestBody String jsonString) throws JsonProcessingException {
        saleService.newRefund(objectMapper.readTree(jsonString));
        return new ResponseEntity<>("Sale successfully voided", HttpStatus.OK);
    }


    @Transactional
    @PostMapping(value = "/newTransferShipment")
    public ResponseEntity<String> newTransferShipment(@RequestBody Shipment shipment) {
        shipmentService.persistShipment(shipment);
        return new ResponseEntity<>("Successfully created new transfer shipment, ID: "+shipment.getShipmentId(), HttpStatus.OK);
    }

    @GetMapping("/getPromotionalFirstSaleList")
    public ResponseEntity<List<Sale>> getPromotionalFirstSaleList(int storeId, int promoId) {
        List<Sale> saleList = saleRepository.getAllByInitialDepositDateIsBetweenAndStoreStoreIdAndFullyPaidIsAndPromotionParentSaleId(LocalDate.now().atStartOfDay(), LocalDate.now().atTime(LocalTime.MAX), storeId, true, -promoId);
        return new ResponseEntity<>(saleList, HttpStatus.OK);
    }

}


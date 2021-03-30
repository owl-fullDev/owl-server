package com.owl.owlserver.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.owl.owlserver.DTO.NewSaleDTO;
import com.owl.owlserver.Service.RefundService;
import com.owl.owlserver.Service.SaleService;
import com.owl.owlserver.Service.ShipmentService;
import com.owl.owlserver.model.*;
import com.owl.owlserver.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@CrossOrigin
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
    private RefundService refundService;
    @Autowired
    private SaleService saleService;

    //JACKSON object Mapper
    private static final ObjectMapper objectMapper = new ObjectMapper();

    //Time settings
    final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    final ZoneId serverLocalTime = ZoneId.systemDefault();

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
    public ResponseEntity<List<Sale>> getPendingSaleList(@RequestParam int storeId) {
        storeRepository.findById(storeId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with specified ID exists"));
        return new ResponseEntity<>(saleRepository.getAllByStoreStoreIdAndPickupDateEquals(storeId, null), HttpStatus.OK);
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

        int saleId = wholeJSON.get("saleId").asInt();
        Sale sale = saleRepository.findById(saleId).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Sale with specified ID exists"));

        String zonePickUpTime = wholeJSON.get("pickUpDate").asText();
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(zonePickUpTime, formatter);
        ZonedDateTime convertedTime = zonedDateTime.withZoneSameInstant(serverLocalTime);
        LocalDateTime localPickUpTime = convertedTime.toLocalDateTime();

        if (sale.isFullyPaid()) {
            sale.setPickupDate(localPickUpTime);
        }
        else {
            sale.setPickupDate(localPickUpTime);
            sale.setFinalDepositDate(localPickUpTime);
            sale.setFinalDepositType(wholeJSON.get("finalPaymentType").asText());
            if (sale.getGrandTotal()-sale.getInitialDepositAmount()-wholeJSON.get("finalPaymentAmount").asDouble()>sale.getGrandTotal()*0.01){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Final payment does not cover amount due!");
            }
            sale.setFinalDepositAmount(wholeJSON.get("finalPaymentAmount").asDouble());
        }
        saleRepository.save(sale);
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

        LocalDate localDateStart = LocalDate.now().minusDays(2);
        LocalDate localDateEnd = LocalDate.now();

        LocalDateTime startPeriod = localDateStart.atStartOfDay();
        LocalDateTime endPeriod = localDateEnd.atTime(LocalTime.MAX);

        List<Sale> saleList = saleRepository.getAllByInitialDepositDateIsBetweenAndStoreStoreIdOrderByInitialDepositDate(startPeriod, endPeriod, storeId);
        return new ResponseEntity<>(saleList, HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/refundSale")
    public ResponseEntity<String> refundSale(@RequestBody String jsonString) throws JsonProcessingException {
        JsonNode wholeJSON = objectMapper.readTree(jsonString);
        refundService.newRefund(wholeJSON);
        return new ResponseEntity<>("Sale successfully voided", HttpStatus.OK);
    }


    @Transactional
    @PostMapping(value = "/newTransferShipment")
    public ResponseEntity<String> newTransferShipment(@RequestBody Shipment shipment) {

        if (shipment==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All shipment details are empty!");
        }
        shipmentService.persistShipment(shipment);
        return new ResponseEntity<>("Successfully created new transfer shipment, ID: "+shipment.getShipmentId(), HttpStatus.OK);
    }

    @GetMapping("/getPromotionalFirstSaleList")
    public ResponseEntity<List<Sale>> getPromotionalFirstSaleList(int storeId, int promoId) {

        LocalDate localDate = LocalDate.now();

        LocalDateTime startPeriod = localDate.atStartOfDay();
        LocalDateTime endPeriod = localDate.atTime(LocalTime.MAX);

        List<Sale> saleList = saleRepository.getAllByInitialDepositDateIsBetweenAndStoreStoreIdAndFullyPaidIsAndPromotionParentSaleId(startPeriod, endPeriod, storeId, true, -promoId);
        return new ResponseEntity<>(saleList, HttpStatus.OK);
    }

}


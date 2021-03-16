package com.owl.owlserver.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.owl.owlserver.Service.ShipmentService;
import com.owl.owlserver.deserializer.ShipmentDeserializer;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

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
        Store store = storeRepository.findById(storeId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with specified ID"));
        Product product = productRepository.findById(productId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No product with specified ID exists"));
        StoreQuantity storeQuantity = storeQuantityRepository.findByStoreAndProduct_ProductId(store,productId);
        if (storeQuantity==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with specified ID not in stock in store");
        }
        JsonNode jsonNode = objectMapper.convertValue(product, JsonNode.class);
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
    public ResponseEntity<String> newSale(@RequestBody String jsonString) throws JsonProcessingException {
        JsonNode wholeJSON = objectMapper.readTree(jsonString);

        //new customer or existing customer validation
        int customerId = wholeJSON.get("customerId").asInt();
        Customer customer;
        if (customerId == 0) {
            JsonNode customerJSON = wholeJSON.get("newCustomer");
            customer = objectMapper.treeToValue(customerJSON, Customer.class);
            customerRepository.save(customer);
        }
        else {
            customer = customerRepository.findById(customerId).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, "No customer with specified ID exist"));
        }

        //sale deserialization
        JsonNode sale = wholeJSON.get("sale");
        String initialDepositType = sale.get("initialDepositType").asText();

        String initialDepositDateStr = sale.get("initialDepositDate").asText();
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(initialDepositDateStr, formatter);
        ZonedDateTime convertedTime = zonedDateTime.withZoneSameInstant(serverLocalTime);
        LocalDateTime initialDepositDate = convertedTime.toLocalDateTime();

        //store check
        int storeId = sale.get("storeId").asInt();
        Store store = storeRepository.findById(storeId).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, "No store with specified ID exists"));

        //employee check
        int employeeId = sale.get("employeeId").asInt();
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, "No employee with specified ID exists"));

        //promotion check
        int promotionId = sale.get("promotionId").asInt();
        if (promotionId != 0) {
            promotionRepository.findById(promotionId).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, "No promotion with specified ID exists"));
        }

        //full payment or deposit
        double grandTotal = sale.get("grandTotal").asDouble();
        double initialDepositAmount = sale.get("initialDepositAmount").asDouble();
        boolean fullyPaid;
        fullyPaid = !(grandTotal > initialDepositAmount);

        //new sale
        Sale newSale = new Sale(employeeId, store, grandTotal, initialDepositDate, initialDepositType, initialDepositAmount, fullyPaid);
        customer.addSale(newSale);
        newSale.setCustomer(customer);
        saleRepository.save(newSale);

        //saleDetails deserialization
        JsonNode products = wholeJSON.get("products");
        int itemList = wholeJSON.get("itemsSold").asInt();

        for (int i = 0; i < itemList; i++) {
            String productId = products.get(i).get("productId").asText();
            int quantity = products.get(i).get("quantity").asInt();
            Product product = productRepository.findById(productId).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, "No product with specified ID exists"));
            SaleDetail newSaleDetail = new SaleDetail(product, quantity);
            newSale.addSaleDetail(newSaleDetail);
            newSaleDetail.setSale(newSale);
            saleDetailRepository.save(newSaleDetail);

            //if product purchased is NOT a custom lens, decrease in-store quantity
            if (!(product.getProductId().startsWith("CL"))){
                StoreQuantity storeQuantity = storeQuantityRepository.findByStoreAndProduct_ProductId(store, productId);
                if (storeQuantity.getInstoreQuantity()-quantity<0){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough of product in stock in store");
                }
                storeQuantity.setInstoreQuantity(storeQuantity.getInstoreQuantity() - quantity);
                storeQuantityRepository.save(storeQuantity);
            }
        }
        return new ResponseEntity<>("new sale created", HttpStatus.OK);
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

        int ShipmentId = wholeJSON.get("shipmentId").asInt();
        Shipment shipment = shipmentRepository.findById(ShipmentId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No shipment with specified ID exists"));

        if (shipment.getDestinationType()!=3){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This shipment is not meant for a store!");
        }

        int storeId = wholeJSON.get("storeId").asInt();
        Store store = storeRepository.findById(storeId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with specified ID exists"));

        if (shipment.getReceivedTimestamp()!=null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This shipment has already been received");
        }

        if (shipment.getDestinationId()!=storeId){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This shipment is not meant for this store!");
        }

        String zonePickUpTime = wholeJSON.get("receivedDate").asText();
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(zonePickUpTime, formatter);
        ZonedDateTime convertedTime = zonedDateTime.withZoneSameInstant(serverLocalTime);
        LocalDateTime localPickUpTime = convertedTime.toLocalDateTime();

        List<ShipmentDetail> shipmentDetailList = shipment.getShipmentDetailList();
        for (ShipmentDetail shipmentDetail : shipmentDetailList){
            Product product = shipmentDetail.getProduct();
            int quantity = shipmentDetail.getQuantity();
            shipmentDetail.setReceivedQuantity(quantity);
            shipmentDetailRepository.save(shipmentDetail);
            StoreQuantity storeQuantity = storeQuantityRepository.findByStoreAndProduct_ProductId(store, product.getProductId());

            //first time receiving product
            if (storeQuantity==null){
                storeQuantity = new StoreQuantity(store,product,quantity,quantity);
                storeQuantityRepository.save(storeQuantity);
            }
            else {
                storeQuantity.setInstoreQuantity(storeQuantity.getInstoreQuantity()+quantity);
                storeQuantityRepository.save(storeQuantity);
            }
        }
        shipment.setReceivedTimestamp(localPickUpTime);
        shipmentRepository.save(shipment);
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
    @GetMapping("/refundSale")
    public ResponseEntity<String> refundSale(int saleId) {

        Sale sale = saleRepository.findById(saleId).orElse(null);
        if(sale==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No sale exists");
        }

        List<SaleDetail> saleDetailList = sale.getSaleDetailList();
        saleDetailRepository.deleteAll(saleDetailList);
        saleRepository.deleteById(saleId);
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

}


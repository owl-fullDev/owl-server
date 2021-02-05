package com.owl.owlserver.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.owl.owlserver.model.*;
import com.owl.owlserver.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    //Time settings
    final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    final ZoneId serverLocalTime = ZoneId.systemDefault();

    //REST endpoints
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String ping() {
        return "Hello world! :), GET request successfully received";
    }

    @GetMapping("/getCustomerByName")
    public List<Customer> getCustomerByName(@RequestParam String firstName, String lastName) {
        return customerRepository.findAllByFirstNameAndLastName(firstName, lastName);
    }

    @GetMapping("/getCustomerByPhoneNumber")
    public List<Customer> getCustomerByPhoneNumber(@RequestParam String phoneNumber) {
        return customerRepository.findAllByPhoneNumber(phoneNumber);
    }

    @GetMapping("/getInStoreProductQuantity")
    public Product getInStoreProductQuantity(@RequestParam int storeId, String productId) {
        Store store = storeRepository.findById(storeId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with specified ID"));
        StoreQuantity storeQuantity = storeQuantityRepository.findByStoreAndProductId(store,productId);
        if (storeQuantity==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No product with specified ID in stock in store");
        }
        int quantity = storeQuantity.getInstoreQuantity();
        Product product = productRepository.findById(productId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No product with specified ID exists"));
        product.setStoreQuantity(quantity);
        return product;
    }

    @GetMapping("/getCustomLensList")
    public List<Product> getCustomLensList() {
        List<Product> productList = productRepository.findAllByProductIdStartsWith("CL");
        return productList;
    }

    @GetMapping("/getStorePromotions")
    public List<Promotion> getStorePromotions(@RequestParam int storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with specified ID exists"));
        return store.getPromotionList();
    }

    @GetMapping("/getStoreEmployees")
    public List<Employee> getStoreEmployees(@RequestParam int storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with specified ID exists"));
        return store.getEmployeesList();
    }

    @GetMapping("/getPendingSaleList")
    public List<Sale> getPendingSaleList(@RequestParam int storeId) {
        List<Sale> pendingSaleList = saleRepository.getAllByStoreStoreIdAndPickupDateEquals(storeId, null);
        return pendingSaleList;
    }

    @PostMapping(value = "/newSale")
    public String newSale(@RequestBody String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode wholeJSON = objectMapper.readTree(jsonString);
        int customerId = wholeJSON.get("customerId").asInt();
        Customer customer;

        //new customer or existing customer
        if (customerId == 0) {
            JsonNode customerJSON = wholeJSON.get("newCustomer");
            customer = objectMapper.treeToValue(customerJSON, Customer.class);
            customerRepository.save(customer);
        }
        else {
            customer = customerRepository.findById(customerId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No customer with specified details exist"));
        }

        //sale deserialization
        JsonNode sale = wholeJSON.get("sale");
        int promotionId = sale.get("promotionId").asInt(0);
        int employeeId = sale.get("employeeId").asInt(0);
        int storeId = sale.get("storeId").asInt(0);
        double grandTotal = sale.get("grandTotal").asDouble(0);
        String initialDepositDateStr = sale.get("initialDepositDate").asText();
        String initialDepositType = sale.get("initialDepositType").asText();
        double initialDepositAmount = sale.get("initialDepositAmount").asDouble(0);
        boolean fullyPaid;

        ZonedDateTime zonedDateTime = ZonedDateTime.parse(initialDepositDateStr, formatter);
        ZonedDateTime convertedTime = zonedDateTime.withZoneSameInstant(serverLocalTime);
        LocalDateTime initialDepositDate = convertedTime.toLocalDateTime();

        Store store = storeRepository.findById(storeId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with specified ID exists"));

        if (promotionId != 0) {
            Promotion promotion = promotionRepository.findById(promotionId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No promotion with specified ID exists"));
        }

        //full payment or deposit
        if (grandTotal > initialDepositAmount) {
            fullyPaid = false;
        } else {
            fullyPaid = true;
        }

        //new sale
        Sale newSale = new Sale(employeeId, store, grandTotal, initialDepositDate, initialDepositType, initialDepositAmount, fullyPaid);
        customer.addSale(newSale);
        newSale.setCustomer(customer);
        saleRepository.save(newSale);

        //saleDetails
        JsonNode products = wholeJSON.get("products");
        int itemList = wholeJSON.get("itemsSold").asInt();
        String saleDetailList = "";

        for (int i = 0; i < itemList; i++) {
            String productId = wholeJSON.get("products").get(i).get("productId").asText();
            int quantity = wholeJSON.get("products").get(i).get("quantity").asInt();
            Product product = productRepository.findById(productId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No product with specified ID exists"));
            SaleDetail newSaleDetail = new SaleDetail(product, quantity);
            newSale.addSaleDetail(newSaleDetail);
            newSaleDetail.setSale(newSale);
            saleDetailRepository.save(newSaleDetail);

            //if product purchased is NOT a custom lens
            if (!(product.getProductId().startsWith("CL"))){
                StoreQuantity storeQuantity = storeQuantityRepository.findByStoreAndProductId(store, productId);
                storeQuantity.setInstoreQuantity(storeQuantity.getInstoreQuantity() - quantity);
                storeQuantityRepository.saveAndFlush(storeQuantity);
            }
            saleDetailList += "\n" + newSaleDetail.toString();
        }
        return customer.toString() + "\n\n" + customer.getSale(newSale).toString() + "\n\n" + saleDetailList;
    }

    @PostMapping(value = "/updateSale")
    public String updateSale(@RequestBody String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode wholeJSON = objectMapper.readTree(jsonString);
        int saleId = wholeJSON.get("saleId").asInt();
        String zonePickUpTime = wholeJSON.get("pickUpDate").asText();

        ZonedDateTime zonedDateTime = ZonedDateTime.parse(zonePickUpTime, formatter);
        ZonedDateTime convertedTime = zonedDateTime.withZoneSameInstant(serverLocalTime);
        LocalDateTime localPickUpTime = convertedTime.toLocalDateTime();

        Sale sale = saleRepository.findById(saleId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No Sale with specified ID exists"));
        if (sale.isFullyPaid()) {
            sale.setPickupDate(localPickUpTime);
        }
        else {
            sale.setPickupDate(localPickUpTime);
            sale.setFinalDepositDate(localPickUpTime);
            sale.setFinalDepositType(wholeJSON.get("finalPaymentType").asText());
            sale.setFinalDepositAmount(wholeJSON.get("finalPaymentAmount").asDouble());
        }
        saleRepository.save(sale);
        return "Successfully updates Sale";
    }

//    @PostMapping(value = "/receiveShipment")
//    public ResponseEntity<String> receiveShipment(@RequestBody String jsonString) throws JsonProcessingException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode wholeJSON = objectMapper.readTree(jsonString);
//        int storeId = wholeJSON.get("storeId").asInt();
//        int ShipmentId = wholeJSON.get("ShipmentId").asInt();
//        String zonePickUpTime = wholeJSON.get("receivedDate").asText();
//
//        ZonedDateTime zonedDateTime = ZonedDateTime.parse(zonePickUpTime, formatter);
//        ZonedDateTime convertedTime = zonedDateTime.withZoneSameInstant(serverLocalTime);
//        LocalDateTime localPickUpTime = convertedTime.toLocalDateTime();
//
//        Shipment shipment = shipmentRepository.findById(ShipmentId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No  shipment with specified ID exists"));
//
//        if (shipment.getReceivedTimestamp()!=null){
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This shipment has already been received");
//        }
//
//        Store store = storeRepository.findById(storeId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with specified ID exists"));
//
//        if (shipment.getStore().getStoreId()!=storeId){
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This shipment is not meant for this store!");
//        }
//
//        List<ShipmentDetail> shipmentDetailList = shipment.getShipmentDetailList();
//        for (ShipmentDetail shipmentDetail : shipmentDetailList){
//
//            Product product = shipmentDetail.getProduct();
//            int quantity = shipmentDetail.getQuantity();
//            StoreQuantity storeQuantity = storeQuantityRepository.findByStoreAndProductId(store, product.getProductId());
//
//            //first time receiving product
//            if (storeQuantity==null){
//                storeQuantity = new StoreQuantity(store,product.getProductId(),quantity);
//                storeQuantityRepository.saveAndFlush(storeQuantity);
//            }
//            else {
//                storeQuantity.setInstoreQuantity(storeQuantity.getInstoreQuantity()+quantity);
//                storeQuantityRepository.saveAndFlush(storeQuantity);
//            }
//        }
//
//        shipment.setReceivedTimestamp(localPickUpTime);
//        shipmentRepository.saveAndFlush(shipment);
//
//        return new ResponseEntity<>("Shipment received by store!", HttpStatus.OK);
//    }

    @GetMapping("/getRecentSalesList")
    public ResponseEntity<List<Sale>> getRecentSalesList(int storeId) {

            LocalDate localDateStart = LocalDate.now().minusDays(7);
            LocalDate localDateEnd = LocalDate.now();

            LocalDateTime startPeriod = localDateStart.atStartOfDay();
            LocalDateTime endPeriod = localDateEnd.atTime(LocalTime.MAX);

            List<Sale> saleList = saleRepository.getAllByInitialDepositDateIsBetweenAndStoreStoreIdOrderByInitialDepositDate(startPeriod,endPeriod,storeId);

            return new ResponseEntity<>(saleList, HttpStatus.OK);
    }

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

    @GetMapping("/test")
    public Store test(int s) {

        Store store = storeRepository.findById(s).orElse(null);
        if(store==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No store exists");
        }
        else {
            return store;
        }
    }
}


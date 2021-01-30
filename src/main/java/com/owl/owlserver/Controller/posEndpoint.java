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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    RestockShipmentRepository restockShipmentRepository;
    @Autowired
    RestockShipmentDetailRepository restockShipmentDetailRepository;
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

    @GetMapping({"/getStoreFrameQuantity", "/getStoreLensQuantity"})
    public Product getStoreFrameQuantity(@RequestParam int storeId, String frameId) {
        Store store = storeRepository.findById(storeId).orElse(null);
        int quantity = storeQuantityRepository.findByStoreAndProductId(store, frameId).getInstoreQuantity();
        Product product = productRepository.findById(frameId).orElse(null);
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
        Store store = storeRepository.findById(storeId).orElse(null);
        return store.getPromotionList();
    }

    @GetMapping("/getStoreEmployees")
    public List<Employee> getStoreEmployees(@RequestParam int storeId) {
        Store store = storeRepository.findById(storeId).orElse(null);
        return store.getEmployeesList();
    }

    @GetMapping("/getPendingSaleList")
    public List<Sale> getPendingSaleList(@RequestParam int storeId) throws JsonProcessingException {
        List<Sale> pendingSaleList = saleRepository.getAllByStoreStoreIdAndPickupDateEquals(storeId, null);
        return pendingSaleList;
    }

    @PostMapping(value = "/newSale")
    public String newSale(@RequestBody String jsonString) throws JsonProcessingException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode wholeJSON = objectMapper.readTree(jsonString);
        int customerId = wholeJSON.get("customerId").asInt();
        Customer customer;

        //new customer or existing customer
        if (customerId == 0) {
            JsonNode customerJSON = wholeJSON.get("newCustomer");
            customer = objectMapper.treeToValue(customerJSON, Customer.class);
            customerRepository.save(customer);
        } else {
            customer = customerRepository.findById(customerId).orElse(null);
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

        Store store = storeRepository.findById(storeId).orElse(null);

        if (promotionId != 0) {
            Promotion promotion = promotionRepository.findById(promotionId).orElse(null);
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
            Product product = productRepository.findById(productId).orElse(null);
            SaleDetail newSaleDetail = new SaleDetail(product, quantity);
            newSale.addSaleDetail(newSaleDetail);
            newSaleDetail.setSale(newSale);
            saleDetailRepository.save(newSaleDetail);
            StoreQuantity storeQuantity = storeQuantityRepository.findByStoreAndProductId(store,productId);
            storeQuantity.setInstoreQuantity(storeQuantity.getInstoreQuantity()-quantity);
            storeQuantityRepository.saveAndFlush(storeQuantity);
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

        Sale sale = saleRepository.findById(saleId).orElse(null);
        if (sale.isFullyPaid()) {
            sale.setPickupDate(localPickUpTime);
        } else {
            sale.setPickupDate(localPickUpTime);
            sale.setFinalDepositDate(localPickUpTime);
            sale.setFinalDepositType(wholeJSON.get("finalPaymentType").asText());
            sale.setFinalDepositAmount(wholeJSON.get("finalPaymentAmount").asDouble());
        }
        saleRepository.save(sale);
        return "Successfully updates Sale";
    }

    @PostMapping(value = "/receiveRestockShipment")
    public ResponseEntity<String> receiveRestockShipment(@RequestBody String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode wholeJSON = objectMapper.readTree(jsonString);
        int storeId = wholeJSON.get("storeId").asInt();
        int restockShipmentId = wholeJSON.get("restockShipmentId").asInt();
        String zonePickUpTime = wholeJSON.get("receivedDate").asText();

        ZonedDateTime zonedDateTime = ZonedDateTime.parse(zonePickUpTime, formatter);
        ZonedDateTime convertedTime = zonedDateTime.withZoneSameInstant(serverLocalTime);
        LocalDateTime localPickUpTime = convertedTime.toLocalDateTime();

        RestockShipment restockShipment = restockShipmentRepository.findById(restockShipmentId).orElse(null);

        if (restockShipment==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no restock shipment with specified ID");
        }

        if (restockShipment.getReceivedTimestamp()!=null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This shipment has already been received");
        }

        Store store = storeRepository.findById(storeId).orElse(null);
        if (store==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no store with specified ID");
        }

        if (restockShipment.getStore().getStoreId()!=storeId){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This shipment is not meant for this store!");
        }

        List<RestockShipmentDetail> restockShipmentDetailList = restockShipment.getRestockShipmentDetailList();
        for (RestockShipmentDetail restockShipmentDetail: restockShipmentDetailList){

            Product product = restockShipmentDetail.getProduct();
            int quantity = restockShipmentDetail.getQuantity();
            StoreQuantity storeQuantity = storeQuantityRepository.findByStoreAndProductId(store, product.getProductId());

            //first time receiving product
            if (storeQuantity==null){
                storeQuantity = new StoreQuantity(store,product.getProductId(),quantity);
                storeQuantityRepository.saveAndFlush(storeQuantity);
            }
            else {
                storeQuantity.setInstoreQuantity(storeQuantity.getInstoreQuantity()+quantity);
                storeQuantityRepository.saveAndFlush(storeQuantity);
            }
        }

        restockShipment.setReceivedTimestamp(localPickUpTime);
        restockShipmentRepository.saveAndFlush(restockShipment);

        return new ResponseEntity<>("Restock shipment received by store!", HttpStatus.OK);
    }
}


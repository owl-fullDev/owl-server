package com.owl.owlserver.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.owl.owlserver.model.*;
import com.owl.owlserver.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
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

    //Time settings
    final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    final ZoneId serverLocalTime = ZoneId.systemDefault();

    //REST endpoints
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String ping() {
        return "Hello world! :), GET request successfully recieved";
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

    @GetMapping("/getCustomFrameList")
    public List<Product> getCustomFrameList() {
        return productRepository.findAllByProductIdStartsWith("cl");
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
//        int customerId = wholeJSON.get("customerId").asInt();
//        Customer customer;
//
//        //new customer or existing customer
//        if (customerId == 0) {
//            JsonNode customerJSON = wholeJSON.get("newCustomer");
//            customer = objectMapper.treeToValue(customerJSON, Customer.class);
//            customerRepository.save(customer);
//        } else {
//            customer = customerRepository.findById(customerId).orElse(null);
//        }
//
//        //sale deserialization
//        JsonNode sale = wholeJSON.get("sale");
//        int promotionId = sale.get("promotionId").asInt(0);
//        int employeeId = sale.get("employeeId").asInt(0);
//        int storeId = sale.get("storeId").asInt(0);
//        double grandTotal = sale.get("grandTotal").asDouble(0);
//        String initialDepositDateStr = sale.get("initialDepositDate").asText();
//        String initialDepositType = sale.get("initialDepositType").asText();
//        double initialDepositAmount = sale.get("initialDepositAmount").asDouble(0);
//        boolean fullyPaid;
//
//        ZonedDateTime zonedDateTime = ZonedDateTime.parse(initialDepositDateStr, formatter);
//        ZonedDateTime convertedTime = zonedDateTime.withZoneSameInstant(serverLocalTime);
//        LocalDateTime initialDepositDate = convertedTime.toLocalDateTime();
//
//        Store store = storeRepository.findById(storeId).orElse(null);
//        Promotion promotion = promotionRepository.findById(promotionId).orElse(null);
//
//        //full payment or deposit
//        if (grandTotal > initialDepositAmount) {
//            fullyPaid = false;
//        } else {
//            fullyPaid = true;
//        }
//
//        //new sale
//        Sale newSale = new Sale(promotion, employeeId, store, grandTotal, initialDepositDate, initialDepositType, initialDepositAmount, fullyPaid);
//        customer.addSale(newSale);
//        newSale.setCustomer(customer);
//        saleRepository.save(newSale);
//
//        //saleDetails
//        JsonNode products = wholeJSON.get("products");
//        int itemList = wholeJSON.get("itemsSold").asInt();
//        String saleDetailList = "";
//
//        for (int i = 0; i < itemList; i++) {
//            String productId = wholeJSON.get("products").get(i).get("productId").asText();
//            int quantity = wholeJSON.get("products").get(i).get("quantity").asInt();
//            Product product = productRepository.findById(productId).orElse(null);
//            SaleDetail newSaleDetail = new SaleDetail(product, quantity);
//            newSale.addSaleDetail(newSaleDetail);
//            newSaleDetail.setSale(newSale);
//            saleDetailRepository.save(newSaleDetail);
//            saleDetailList += "\n" + newSaleDetail.toString();
//        }
//        return customer.toString() + "\n\n" + customer.getSale(newSale).toString() + "\n\n" + saleDetailList;
        return jsonString;
    }


    @GetMapping("/test")
    public String test(@RequestParam int id) {
        Customer customer = customerRepository.findById(id).orElse(null);
        Sale sale = saleRepository.findById(customer.getSaleList().get(0).getSaleId()).orElse(null);
        return customer.toString() + "\n\n" + customer.getSaleList().toString() + "\n\n" + sale.getSaleDetailList().toString();
    }

    @PostMapping(value = "/updateSale")
    public String updateSale(@RequestBody String jsonString) throws JsonProcessingException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode wholeJSON = objectMapper.readTree(jsonString);
        int saleId = wholeJSON.get("saleId").asInt();
        String zonePickUpTime = wholeJSON.get("pickUpDate").asText();

        ZonedDateTime zonedDateTime = ZonedDateTime.parse(zonePickUpTime, formatter);
        ZonedDateTime convertedTime = zonedDateTime.withZoneSameInstant(serverLocalTime);
        LocalDateTime localPickUpTime = convertedTime.toLocalDateTime();

        Sale sale = saleRepository.findById(saleId).orElse(null);
        if (sale.isFullyPaid()){
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
}

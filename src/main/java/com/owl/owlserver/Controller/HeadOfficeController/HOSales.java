package com.owl.owlserver.Controller.HeadOfficeController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.owl.owlserver.Serializer.SalesAllSerializer;
import com.owl.owlserver.model.Refund;
import com.owl.owlserver.model.Sale;
import com.owl.owlserver.model.Store;
import com.owl.owlserver.repositories.*;
import org.springframework.aop.AopInvocationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/hoSalesEndpoint")
public class HOSales {

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
    RefundRepository refundRepository;


    //REST endpoints
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String ping() {
        return "Head office api for sales, GET request received";
    }

    @GetMapping("/getAllSalesToday")
    public ArrayNode getAllSalesToday() throws JsonProcessingException {

        LocalDate localDate = LocalDate.now();
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Sale.class, new SalesAllSerializer());
        mapper.registerModule(module);

        List<Sale> saleList = saleRepository.getAllByInitialDepositDateIsBetweenOrderByInitialDepositDate(startOfDay, endOfDay);
        ArrayNode arrayNode = mapper.createArrayNode();

        for (Sale sale : saleList) {
            String x = mapper.writeValueAsString(sale);
            JsonNode jsonNode = mapper.readTree(x);
            arrayNode.add(jsonNode);
        }
        return arrayNode;
    }

    @GetMapping("/getAllRefundsForSpecificPeriod")
    public ArrayNode getAllRefundsForSpecificPeriod(String start, String end) {

        LocalDate localDateStart = LocalDate.parse(start);
        LocalDate localDateEnd = LocalDate.parse(end);

        LocalDateTime startPeriod = localDateStart.atStartOfDay();
        LocalDateTime endPeriod = localDateEnd.atTime(LocalTime.MAX);

        List<Refund> refundList = refundRepository.getAllByRefundDateIsBetween(startPeriod, endPeriod);
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();

        if (refundList==null){
            arrayNode.add("No refunds for specified time period");
            return arrayNode;
        }
        else {
            for (Refund refund : refundList) {
                ObjectNode jsonNode = mapper.createObjectNode();
                jsonNode.put("refundId",refund.getRefundId());
                jsonNode.put("refundDate",refund.getRefundDate().toString());
                jsonNode.put("refundRemarks",refund.getRemarks());
                jsonNode.put("sale",refund.getRefundDetails());
                System.out.println(refund.getRefundDetails());
                arrayNode.add(jsonNode);
            }
            return arrayNode;
        }
    }

    @GetMapping("/getAllSalesForSpecificPeriod")
    public ArrayNode getAllSalesForSpecificPeriod(String start, String end) throws JsonProcessingException {

        LocalDate localDateStart = LocalDate.parse(start);
        LocalDate localDateEnd = LocalDate.parse(end);

        LocalDateTime startPeriod = localDateStart.atStartOfDay();
        LocalDateTime endPeriod = localDateEnd.atTime(LocalTime.MAX);

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Sale.class, new SalesAllSerializer());
        mapper.registerModule(module);

        List<Sale> saleList = saleRepository.getAllByInitialDepositDateIsBetweenOrderByInitialDepositDate(startPeriod, endPeriod);
        ArrayNode arrayNode = mapper.createArrayNode();

        if (saleList==null){
            arrayNode.add("No sales for specified time period");
            return arrayNode;
        }
        else {
            for (Sale sale : saleList) {
                String x = mapper.writeValueAsString(sale);
                JsonNode jsonNode = mapper.readTree(x);
                arrayNode.add(jsonNode);
            }
            return arrayNode;
        }
    }

    @GetMapping("/getAllSalesTodayByStore")
    public ArrayNode getAllSalesTodayByStore() throws JsonProcessingException {

        LocalDate localDate = LocalDate.now();
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);

        ObjectMapper mapper = new ObjectMapper();

        List<Store> storeList = storeRepository.findAll();
        ArrayNode arrayNode = mapper.createArrayNode();

        for (Store store : storeList) {
            ObjectNode objectNode = mapper.valueToTree(store);
            try {
                int storeId = store.getStoreId();
                double total = storeRepository.totalStoreRevenue(storeId, startOfDay.toString(), endOfDay.toString());
                objectNode.put("totalRevenue", total);
                int saleCount = storeRepository.storeSaleCount(storeId, startOfDay.toString(), endOfDay.toString());
                objectNode.put("saleCount", saleCount);
            }
            catch (AopInvocationException error) {
                objectNode.put("totalRevenue", 0);
                objectNode.put("saleCount", 0);
            }
            arrayNode.add(objectNode);
        }

        return arrayNode;
    }

    @GetMapping("/getAllSalesForSpecificPeriodByStore")
    public ArrayNode getAllSalesForSpecificPeriodByStore(String start, String end) throws JsonProcessingException {

        LocalDate localDateStart = LocalDate.parse(start);
        LocalDate localDateEnd = LocalDate.parse(end);

        LocalDateTime startPeriod = localDateStart.atStartOfDay();
        LocalDateTime endPeriod = localDateEnd.atTime(LocalTime.MAX);

        ObjectMapper mapper = new ObjectMapper();

        List<Store> storeList = storeRepository.findAll();
        ArrayNode arrayNode = mapper.createArrayNode();

        for (Store store : storeList) {
            ObjectNode objectNode = mapper.valueToTree(store);
            try {
                int storeId = store.getStoreId();
                double total = storeRepository.totalStoreRevenue(storeId, startPeriod.toString(), endPeriod.toString());
                objectNode.put("totalRevenue", total);
                int saleCount = storeRepository.storeSaleCount(storeId, startPeriod.toString(), endPeriod.toString());
                objectNode.put("saleCount", saleCount);
            }
            catch (AopInvocationException error) {
                objectNode.put("totalRevenue", 0);
                objectNode.put("saleCount", 0);

            }
            arrayNode.add(objectNode);
        }

        return arrayNode;
    }
}
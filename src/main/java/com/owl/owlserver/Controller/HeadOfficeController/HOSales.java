package com.owl.owlserver.Controller.HeadOfficeController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.owl.owlserver.Serializer.SalesAllSerializer;
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
                double total = storeRepository.totalStoreRevenue(store.getStoreId(), startOfDay.toString(), endOfDay.toString());
                objectNode.put("totalRevenue", total);
            }
            catch (AopInvocationException error) {
                objectNode.put("totalRevenue", 0);
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
                double total = storeRepository.totalStoreRevenue(store.getStoreId(), startPeriod.toString(), endPeriod.toString());
                objectNode.put("totalRevenue", total);
            }
            catch (AopInvocationException error) {
                objectNode.put("totalRevenue", 0);
            }
            arrayNode.add(objectNode);
        }

        return arrayNode;
    }
}
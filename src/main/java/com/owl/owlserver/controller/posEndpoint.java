package com.owl.owlserver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.owl.owlserver.model.*;
import com.owl.owlserver.repositories.*;
import jdk.internal.org.objectweb.asm.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/posEndpoint")
public class posEndpoint {

    @Autowired CustomerRepository customerRepository;
    @Autowired EmployeeRepository employeeRepository;
    @Autowired ProductRepository productRepository;
    @Autowired PromotionRepository promotionRepository;
    @Autowired SaleDetailRepository saleDetailRepository;
    @Autowired SaleRepository saleRepository;
    @Autowired StoreQuantityRepository storeQuantityRepository;
    @Autowired StoreRepository storeRepository;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String ping(){
        return "Hello world! :), GET request successfully recieved";
    }

    @GetMapping("/getCustomerByName")
    public List<Customer> getCustomerByName(@RequestParam String firstName, String lastName){
        return customerRepository.findAllByFirstNameAndLastName(firstName,lastName);
    }

    @GetMapping({"/getStoreFrameQuantity","/getStoreLensQuantity"})
    public Product getStoreFrameQuantity(@RequestParam int storeId, String frameId){
        Store store = storeRepository.findById(storeId).orElse(null);
        int quantity = storeQuantityRepository.findByStoreAndProductId(store,frameId).getInstoreQuantity();
        Product product = productRepository.findById(frameId).orElse(null);
        product.setStoreQuantity(quantity);
        return product;
    }

    @GetMapping("/getCustomFrameList")
    public List<Product> getCustomFrameList(){
        return productRepository.findAllByProductIdStartsWith("cl");
    }

    @GetMapping("/getStorePromotions")
    public List<Promotion> getStorePromotions(@RequestParam int storeId){
        Store store = storeRepository.findById(storeId).orElse(null);
        return store.getPromotionList();
    }

    @GetMapping("/getStoreEmployees")
    public List<Employee> getStoreEmployees(@RequestParam int storeId){
        Store store = storeRepository.findById(storeId).orElse(null);
        return store.getEmployeesList();
    }

    @PostMapping(value = "/newSale")
    public String newSale(@RequestBody String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode wholeJSON = objectMapper.readTree(jsonString);
        JsonNode products = wholeJSON.get("products");
        JsonNode sale = wholeJSON.get("sale");
        int customerId = wholeJSON.get("customerId").asInt();
        int saleCount = wholeJSON.get("itemsSold").asInt();
        Customer customer;

        if(customerId==0){
            JsonNode customerJSON = wholeJSON.get("newCustomer");
            customer = objectMapper.treeToValue(customerJSON, Customer.class);
        }
        else {
            customer = customerRepository.findById(customerId).orElse(null);
        }
        customerRepository.save(customer);
        Sale newSale = objectMapper.treeToValue(sale,Sale.class);
        newSale.setCustomer(customer);
        saleRepository.save(newSale);

        return customer.toString()+newSale.toString();
    }

}

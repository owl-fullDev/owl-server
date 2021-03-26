package com.owl.owlserver.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.owl.owlserver.model.Customer;
import com.owl.owlserver.model.Sale;
import com.owl.owlserver.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;

@CrossOrigin
@RestController
@RequestMapping("/tapirRestApi")
public class Tapir {

    @Autowired
    CustomerRepository customerRepository;

    //REST endpoints
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String ping() {
        return "Why hello there";
    }

    @GetMapping("/i")
    public String i() {
        return "Happy birthday Ishnaaaaaaaaa\n\n" +
                "https://tenor.com/view/taylor-swift-22-lyrics-gif-12372675";
    }

    @GetMapping("/j")
    public String j() {
        return "E-GIRL FEGGATZ DETECTED";
    }

    @GetMapping("/p")
    public String p() {
        return "wassup lil bitch";
    }

    @GetMapping("/s")
    public String s() {
        return "apa lu buka2 sembarangan";
    }

    @GetMapping("/b")
    public String b() {
        return "Happy birthday broooooooooooo";
    }

    @PostMapping("/test")
    public String test(@RequestBody TestEntity testEntity) throws JsonProcessingException {
         return testEntity.toString();
    }

    @PostMapping("/test2")
    public String test2(@RequestBody String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode wholeJSON = objectMapper.readTree(jsonString);
        JsonNode testJson = wholeJSON.get("testEntity");

        try {
            TestEntity testEntity = objectMapper.treeToValue(testJson,TestEntity.class);
            return testEntity.toString();
        }
        catch (Exception e){
            return e.toString();
        }
    }

    @Transactional
    @PostMapping(value = "/newSale")
    public String newSale(@RequestBody String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode wholeJSON = objectMapper.readTree(jsonString);

        //new customer or existing customer validation
        int customerId = wholeJSON.get("customerId").asInt();
        Customer customer;
        if (customerId == 0) {
            JsonNode customerJSON = wholeJSON.get("newCustomer");
            customer = objectMapper.treeToValue(customerJSON, Customer.class);
        } else {
            customer = customerRepository.findById(customerId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No customer with specified ID exist"));
        }

        //sale deserialization
        JsonNode sale = wholeJSON.get("sale");
        Sale newSale = objectMapper.treeToValue(sale,Sale.class);
        return newSale.toString();
    }
}
//    //to set custom field in deserializing
//    @GetMapping("/getProduct")
//    public String getProduct(@RequestParam String productId){
//        Product product = productService.getById(productId);
//          JsonNode jsonNode = objectMapper.convertValue(product, JsonNode.class);
//        ((ObjectNode)jsonNode).put("quantity",storeQuantity.getInstoreQuantity());
//        return node.toString();
//    }

    //to throw proper errors:
//    orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No Sale with specified ID exists"));


package com.owl.owlserver.Controller.HeadOfficeController;

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

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/hoStoresEndpoint")
public class HOStores {

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
        return "Head office api for stores, GET request received";
    }

    @GetMapping("/getAllStores")
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    @GetMapping("/getStore")
    public Store getStore(int storeId) {
        Store store = storeRepository.findById(storeId).orElse(null);
        if (store==null){
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Store doesnt Exist!");
        }
        else {
            return store;
        }
    }

    @GetMapping("/getStorePromotions")
    public List<Promotion> getStorePromotions(int storeId) {
        Store store = storeRepository.findById(storeId).orElse(null);
        if (store==null){
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Store doesnt Exist!");
        }
        else {
            return store.getPromotionList();
        }
    }

    @GetMapping("/getStoreEmployees")
    public List<Employee> getStoreEmployees(int storeId) {
        Store store = storeRepository.findById(storeId).orElse(null);
        if (store==null){
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Store doesnt Exist!");
        }
        else {
            return store.getEmployeesList();
        }
    }

    @GetMapping("/getStoreQuantity")
    public List<StoreQuantity> getStoreQuantity(int storeId) {
        Store store = storeRepository.findById(storeId).orElse(null);
        if (store==null){
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Store doesnt Exist!");
        }
        else {
            return store.getStoreQuantityList();
        }
    }

    @PostMapping("/addStore")
    public ResponseEntity<String> addStore(@RequestBody String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode wholeJSON = objectMapper.readTree(jsonString);
        String name = wholeJSON.get("name").asText();
        String address = wholeJSON.get("address").asText();
        String city = wholeJSON.get("city").asText();
        String phoneNumber = wholeJSON.get("phoneNumber").asText();

        Store store = new Store(name,address,city,phoneNumber);
        storeRepository.saveAndFlush(store);
        return new ResponseEntity<>("successfully added new store:\n"+store.toString(),HttpStatus.CREATED);

    }

    @GetMapping("/deleteStore")
    public ResponseEntity<String> deleteStore(int storeId) {
        Store store = storeRepository.findById(storeId).orElse(null);
        if (store==null){
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Store doesnt Exist!");
        }
        else {
            String address = store.getAddress();
            storeRepository.deleteById(storeId);
            return new ResponseEntity<>("successfully deleted store:\n"+address,HttpStatus.OK);
        }
    }


    @GetMapping("/addOneStoreProduct")
    public ResponseEntity<String> addOneStoreProduct(int storeId, String productId) {
        Store store = storeRepository.findById(storeId).orElse(null);
        Product product = productRepository.findById(productId).orElse(null);
        if (store==null){
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Store doesnt Exist!");
        }
        else if (product==null){
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Product doesnt Exist!");
        }
        else {
            StoreQuantity storeQuantity = new StoreQuantity(store,product,0);
            storeQuantityRepository.save(storeQuantity);
            store.addStoreQuantity(storeQuantity);
            storeRepository.save(store);
            return new ResponseEntity<>("successfully added product: "+product.getProductName()+" into store: "+store.getAddress(),HttpStatus.OK);
        }
    }

    @GetMapping("/removeOneStoreProduct")
    public ResponseEntity<String> removeOneStoreProduct(int storeId, String productId) {
        Store store = storeRepository.findById(storeId).orElse(null);
        StoreQuantity storeQuantity = storeQuantityRepository.findByStoreAndProduct_ProductId(store,productId);
        Product product = productRepository.findById(productId).orElse(null);
        if (store==null){
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Store doesnt Exist!");
        }
        else if (storeQuantity==null){
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Product not in this store!");
        }
        else if (storeQuantity.getInstoreQuantity()!=0){
            throw new ResponseStatusException(HttpStatus.valueOf(400), "There still exists product in stock in the store!");
        }
        else {
            storeQuantity.setStore(null);
            storeQuantityRepository.save(storeQuantity);
            store.removeStoreQuantity(storeQuantity);
            storeRepository.save(store);
            return new ResponseEntity<>("successfully removed product: "+product.getProductName()+" from store: "+store.getAddress(),HttpStatus.OK);
        }
    }

}
package com.owl.owlserver.Controller.HeadOfficeController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.owl.owlserver.model.*;
import com.owl.owlserver.repositories.*;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@PreAuthorize("hasRole('OFFICE') or hasRole('ADMIN')")
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
    public ResponseEntity<String> ping() {
        return new ResponseEntity<>("Head office api for stores, GET request received",HttpStatus.OK);
    }

    @GetMapping("/getAllStores")
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    @GetMapping("/getStore")
    public Store getStore(int storeId) {
        return storeRepository.findById(storeId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No store with ID of :" + storeId + " exists"));
    }

    @GetMapping("/getStorePromotions")
    public List<Promotion> getStorePromotions(int storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No store with ID of :" + storeId + " exists"));
        return store.getPromotionList();
    }

    @GetMapping("/getStoreEmployees")
    public List<Employee> getStoreEmployees(int storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No store with ID of :" + storeId + " exists"));
        return store.getEmployeesList();
    }

    @GetMapping("/getStoreQuantity")
    public List<StoreQuantity> getStoreQuantity(int storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No store with ID of :" + storeId + " exists"));
        return store.getStoreQuantityList();
    }

    @PostMapping("/updateSetStoreQuantity")
    public ResponseEntity<String> updateSetStoreQuantity(@RequestBody String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode wholeJSON = objectMapper.readTree(jsonString);

        int storeId = wholeJSON.get("storeId").asInt();
        String productId = wholeJSON.get("productId").asText();
        int setQuantity = wholeJSON.get("setQuantity").asInt();

        Store store = storeRepository.findById(storeId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No store with ID of :" + storeId + " exists"));
        StoreQuantity storeQuantity = storeQuantityRepository.findByStore_StoreIdAndProduct_ProductId(storeId,productId);
        if (storeQuantity==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no such product in store at store");
        }
        storeQuantity.setSetQuantity(setQuantity);
        storeQuantityRepository.save(storeQuantity);
        return new ResponseEntity<>("successfully update set store quantity to "+setQuantity,HttpStatus.OK);
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
        storeRepository.save(store);
        return new ResponseEntity<>("successfully added new store",HttpStatus.CREATED);
    }

    @GetMapping("/addOneStoreProduct")
    public ResponseEntity<String> addOneStoreProduct(int storeId, String productId) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No store with ID of :" + storeId + " exists"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No product with ID of :" + productId + " exists"));

        StoreQuantity storeQuantity = new StoreQuantity(store, product, 0, 0);
        storeQuantityRepository.save(storeQuantity);
        store.addStoreQuantity(storeQuantity);
        storeRepository.save(store);
        return new ResponseEntity<>("successfully added product listing of product: " + product.getProductName() + " into store: " + store.getAddress(), HttpStatus.OK);
    }

    @GetMapping("/removeOneStoreProduct")
    public ResponseEntity<String> removeOneStoreProduct(int storeId, String productId) {
        Store store = storeRepository.findById(storeId).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, "No product with ID of :"+storeId+" exists"));
        StoreQuantity storeQuantity = storeQuantityRepository.findByStoreAndProduct_ProductId(store,productId);
        Product product = productRepository.findById(productId).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, "No product with ID of :"+productId+" exists"));

        if (storeQuantity==null){
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Product not in this store!");
        }
        else if (storeQuantity.getInstoreQuantity()!=0){
            throw new ResponseStatusException(HttpStatus.valueOf(400), "There still exists product in stock in the store!");
        }
        else {
            storeQuantityRepository.delete(storeQuantity);
            store.removeStoreQuantity(storeQuantity);
            storeRepository.save(store);
            return new ResponseEntity<>("successfully removed product: "+product.getProductName()+" from store: "+store.getAddress(),HttpStatus.OK);
        }
    }

}
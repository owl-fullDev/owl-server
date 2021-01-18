package com.owl.owlserver.Controller.HeadOfficeController;

import com.owl.owlserver.model.*;
import com.owl.owlserver.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.databind.JsonNode;

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

    @GetMapping("/getStoreLocation")
    public ResponseEntity<String> getStoreLocation(int storeId) {
        Store store = storeRepository.findById(storeId).orElse(null);
        if (store==null){
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Store doesnt Exist!");
        }
        else {
            return new ResponseEntity<>(store.getLocation(), HttpStatus.OK);
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

    @GetMapping("/addStore")
    public ResponseEntity<String> addStore(String location) {
        boolean alreadyExists = storeRepository.existsByLocation(location);
        if (alreadyExists){
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Store already Exists!");
        }
        else {
            Store store = new Store(location);
            storeRepository.saveAndFlush(store);
            return new ResponseEntity<>("successfully added new store:\n"+store.toString(),HttpStatus.CREATED);
        }
    }

    @GetMapping("/deleteStore")
    public ResponseEntity<String> deleteStore(int storeId) {
        Store store = storeRepository.findById(storeId).orElse(null);
        if (store==null){
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Store doesnt Exist!");
        }
        else {
            String location = store.getLocation();
            storeRepository.deleteById(storeId);
            return new ResponseEntity<>("successfully deleted store:\n"+location,HttpStatus.OK);
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
            StoreQuantity storeQuantity = new StoreQuantity(store,productId,0);
            storeQuantityRepository.save(storeQuantity);
            store.addStoreQuantity(storeQuantity);
            storeRepository.save(store);
            return new ResponseEntity<>("successfully added product: "+product.getProductName()+" into store: "+store.getLocation(),HttpStatus.OK);
        }
    }

    @GetMapping("/removeOneStoreProduct")
    public ResponseEntity<String> removeOneStoreProduct(int storeId, String productId) {
        Store store = storeRepository.findById(storeId).orElse(null);
        StoreQuantity storeQuantity = storeQuantityRepository.findByStoreAndProductId(store,productId);
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
            return new ResponseEntity<>("successfully removed product: "+product.getProductName()+" from store: "+store.getLocation(),HttpStatus.OK);
        }
    }

}
package com.owl.owlserver.Controller.HeadOfficeController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.owl.owlserver.model.*;
import com.owl.owlserver.repositories.*;
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
@RequestMapping("/hoPromotionsEndpoint")
public class HOPromotions {

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
        return "Head office api for promotions, GET request received";
    }

    @GetMapping("/getAllPromotions")
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    @GetMapping("/getPromotionActiveStoreList")
    public List<Store> getPromotionActiveStoreList(int promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No promotion with ID of: "+promotionId+" exists"));
        return promotion.getStoreList();
    }

    @GetMapping("/setPromotionAllStores")
    public ResponseEntity<String> setPromotionAllStores(int promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No promotion with ID of: " + promotionId + " exists"));
        if (promotion.isActiveInAllStores()) {
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Promotion is already active in all stores!");
        }
        List<Store> storeList = storeRepository.findAll();
        for (Store store : storeList) {
            store.addPromotion(promotion);
            promotion.addStore(store);
        }
        promotion.setActiveInAllStores(true);
        storeRepository.saveAll(storeList);
        promotionRepository.save(promotion);
        return new ResponseEntity<>("successfully activated promotion " + promotion.getPromotionName() + " for all stores", HttpStatus.OK);
    }

    @GetMapping("/removePromotionAllStores")
    public ResponseEntity<String> removePromotionAllStores(int promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No promotion with ID of: " + promotionId + " exists"));
        List<Store> storeList = storeRepository.findAll();
        for (Store store : storeList) {
            store.removePromotion(promotion);
            promotion.removeStore(store);
        }
        promotion.setActiveInAllStores(false);
        storeRepository.saveAll(storeList);
        promotionRepository.save(promotion);
        return new ResponseEntity<>("successfully deactivated promotion " + promotion.getPromotionName() + " from all stores", HttpStatus.OK);
    }

    @GetMapping("/setPromotionOneStore")
    public ResponseEntity<String> setPromotionOneStore(int promotionId, int storeId) {
        Promotion promotion = promotionRepository.findById(promotionId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No promotion with ID of: " + promotionId + " exists"));
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with ID of: " + storeId + " exists"));
        if (promotion.getStoreList().contains(store)) {
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Promotion is already active in that store!");
        }
        store.addPromotion(promotion);
        promotion.addStore(store);
        storeRepository.save(store);
        promotionRepository.save(promotion);
        return new ResponseEntity<>("successfully activated promotion: " + promotion.getPromotionName() + " at store: " + store.getName(), HttpStatus.OK);
    }

    @GetMapping("/removePromotionOneStore")
    public ResponseEntity<String> removePromotionOneStore(int promotionId, int storeId) {
        Promotion promotion = promotionRepository.findById(promotionId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No promotion with ID of: " + promotionId + " exists"));
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with ID of: " + storeId + " exists"));
        if (!promotion.getStoreList().contains(store)) {
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Promotion is not active in that store!");
        }
        store.removePromotion(promotion);
        promotion.removeStore(store);
        storeRepository.save(store);
        promotionRepository.save(promotion);
        return new ResponseEntity<>("successfully deactivated promotion: " + promotion.getPromotionName() + " from store: " + store.getName(), HttpStatus.OK);
    }

    @PostMapping("/addPromotion")
    public ResponseEntity<String> addPromotion(@RequestBody String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode wholeJSON = objectMapper.readTree(jsonString);
        String promotionName = wholeJSON.get("promotionName").asText();
        int percentage = wholeJSON.get("percentage").asInt(0);

        boolean alreadyExists = promotionRepository.existsDistinctByPercentage(percentage);
        boolean alreadyExistsName = promotionRepository.existsDistinctByPromotionName(promotionName);

        if (alreadyExists||alreadyExistsName) {
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Promotion already Exists!");
        }
        else {
            Promotion promotion = new Promotion(percentage, promotionName);
            promotionRepository.saveAndFlush(promotion);
            return new ResponseEntity<>("successfully added new promotion:\n" + promotion.toString(), HttpStatus.CREATED);
        }
    }
}
package com.owl.owlserver.Controller.HeadOfficeController.ProductsEndpoint;

import com.owl.owlserver.model.Product;
import com.owl.owlserver.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/hoFramesEndpoint")
public class FramesEndpoint {

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
        return "Head office api for Frames, GET request received";
    }

    @GetMapping("/getAllFrames")
    public List<Product> getAllFrames() {
        return productRepository.findAllByProductIdStartsWith("F");
    }

//    @GetMapping("/deleteProduct")
//    public ResponseEntity<String> deleteProduct(int productId) {
//        Promotion promotion = promotionRepository.findById(promotionId).orElse(null);
//        if (promotion==null){
//            throw new ResponseStatusException(HttpStatus.valueOf(400), "Promotion doesnt Exist!");
//        }
//        else {
//            String promotionName = promotion.getPromotionName();
//            List<Store> storeList = storeRepository.findAll();
//            for (Store store:storeList){
//                store.removePromotion(promotion);
//            }
//            promotionRepository.deleteById(promotionId);
//            return new ResponseEntity<>("successfully deleted promotion: " + promotionName, HttpStatus.OK);
//        }
//    }
//
//    @GetMapping("/getPromotionActiveStoreList")
//    public List<Store> getPromotionActiveStoreList(int promotionId) {
//        Promotion promotion = promotionRepository.findById(promotionId).orElse(null);
//        if (promotion==null){
//            throw new ResponseStatusException(HttpStatus.valueOf(400), "Promotion doesnt Exist!");
//        }
//        else {
//            return promotion.getStoreList();
//        }
//    }
//
//    @GetMapping("/setPromotionAllStores")
//    public ResponseEntity<String> setPromotionAllStores(int promotionId) {
//        Promotion promotion = promotionRepository.findById(promotionId).orElse(null);
//        if (promotion==null){
//            throw new ResponseStatusException(HttpStatus.valueOf(400), "Promotion doesnt Exist!");
//        }
//        else if (promotion.isActiveInAllStores()){
//            throw new ResponseStatusException(HttpStatus.valueOf(400), "Promotion is already active in all stores!");
//        }
//        else {
//            List<Store> storeList = storeRepository.findAll();
//            for (Store store:storeList){
//                store.addPromotion(promotion);
//                promotion.addStore(store);
//            }
//            promotion.setActiveInAllStores(true);
//            storeRepository.saveAll(storeList);
//            promotionRepository.save(promotion);
//            return new ResponseEntity<>("successfully added promotion "+promotion.getPromotionName()+" to all stores", HttpStatus.OK);
//        }
//    }
//
//    @GetMapping("/removePromotionAllStores")
//    public ResponseEntity<String> removePromotionAllStores(int promotionId) {
//        Promotion promotion = promotionRepository.findById(promotionId).orElse(null);
//        if (promotion==null){
//            throw new ResponseStatusException(HttpStatus.valueOf(400), "Promotion doesnt Exist!");
//        }
//        else if (!promotion.isActiveInAllStores()){
//            throw new ResponseStatusException(HttpStatus.valueOf(400), "Promotion is not active in all stores!");
//        }
//        else {
//            List<Store> storeList = storeRepository.findAll();
//            for (Store store:storeList){
//                store.removePromotion(promotion);
//                promotion.removeStore(store);
//            }
//            promotion.setActiveInAllStores(false);
//            storeRepository.saveAll(storeList);
//            promotionRepository.save(promotion);
//            return new ResponseEntity<>("successfully removed promotion "+promotion.getPromotionName()+" from all stores", HttpStatus.OK);
//        }
//    }
//
//    @GetMapping("/setPromotionOneStore")
//    public ResponseEntity<String> setPromotionOneStore(int promotionId, int storeId) {
//        Promotion promotion = promotionRepository.findById(promotionId).orElse(null);
//        Store store = storeRepository.findById(storeId).orElse(null);
//        if (store==null){
//            throw new ResponseStatusException(HttpStatus.valueOf(400), "Store doesnt Exist!");
//        }
//        else if (promotion==null){
//            throw new ResponseStatusException(HttpStatus.valueOf(400), "Promotion doesnt Exist!");
//        }
//        else if (promotion.getStoreList().contains(store)){
//            throw new ResponseStatusException(HttpStatus.valueOf(400), "Promotion is already active in that store!");
//        }
//        else {
//            store.addPromotion(promotion);
//            promotion.addStore(store);
//            storeRepository.save(store);
//            promotionRepository.save(promotion);
//            return new ResponseEntity<>("successfully added promotion " + promotion.getPromotionName() + " from store " + store.getLocation(), HttpStatus.OK);
//        }
//    }
//
//    @GetMapping("/removePromotionOneStore")
//    public ResponseEntity<String> removePromotionOneStore(int promotionId, int storeId) {
//        Promotion promotion = promotionRepository.findById(promotionId).orElse(null);
//        Store store = storeRepository.findById(storeId).orElse(null);
//        if (store==null){
//            throw new ResponseStatusException(HttpStatus.valueOf(400), "Store doesnt Exist!");
//        }
//        else if (promotion==null){
//            throw new ResponseStatusException(HttpStatus.valueOf(400), "Promotion doesnt Exist!");
//        }
//        else if (!promotion.getStoreList().contains(store)){
//            throw new ResponseStatusException(HttpStatus.valueOf(400), "Promotion is not active in that store!");
//        }
//        else {
//            store.removePromotion(promotion);
//            promotion.removeStore(store);
//            storeRepository.save(store);
//            promotionRepository.save(promotion);
//            return new ResponseEntity<>("successfully removed promotion " + promotion.getPromotionName() + " from store " + store.getLocation(), HttpStatus.OK);
//        }
//    }

}
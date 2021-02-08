package com.owl.owlserver.Controller.HeadOfficeController.ProductsEndpoint;

import com.owl.owlserver.model.Product;
import com.owl.owlserver.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/hoProductsEndpoint")
public class HOProductEndpoint {

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
    ShipmentRepository shipmentRepository;
    @Autowired
    ShipmentDetailRepository shipmentDetailRepository;
    @Autowired
    WarehouseRepository warehouseRepository;
    @Autowired
    WarehouseQuantityRepository warehouseQuantityRepository;

    //REST endpoints
    @GetMapping
    public ResponseEntity<String> ping() {
        return new ResponseEntity<>("Head office api for Products, GET request received", HttpStatus.OK);
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> productList = productRepository.findAll();
        if (productList.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No products found");
        }
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }

    @GetMapping("/getAllFrames")
    public ResponseEntity<List<Product>> getAllFrames() {
        List<Product> productList = productRepository.findAllByProductIdStartsWith("F");
        if (productList.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No frames found");
        }
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }

    @GetMapping("/getAllLenses")
    public ResponseEntity<List<Product>> getAllLenses() {
        List<Product> productList = productRepository.findAllByProductIdStartsWith("L");
        if (productList.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No lenses found");
        }
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }

    @GetMapping("/getAllCustomLenses")
    public ResponseEntity<List<Product>> getAllCustomLenses() {
        List<Product> productList = productRepository.findAllByProductIdStartsWith("CL");
        if (productList.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No custom lenses found");
        }
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }
}
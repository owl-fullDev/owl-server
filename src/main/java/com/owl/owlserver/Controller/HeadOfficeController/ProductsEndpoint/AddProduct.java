package com.owl.owlserver.Controller.HeadOfficeController.ProductsEndpoint;

import com.owl.owlserver.model.Product;
import com.owl.owlserver.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/hoAddProduct")
public class AddProduct {

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
        return "Head office api for Adding Products, GET request received";
    }

    @GetMapping("/getAllProductTypes")
    public List<String> getAllProductTypes() {
        List<String> list = new ArrayList<>();
        list.add("str 1");
        list.add("str 2");
        return list;
    }

}
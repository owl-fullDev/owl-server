package com.owl.owlserver.Controller.HeadOfficeController.ProductsEndpoint;

import com.owl.owlserver.model.Product;
import com.owl.owlserver.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/hoLensesEndpoint")
public class LensesEndpoint {

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
        return "Head office api for Lenses, GET request received";
    }

    @GetMapping("/getAllLenses")
    public List<Product> getAllLenses() {
        return productRepository.findAllByProductIdStartsWith("L");
    }

}
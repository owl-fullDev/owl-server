package com.owl.owlserver.Controller.HeadOfficeController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.owl.owlserver.model.Employee;
import com.owl.owlserver.model.Product;
import com.owl.owlserver.model.Supplier;
import com.owl.owlserver.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/hoSuppliersEndpoint")
public class HOSuppliers {

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
    @Autowired
    SupplierRespository supplierRespository;

    //JACKSON object Mapper
    private static final ObjectMapper objectMapper = new ObjectMapper();


    //REST endpoints
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String ping() {
        return "Head office api for suppliers, GET request received";
    }

    @GetMapping("/getAllSuppliers")
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        List<Supplier> supplierList = supplierRespository.findAll();
        if (supplierList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No suppliers saves");
        }
        return new ResponseEntity<>(supplierList, HttpStatus.OK);
    }

    @PostMapping(value = "/addNewSupplier")
    public ResponseEntity<String> addNewSupplier(@RequestBody String jsonString) throws JsonProcessingException {
        JsonNode wholeJSON = objectMapper.readTree(jsonString);

        String supplierName = wholeJSON.get("supplierName").asText();
        String supplierAddress = wholeJSON.get("supplierAddress").asText();
        String supplierPhoneNumber = wholeJSON.get("supplierPhoneNumber").asText();
        String supplierEmail = wholeJSON.get("supplierEmail").asText();

        Supplier newSupplier = new Supplier(supplierName,supplierAddress,supplierPhoneNumber,supplierEmail);
        supplierRespository.saveAndFlush(newSupplier);

        return new ResponseEntity<>("Supplier has been added", HttpStatus.OK);
    }

}
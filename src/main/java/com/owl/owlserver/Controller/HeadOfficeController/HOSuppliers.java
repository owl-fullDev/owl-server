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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

//@PreAuthorize("hasRole('OFFICE') or hasRole('ADMIN')")
@CrossOrigin
@RestController
@RequestMapping("/hoSuppliersEndpoint")
public class HOSuppliers {

    //injecting repositories for database access
    @Autowired
    SupplierRespository supplierRespository;

    //REST endpoints
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String ping() {
        return "Head office api for suppliers, GET request received";
    }

    @GetMapping("/getAllSuppliers")
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        List<Supplier> supplierList = supplierRespository.findAll();
        return new ResponseEntity<>(supplierList, HttpStatus.OK);
    }

    @PostMapping(value = "/addNewSupplier")
    public ResponseEntity<String> addNewSupplier(@RequestBody String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
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
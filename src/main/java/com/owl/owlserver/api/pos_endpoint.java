package com.owl.owlserver.api;

import com.owl.owlserver.model.Customer;
import com.owl.owlserver.model.Sale;
import com.owl.owlserver.model.StoreInventory;
import com.owl.owlserver.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class pos_endpoint {

    @Autowired
    CustomerRepository customerRepository;

    @GetMapping
    public String testConnection(){
        return "connection sucess!";
    }

    @GetMapping("/testPersistence")
    public String testPersistence(){
        Customer customer = new Customer("testuser", "1", "0101010101", "", 0, 0.0, 0.0, 0, 0.0, "x", 0.0, 0.0, 0, 0.0, "x");
        Sale newsale = new Sale(2, 222.2, 1, 1, LocalDateTime.now(), "cash", 333.3, true);
        customer.addSale(newsale);
        try {
            customerRepository.save(customer);
            return "sucessfully added new user";
        }
        catch (Exception error){
            return error.toString();
        }
    }
}


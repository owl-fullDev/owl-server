package com.owl.owlserver.Controller.HeadOfficeController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.owl.owlserver.model.Employee;
import com.owl.owlserver.model.Promotion;
import com.owl.owlserver.model.Store;
import com.owl.owlserver.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/hoEmployeesEndpoint")
public class HOEmployees {

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
        return "Head office api for employees, GET request received";
    }

    @GetMapping("/getAllEmployees")
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @PostMapping("/addEmployee")
    public ResponseEntity<String> addEmployee(@RequestBody String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode wholeJSON = objectMapper.readTree(jsonString);
        String firstName = wholeJSON.get("firstName").asText();
        String lastName = wholeJSON.get("lastName").asText();
        String jobTitle = wholeJSON.get("jobTitle").asText();

        boolean alreadyExistsFirstName = employeeRepository.existsDistinctByFirstName(firstName);
        boolean alreadyExistsLastName = employeeRepository.existsDistinctByLastname(lastName);

        if (alreadyExistsFirstName||alreadyExistsFirstName) {
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Employee already Exists!");
        }
        else {
            Employee employee = new Employee(firstName, lastName, jobTitle);
            employeeRepository.saveAndFlush(employee);
            return new ResponseEntity<>("successfully added new employee:\n" + employee.toString(), HttpStatus.CREATED);
        }
    }


}
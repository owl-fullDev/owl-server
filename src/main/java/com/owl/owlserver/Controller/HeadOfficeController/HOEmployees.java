package com.owl.owlserver.Controller.HeadOfficeController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.owl.owlserver.model.Employee;
import com.owl.owlserver.model.Product;
import com.owl.owlserver.model.Store;
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
@RequestMapping("/hoEmployeesEndpoint")
public class HOEmployees {

    //injecting repositories for database access
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    StoreRepository storeRepository;

    //JACKSON object Mapper
    private static final ObjectMapper objectMapper = new ObjectMapper();

    //REST endpoints
    @GetMapping
    public ResponseEntity<String> ping() {
        return new ResponseEntity<>("Head office api for employees, GET request received", HttpStatus.OK);
    }

    @GetMapping("/getAllEmployees")
    public ResponseEntity<ArrayNode> getAllEmployees() {
        List<Employee> employeeList = employeeRepository.findAll();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for (Employee employee: employeeList){
            JsonNode jsonNode = objectMapper.convertValue(employee, JsonNode.class);
            if (employee.getStore()!=null) {
                ((ObjectNode) jsonNode).put("storeId", employee.getStore().getStoreId());
                ((ObjectNode) jsonNode).put("storeName", employee.getStore().getName());
            }
            arrayNode.add(jsonNode);
        }
        return new ResponseEntity<>(arrayNode, HttpStatus.OK);
    }

    @PostMapping("/addNewEmployee")
    public ResponseEntity<String> addEmployee(@RequestBody String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode wholeJSON = objectMapper.readTree(jsonString);
        String firstName = wholeJSON.get("firstName").asText();
        String lastName = wholeJSON.get("lastName").asText();
        String jobTitle = wholeJSON.get("jobTitle").asText();
        String phoneNumber = wholeJSON.get("phoneNumber").asText();
        String email = wholeJSON.get("email").asText();
        int storeId = wholeJSON.get("storeId").asInt();

        Store store = storeRepository.findById(storeId).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, "No store with specified ID exists"));
        boolean alreadyExists = employeeRepository.existsByFirstNameAndLastname(firstName, lastName);

        if (alreadyExists) {
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Employee already Exists!");
        }
        else {
            Employee employee = new Employee(firstName, lastName, jobTitle, phoneNumber, email, store);
            employeeRepository.saveAndFlush(employee);
            return new ResponseEntity<>("successfully added new employee:\n" + employee.toString(), HttpStatus.CREATED);
        }
    }

    @PostMapping("/modifyEmployee")
    public ResponseEntity<String> modifyEmployee(@RequestBody String jsonString) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode wholeJSON = objectMapper.readTree(jsonString);
        int employeeId = wholeJSON.get("employeeId").asInt();
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, "No employee with specified ID exists"));

        String jobTitle = wholeJSON.get("jobTitle").asText();
        String email = wholeJSON.get("email").asText();
        String phoneNumber = wholeJSON.get("phoneNumber").asText();
        employee.setJobTitle(jobTitle);
        employee.setEmail(email);
        employee.setPhoneNumber(phoneNumber);

        int storeId = wholeJSON.get("storeId").asInt();
        if (!(storeId==0)){
            Store store = storeRepository.findById(storeId).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, "No store with specified ID exists"));
            employee.setStore(store);
        }
        else {
            employee.setStore(null);
        }

        employeeRepository.saveAndFlush(employee);
        return new ResponseEntity<>("Successfully modified employee details", HttpStatus.OK);
    }

}
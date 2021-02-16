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
        return "Head office api for employees, GET request received";
    }

    @GetMapping("/getAllEmployees")
    public ResponseEntity<ArrayNode> getAllEmployees() {
        List<Employee> employeeList = employeeRepository.findAll();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        if (employeeList.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No employees exists");
        }
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

        boolean alreadyExists = employeeRepository.existsByFirstNameAndLastname(firstName, lastName);

        if (alreadyExists) {
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Employee already Exists!");
        }
        else {
            Employee employee = new Employee(firstName, lastName, jobTitle);
            employeeRepository.saveAndFlush(employee);
            return new ResponseEntity<>("successfully added new employee:\n" + employee.toString(), HttpStatus.CREATED);
        }
    }

    @PostMapping("/modifyEmployee")
    public ResponseEntity<String> modifyEmployee(@RequestBody String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode wholeJSON = objectMapper.readTree(jsonString);
        int employeeId = wholeJSON.get("employeeId").asInt();
        String jobTitle = wholeJSON.get("jobTitle").asText();
        String email = wholeJSON.get("email").asText();
        String phoneNumber = wholeJSON.get("phoneNumber").asText();
        int storeId = wholeJSON.get("storeId").asInt();

        Employee employee = employeeRepository.findById(employeeId).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, "No employee with specified ID exists"));

        if (!jobTitle.equals("")){
            employee.setJobTitle(jobTitle);
        }
        if (!email.equals("")){
            employee.setEmail(email);
        }
        if (!phoneNumber.equals("")){
            employee.setPhoneNumber(phoneNumber);
        }
        if (!(storeId==0)){
            Store store = storeRepository.findById(storeId).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, "No store with specified ID exists"));
            employee.setStore(store);
        }
        employeeRepository.saveAndFlush(employee);
        return new ResponseEntity<>("Successfully modified employee details", HttpStatus.OK);
    }

}
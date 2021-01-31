package com.owl.owlserver.Controller.HeadOfficeController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.owl.owlserver.Serializer.RestockShipmentSerializer;
import com.owl.owlserver.model.*;
import com.owl.owlserver.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/hoRestockShipmentsEndpoint")
public class HORestockShipment {

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
    RestockShipmentRepository restockShipmentRepository;
    @Autowired
    RestockShipmentDetailRepository restockShipmentDetailRepository;
    @Autowired
    WarehouseRepository warehouseRepository;
    @Autowired
    WarehouseQuantityRepository warehouseQuantityRepository;

    //REST endpoints
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String ping() {
        return "Head office api for restocking shipments, GET request received";
    }

    @GetMapping("/getAllRestockShipments")
    public ArrayNode getAllRestockShipments() throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(RestockShipment.class, new RestockShipmentSerializer());
        mapper.registerModule(module);

        List<RestockShipment> restockShipmentList = restockShipmentRepository.findAllByReceivedTimestampIsNull();
        ArrayNode arrayNode = mapper.createArrayNode();

        if (restockShipmentList == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There are currently no active restock shipments");
        } else {
            for (RestockShipment restockShipment : restockShipmentList) {
                String x = mapper.writeValueAsString(restockShipment);
                JsonNode jsonNode = mapper.readTree(x);
                arrayNode.add(jsonNode);
            }
            return arrayNode;
        }
    }

    @GetMapping("/checkProductId")
    public ResponseEntity<String> checkProductId(String productId) throws JsonProcessingException {
        Product product = productRepository.findById(productId).orElse(null);
        if (product==null){
            return new ResponseEntity<>("Product ID invalid",HttpStatus.NOT_FOUND);
        }
        else{
            return new ResponseEntity<>("Product found ",HttpStatus.OK);
        }
    }


    @GetMapping("/checkWarehouseQuantity")
    public ResponseEntity<Boolean> checkWarehouseQuantity(int warehouseId, String productId, int quantity) throws JsonProcessingException {
        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElse(null);
        if (warehouse == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no warehouse with specified ID");
        }
        if (warehouseQuantityRepository.findByProductId(productId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no product with specified ID in stock in warehouse");
        }
        if (warehouseQuantityRepository.findByProductId(productId).getInWarehouseQuantity() < quantity) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is not enough quantity of product with specified ID in warehouse");
        }
        return new ResponseEntity<>(true,HttpStatus.OK);
    }

    @PostMapping("/addRestockShipment")
    public ResponseEntity<String> addPromotion(@RequestBody String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode wholeJSON = objectMapper.readTree(jsonString);
        int warehouseId = wholeJSON.get("warehouseId").asInt();
        int storeId = wholeJSON.get("storeId").asInt();
        int productCount = wholeJSON.get("productCount").asInt();

        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElse(null);
        if (warehouse == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no warehouse with specified ID");
        }

        Store store = storeRepository.findById(storeId).orElse(null);
        if (store == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no store with specified ID");
        }

        RestockShipment restockShipment = new RestockShipment(warehouse, store);
        restockShipmentRepository.save(restockShipment);

        //restockShipmentDetails
        JsonNode products = wholeJSON.get("products");
        for (int i = 0; i < productCount; i++) {
            String productId = products.get(i).get("productId").asText();
            int quantity = products.get(i).get("quantity").asInt();

            Product product = productRepository.findById(productId).orElse(null);
            if (warehouseQuantityRepository.findByProductId(productId) == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no product with specified ID in stock in warehouse");
            }
            else if (warehouseQuantityRepository.findByProductId(productId).getInWarehouseQuantity() < quantity) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is not enough quantity of product with specified ID in warehouse, Requested quantity: "+quantity+" Current Quantity in warehouse: "+warehouseQuantityRepository.findByProductId(productId).getInWarehouseQuantity());
            }
            else {
                WarehouseQuantity warehouseQuantity = warehouseQuantityRepository.findByWarehouseAndProductId(warehouse, productId);
                warehouseQuantity.setInWarehouseQuantity(warehouseQuantity.getInWarehouseQuantity() - quantity);
                warehouseQuantityRepository.saveAndFlush(warehouseQuantity);
                RestockShipmentDetail restockShipmentDetail = new RestockShipmentDetail(restockShipment, product, quantity);
                restockShipment.addRestockShipmentDetail(restockShipmentDetail);
                restockShipmentDetailRepository.saveAndFlush(restockShipmentDetail);
            }
        }
        restockShipmentRepository.saveAndFlush(restockShipment);
        return new ResponseEntity<>("successfully created new Restock Shipment:\n" + restockShipment.toString(), HttpStatus.CREATED);
    }

}
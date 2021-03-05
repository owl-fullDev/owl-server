package com.owl.owlserver.Controller.HeadOfficeController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.owl.owlserver.model.*;
import com.owl.owlserver.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/hoShipmentsEndpoint")
public class HOShipments {

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
    ShipmentDetailRepository ShipmentDetailRepository;
    @Autowired
    WarehouseRepository warehouseRepository;
    @Autowired
    WarehouseQuantityRepository warehouseQuantityRepository;
    @Autowired
    SupplierRespository supplierRespository;

    //REST endpoints
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String ping() {
        return "Head office api for shipments, GET request received";
    }

    @GetMapping("/getAllActiveShipments")
    public ResponseEntity<ArrayNode> getAllActiveShipments(){

        ObjectMapper mapper = new ObjectMapper();
        List<Shipment> shipmentList = shipmentRepository.findAllByReceivedTimestampIsNull();
        ArrayNode arrayNode = mapper.createArrayNode();

        if (shipmentList == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND ,"There are currently no active shipments");
        }
        else {
            for (Shipment shipment : shipmentList) {
                JsonNode jsonNode = mapper.convertValue(shipment, JsonNode.class);

                //find origin type
                if (shipment.getOriginType()==1) {
                    Supplier supplier = supplierRespository.findById(shipment.getOriginId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No supplier with ID of: "+shipment.getOriginId()+" exists!"));
                    ((ObjectNode) jsonNode).put("originType", "Supplier");
                    ((ObjectNode) jsonNode).put("originName", supplier.getName());
                    if (shipment.getSendTimestamp()==null){
                        ((ObjectNode) jsonNode).put("status", "This is a supplier shipment");
                    }
                }
                else if (shipment.getOriginType()==2) {
                    Warehouse warehouse = warehouseRepository.findById(shipment.getOriginId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No warehouse with ID of: "+shipment.getOriginId()+" exists!"));
                    ((ObjectNode) jsonNode).put("originType", "Warehouse");
                    ((ObjectNode) jsonNode).put("originName", warehouse.getName());
                    if (shipment.getSendTimestamp()==null){
                        ((ObjectNode) jsonNode).put("status", "Shipment has not left origin warehouse");
                    }
                }
                else if (shipment.getOriginType()==3) {
                    Store store = storeRepository.findById(shipment.getOriginId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with ID of: "+shipment.getOriginId()+" exists!"));
                    ((ObjectNode) jsonNode).put("originType", "store");
                    ((ObjectNode) jsonNode).put("originName", store.getName());
                    ((ObjectNode) jsonNode).put("status", "Shipment has not left origin store");
                }

                //find destination type
                if (shipment.getDestinationType()==1) {
                    Supplier supplier = supplierRespository.findById(shipment.getOriginId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No supplier with ID of: "+shipment.getOriginId()+" exists!"));
                    ((ObjectNode) jsonNode).put("destinationType", "Supplier");
                    ((ObjectNode) jsonNode).put("destinationName", supplier.getName());
                }
                else if (shipment.getDestinationType()==2) {
                    ((ObjectNode) jsonNode).put("destinationType", "Warehouse");
                    Warehouse warehouse = warehouseRepository.findById(shipment.getOriginId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No warehouse with ID of: "+shipment.getOriginId()+" exists!"));
                    ((ObjectNode) jsonNode).put("destinationName", warehouse.getName());
                }
                else if (shipment.getDestinationType()==3) {
                    ((ObjectNode) jsonNode).put("destinationType", "Store");
                    Store store = storeRepository.findById(shipment.getDestinationId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with ID of: "+shipment.getOriginId()+" exists!"));
                    ((ObjectNode) jsonNode).put("destinationName", store.getName());
                }

                arrayNode.add(jsonNode);
            }
            return new ResponseEntity<>(arrayNode, HttpStatus.OK);
        }
    }

    @GetMapping("/getReceivedShipmentsPeriod")
    public ResponseEntity<ArrayNode> getReceivedSupplierShipmentsPeriod(String start, String end, boolean isSupplier){

        LocalDate localDateStart = LocalDate.parse(start);
        LocalDate localDateEnd = LocalDate.parse(end);
        LocalDateTime startPeriod = localDateStart.atStartOfDay();
        LocalDateTime endPeriod = localDateEnd.atTime(LocalTime.MAX);


        ObjectMapper mapper = new ObjectMapper();
        List<Shipment> shipmentList;
        if (isSupplier) {
            shipmentList = shipmentRepository.findAllByReceivedTimestampIsNotNullAndReceivedTimestampIsBetweenAndOriginTypeIsOrderByReceivedTimestamp(startPeriod, endPeriod, 1);
        }
        else {
            shipmentList = shipmentRepository.findAllByReceivedTimestampIsNotNullAndReceivedTimestampIsBetweenAndOriginTypeIsNotOrderByReceivedTimestamp(startPeriod, endPeriod, 1);
        }
        ArrayNode arrayNode = mapper.createArrayNode();

        if (shipmentList == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND ,"There are no received supplier shipments for specified date rage");
        }
        else {
            for (Shipment shipment : shipmentList) {
                JsonNode jsonNode = mapper.convertValue(shipment, JsonNode.class);

                //find origin type
                if (shipment.getOriginType()==1) {
                    Supplier supplier = supplierRespository.findById(shipment.getOriginId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No supplier with ID of: "+shipment.getOriginId()+" exists!"));
                    ((ObjectNode) jsonNode).put("originType", "Supplier");
                    ((ObjectNode) jsonNode).put("originName", supplier.getName());

                }
                else if (shipment.getOriginType()==2) {
                    Warehouse warehouse = warehouseRepository.findById(shipment.getOriginId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No warehouse with ID of: "+shipment.getOriginId()+" exists!"));
                    ((ObjectNode) jsonNode).put("originType", "Warehouse");
                    ((ObjectNode) jsonNode).put("originName", warehouse.getName());

                }
                else if (shipment.getOriginType()==3) {
                    Store store = storeRepository.findById(shipment.getOriginId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with ID of: "+shipment.getOriginId()+" exists!"));
                    ((ObjectNode) jsonNode).put("originType", "store");
                    ((ObjectNode) jsonNode).put("originName", store.getName());
                }

                //find destination type
                if (shipment.getDestinationType()==1) {
                    Supplier supplier = supplierRespository.findById(shipment.getOriginId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No supplier with ID of: "+shipment.getOriginId()+" exists!"));
                    ((ObjectNode) jsonNode).put("destinationType", "Supplier");
                    ((ObjectNode) jsonNode).put("destinationName", supplier.getName());
                    ((ObjectNode) jsonNode).put("sendTimestamp", shipment.getSendTimestamp().toString());
                    ((ObjectNode) jsonNode).put("receivedTimestamp", shipment.getReceivedTimestamp().toString());
                }
                else if (shipment.getDestinationType()==2) {
                    ((ObjectNode) jsonNode).put("destinationType", "Warehouse");
                    Warehouse warehouse = warehouseRepository.findById(shipment.getOriginId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No warehouse with ID of: "+shipment.getOriginId()+" exists!"));
                    ((ObjectNode) jsonNode).put("destinationName", warehouse.getName());
                    ((ObjectNode) jsonNode).put("sendTimestamp", shipment.getSendTimestamp().toString());
                    ((ObjectNode) jsonNode).put("receivedTimestamp", shipment.getReceivedTimestamp().toString());
                }
                else if (shipment.getDestinationType()==3) {
                    ((ObjectNode) jsonNode).put("destinationType", "Store");
                    Store store = storeRepository.findById(shipment.getDestinationId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with ID of: "+shipment.getOriginId()+" exists!"));
                    ((ObjectNode) jsonNode).put("destinationName", store.getName());
                    ((ObjectNode) jsonNode).put("sendTimestamp", shipment.getSendTimestamp().toString());
                    ((ObjectNode) jsonNode).put("receivedTimestamp", shipment.getReceivedTimestamp().toString());
                }

                arrayNode.add(jsonNode);
            }
            return new ResponseEntity<>(arrayNode, HttpStatus.OK);
        }
    }

    @GetMapping("/checkProductId")
    public ResponseEntity<String> checkProductId(String productId){
        Product product = productRepository.findById(productId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No product with ID of: "+productId+" exists!"));
        return new ResponseEntity<>("Product ID ok",HttpStatus.OK);
    }

    @GetMapping("/checkWarehouseQuantity")
    public ResponseEntity<String> checkWarehouseQuantity(int warehouseId, String productId, int quantity){
        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No warehouse with ID of: "+warehouseId+" exists!"));
        if (warehouseQuantityRepository.findByProductId(productId) == null) {
            return new ResponseEntity<>("There is no product with ID of: "+productId+" in stock in warehouse",HttpStatus.NOT_FOUND);
        }
        if (warehouseQuantityRepository.findByProductId(productId).getInWarehouseQuantity() < quantity) {
            return new ResponseEntity<>("There is not enough quantity of product with specified ID in stock in the warehouse, Requested quantity: "+quantity+" Current Quantity in warehouse: "+warehouseQuantityRepository.findByProductId(productId).getInWarehouseQuantity(),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Product in store with enough quantity",HttpStatus.OK);
    }

    @GetMapping("/checkStoreQuantity")
    public ResponseEntity<String> checkStoreQuantity(int storeId, String productId, int quantity){
        Store store = storeRepository.findById(storeId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with ID of: "+storeId+" exists!"));
        if (storeQuantityRepository.findByStoreAndProductId(store, productId) == null) {
            return new ResponseEntity<>("There is no product with ID of: "+productId+" in stock in store",HttpStatus.NOT_FOUND);
        }
        if (storeQuantityRepository.findByStoreAndProductId(store, productId).getInstoreQuantity() < quantity) {
            return new ResponseEntity<>("There is not enough quantity of product with specified ID in stock in the store, Requested quantity: "+quantity+" Current Quantity in store: "+storeQuantityRepository.findByStoreAndProductId(store, productId).getInstoreQuantity(),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Product in store with enough quantity",HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/addShipment")
    public ResponseEntity<String> addPromotion(@RequestBody String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode wholeJSON = objectMapper.readTree(jsonString);
        int originType = wholeJSON.get("originType").asInt();
        int originId = wholeJSON.get("originId").asInt();
        int destinationType = wholeJSON.get("destinationType").asInt();
        int destinationId = wholeJSON.get("destinationId").asInt();
        int productCount = wholeJSON.get("productCount").asInt();

        //input checking origin type
        if (originType==1) {
            Supplier supplier = supplierRespository.findById(originId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No supplier with ID of: "+originId+" exists!"));
        }
        else if (originType==2) {
            Warehouse warehouse = warehouseRepository.findById(originId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No warehouse with ID of: "+originId+" exists!"));
        }
        else if (originType==3) {
            Store store = storeRepository.findById(originId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with ID of: "+originId+" exists!"));
        }

        //input checking destination type
        if (destinationType==1) {
            Supplier supplier = supplierRespository.findById(destinationId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No supplier with ID of: "+destinationId+" exists!"));
        }
        else if (destinationType==2) {
            Warehouse warehouse = warehouseRepository.findById(destinationId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No warehouse with ID of: "+destinationId+" exists!"));
        }
        else if (destinationType==3) {
            Store store = storeRepository.findById(destinationId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with ID of: "+destinationId+" exists!"));
        }

        Shipment shipment = new Shipment(originType, destinationType, originId, destinationId);
        shipment.setSendTimestamp(LocalDateTime.now());
        shipmentRepository.save(shipment);

        //ShipmentDetails
        JsonNode products = wholeJSON.get("products");
        for (int i = 0; i < productCount; i++) {
            String productId = products.get(i).get("productId").asText();
            int quantity = products.get(i).get("quantity").asInt();

            Product product = productRepository.findById(productId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No product with ID of: "+productId+" exists!"));

            //if origin is a supplier
            if (originType==1){
                ShipmentDetail shipmentDetail = new ShipmentDetail(shipment, product, quantity);
                shipment.addShipmentDetail(shipmentDetail);
                ShipmentDetailRepository.save(shipmentDetail);
            }

            //if origin is a warehouse
            else if (originType==2) {
                if (warehouseQuantityRepository.findByProductId(productId) == null) {
                    return new ResponseEntity<>("There is no product with specified ID in stock in warehouse", HttpStatus.NOT_FOUND);
                }
                else if (warehouseQuantityRepository.findByProductId(productId).getInWarehouseQuantity() < quantity) {
                    return new ResponseEntity<>("There is not enough quantity of product with specified ID in warehouse, Requested quantity: " + quantity + " Current Quantity in warehouse: " + warehouseQuantityRepository.findByProductId(productId).getInWarehouseQuantity(), HttpStatus.BAD_REQUEST);
                }
                else {
                    WarehouseQuantity warehouseQuantity = warehouseQuantityRepository.findByWarehouseWarehouseIdAndProductId(originId, productId);
                    warehouseQuantity.setInWarehouseQuantity(warehouseQuantity.getInWarehouseQuantity() - quantity);
                    warehouseQuantityRepository.save(warehouseQuantity);
                    ShipmentDetail shipmentDetail = new ShipmentDetail(shipment, product, quantity);
                    shipment.addShipmentDetail(shipmentDetail);
                    ShipmentDetailRepository.save(shipmentDetail);
                }
            }

            //if origin is a store
            else if (originType==3) {
                if (storeQuantityRepository.findByStoreStoreIdAndProductId(originId, productId) == null) {
                    return new ResponseEntity<>("There is no product with specified ID in stock in store", HttpStatus.NOT_FOUND);
                }
                else if (storeQuantityRepository.findByStoreStoreIdAndProductId(originId, productId).getInstoreQuantity() < quantity) {
                    return new ResponseEntity<>("There is not enough quantity of product with specified ID in store, Requested quantity: " + quantity + " Current Quantity in warehouse: " + storeQuantityRepository.findByStoreStoreIdAndProductId(originId, productId).getInstoreQuantity(), HttpStatus.BAD_REQUEST);
                }
                else {
                    StoreQuantity storeQuantity = storeQuantityRepository.findByStoreStoreIdAndProductId(originId, productId);
                    storeQuantity.setInstoreQuantity(storeQuantity.getInstoreQuantity() - quantity);
                    storeQuantityRepository.save(storeQuantity);
                    ShipmentDetail shipmentDetail = new ShipmentDetail(shipment, product, quantity);
                    shipment.addShipmentDetail(shipmentDetail);
                    ShipmentDetailRepository.save(shipmentDetail);
                }
            }
        }
        shipmentRepository.saveAndFlush(shipment);
        return new ResponseEntity<>("successfully created new Shipment", HttpStatus.CREATED);
    }


}
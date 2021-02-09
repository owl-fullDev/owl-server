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

    @GetMapping("/getAllShipments")
    public ResponseEntity<ArrayNode> getAllShipments(){

        ObjectMapper mapper = new ObjectMapper();
        List<Shipment> shipmentList = shipmentRepository.findAllByReceivedTimestampIsNull();
        ArrayNode arrayNode = mapper.createArrayNode();

        if (shipmentList == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND ,"There are currently no active  shipments");
        }
        else {
            for (Shipment shipment : shipmentList) {
                JsonNode jsonNode = mapper.convertValue(shipment, JsonNode.class);

                //find origin type
                if (shipment.getOriginType()==1) {
                    Supplier supplier = supplierRespository.findById(shipment.getOriginId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No supplier with ID of: "+shipment.getOriginId()+" exists!"));
                    ((ObjectNode) jsonNode).put("originSupplierName", supplier.getName());
                }
                else if (shipment.getOriginType()==2) {
                    Warehouse warehouse = warehouseRepository.findById(shipment.getOriginId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No warehouse with ID of: "+shipment.getOriginId()+" exists!"));
                    ((ObjectNode) jsonNode).put("originWarehouseName", warehouse.getName());
                }
                else if (shipment.getOriginType()==3) {
                    Store store = storeRepository.findById(shipment.getOriginId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with ID of: "+shipment.getOriginId()+" exists!"));
                    ((ObjectNode) jsonNode).put("originStoreName", store.getName());
                }

                //find destination type
                if (shipment.getDestinationType()==1) {
                    Supplier supplier = supplierRespository.findById(shipment.getOriginId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No supplier with ID of: "+shipment.getOriginId()+" exists!"));
                    ((ObjectNode) jsonNode).put("destinationSupplierName", supplier.getName());
                }
                else if (shipment.getDestinationType()==2) {
                    Warehouse warehouse = warehouseRepository.findById(shipment.getOriginId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No warehouse with ID of: "+shipment.getOriginId()+" exists!"));
                    ((ObjectNode) jsonNode).put("destinationWarehouseName", warehouse.getName());
                }
                else if (shipment.getDestinationType()==3) {
                    Store store = storeRepository.findById(shipment.getOriginId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with ID of: "+shipment.getOriginId()+" exists!"));
                    ((ObjectNode) jsonNode).put("destinationStoreName", store.getName());
                }

                arrayNode.add(jsonNode);
            }
            return new ResponseEntity<>(arrayNode, HttpStatus.OK);
        }
    }

    @GetMapping("/checkProductId")
    public ResponseEntity<String> checkProductId(String productId){
        Product product = productRepository.findById(productId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No product with ID of: "+productId+" exists!"));
        return new ResponseEntity<>("Product found ",HttpStatus.OK);
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
            Supplier supplier = supplierRespository.findById(originId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No supplier with ID of: "+originId+" exists!"));
        }
        else if (destinationType==2) {
            Warehouse warehouse = warehouseRepository.findById(originId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No warehouse with ID of: "+originId+" exists!"));
        }
        else if (destinationType==3) {
            Store store = storeRepository.findById(originId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with ID of: "+originId+" exists!"));
        }

        Shipment shipment = new Shipment(originType, destinationType, originId, destinationId);
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
        return new ResponseEntity<>("successfully created new Shipment:\n" + shipment.toString(), HttpStatus.CREATED);
    }

}
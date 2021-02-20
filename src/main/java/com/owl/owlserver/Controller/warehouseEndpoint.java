package com.owl.owlserver.Controller;

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

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import java.time.*;
        import java.time.format.DateTimeFormatter;
        import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/warehouseEndpoint")
public class warehouseEndpoint {

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

    //Time settings
    final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    final ZoneId serverLocalTime = ZoneId.systemDefault();

    //REST endpoints
    @GetMapping
    public ResponseEntity<String> ping() {
        return new ResponseEntity<>("This is the end point for warehouses, GET request successfully received", HttpStatus.OK);
    }

    @GetMapping("/getAllSupplierShipments")
    public ResponseEntity<ArrayNode> getAllSupplierShipments() throws JsonProcessingException {
        List<Shipment> shipmentList = shipmentRepository.findAllByReceivedTimestampIsNullAndOriginTypeEqualsAndDestinationTypeEquals(1,2);
        ArrayNode arrayNode = objectMapper.createArrayNode();

        if (shipmentList.isEmpty()){
            return new ResponseEntity<>(arrayNode, HttpStatus.OK);
        }

        for (Shipment shipment: shipmentList){
            JsonNode jsonNode = objectMapper.convertValue(shipment, JsonNode.class);
            Supplier supplier = supplierRespository.findById(shipment.getOriginId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No supplier with ID of: "+shipment.getOriginId()+" exists!"));
            ((ObjectNode) jsonNode).put("supplierName", supplier.getName());
            ((ObjectNode) jsonNode).put("supplierAddress", supplier.getAddress());
            ((ObjectNode) jsonNode).put("sendTimestamp", shipment.getSendTimestamp().toString());
            arrayNode.add(jsonNode);
        }

        return new ResponseEntity<>(arrayNode, HttpStatus.OK);
    }

    @GetMapping("/getAllWarehouseAndStoreShipments")
    public ResponseEntity<ArrayNode> getAllWarehouseAndStoreShipments() throws JsonProcessingException {
        List<Shipment> shipmentList = shipmentRepository.findAllByReceivedTimestampIsNullAndOriginTypeIsNotAndDestinationTypeIs(1, 2);

        ArrayNode arrayNode = objectMapper.createArrayNode();

        if (shipmentList.isEmpty()){
            return new ResponseEntity<>(arrayNode, HttpStatus.OK);
        }

        for (Shipment shipment: shipmentList){
            JsonNode jsonNode = objectMapper.convertValue(shipment, JsonNode.class);

            if (shipment.getOriginType()==2) {
                Warehouse warehouse = warehouseRepository.findById(shipment.getOriginId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No warehouse with ID of: " + shipment.getOriginId() + " exists!"));
                ((ObjectNode) jsonNode).put("originType", "warehouse");
                ((ObjectNode) jsonNode).put("originName", warehouse.getName());
                ((ObjectNode) jsonNode).put("originAddress", warehouse.getAddress());
                ((ObjectNode) jsonNode).put("sendTimestamp", shipment.getSendTimestamp().toString());
                arrayNode.add(jsonNode);
            }

            else if (shipment.getOriginType()==3){
                Store store = storeRepository.findById(shipment.getOriginId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with ID of: " + shipment.getOriginId() + " exists!"));
                ((ObjectNode) jsonNode).put("originType", "store");
                ((ObjectNode) jsonNode).put("originName", store.getName());
                ((ObjectNode) jsonNode).put("originAddress", store.getAddress());
                ((ObjectNode) jsonNode).put("sendTimestamp", shipment.getSendTimestamp().toString());
                arrayNode.add(jsonNode);
            }
        }
        return new ResponseEntity<>(arrayNode, HttpStatus.OK);
    }

    @PostMapping("/receiveShipment")
    public ResponseEntity<String> receiveShipment(@RequestBody String jsonString) throws JsonProcessingException {
        JsonNode wholeJSON = objectMapper.readTree(jsonString);

        int shipmentId = wholeJSON.get("shipmentId").asInt();
        Shipment shipment = shipmentRepository.findById(shipmentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No shipment with specified ID exists"));

        if (shipment.getDestinationType()!=2){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This shipment is not meant for a warehouse!");
        }

        int warehouseId = wholeJSON.get("warehouseId").asInt();
        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No warehouse with specified ID exists"));

        if (shipment.getReceivedTimestamp() != null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This shipment has already been received");
        }

        if (shipment.getDestinationId() != warehouseId) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This shipment is not meant for this warehouse!");
        }

        String zonePickUpTime = wholeJSON.get("receivedDate").asText();
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(zonePickUpTime, formatter);
        ZonedDateTime convertedTime = zonedDateTime.withZoneSameInstant(serverLocalTime);
        LocalDateTime localPickUpTime = convertedTime.toLocalDateTime();
        shipment.setReceivedTimestamp(localPickUpTime);

        //ShipmentDetails
        JsonNode receivedQuantityList = wholeJSON.get("receivedQuantityList");
        List<ShipmentDetail> shipmentDetailList = shipment.getShipmentDetailList();
        for (int i = 0; i < shipmentDetailList.size(); i++) {
            ShipmentDetail shipmentDetail = shipmentDetailList.get(i);
            if (shipmentDetail.getShipmentDetailId()!=receivedQuantityList.get(i).get("shipmentDetailId").asInt()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This shipment detail ID does not match expected shipmentDetailId, Expected: "+shipmentDetail.getShipmentDetailId()+", Received:"+receivedQuantityList.get(i).get("shipmentDetailId").asInt());
            }
            int receivedQuantity = receivedQuantityList.get(i).get("receivedQuantity").asInt();
            shipmentDetail.setReceivedQuantity(receivedQuantity);
            String comment = receivedQuantityList.get(i).get("comment").asText();
            shipmentDetail.setComment(comment);
            shipmentDetailRepository.save(shipmentDetail);

            WarehouseQuantity warehouseQuantity = warehouseQuantityRepository.findByWarehouseWarehouseIdAndProductId(warehouseId, shipmentDetail.getProduct().getProductId());
            warehouseQuantity.setInWarehouseQuantity(warehouseQuantity.getInWarehouseQuantity()+receivedQuantity);
            warehouseQuantityRepository.save(warehouseQuantity);
        }

        shipmentRepository.saveAndFlush(shipment);
        return new ResponseEntity<>("Shipment received by warehouse!", HttpStatus.OK);
    }
}
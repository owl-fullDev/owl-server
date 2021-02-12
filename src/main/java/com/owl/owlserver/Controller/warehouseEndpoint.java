package com.owl.owlserver.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    //JACKSON object Mapper
    private static final ObjectMapper objectMapper = new ObjectMapper();

    //Time settings
    final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    final ZoneId serverLocalTime = ZoneId.systemDefault();

    //REST endpoints
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> ping() {
        return new ResponseEntity<>("Hello world! :), GET request successfully received", HttpStatus.OK);
    }

    @PostMapping(value = "/receiveShipment")
    public ResponseEntity<String> receiveShipment(@RequestBody String jsonString) throws JsonProcessingException {
        JsonNode wholeJSON = objectMapper.readTree(jsonString);

        int ShipmentId = wholeJSON.get("ShipmentId").asInt();
        Shipment shipment = shipmentRepository.findById(ShipmentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No shipment with specified ID exists"));

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

        List<ShipmentDetail> shipmentDetailList = shipment.getShipmentDetailList();
        for (ShipmentDetail shipmentDetail : shipmentDetailList) {
            Product product = shipmentDetail.getProduct();
            int quantity = shipmentDetail.getQuantity();
            WarehouseQuantity warehouseQuantity = warehouseQuantityRepository.findByWarehouseWarehouseIdAndProductId(warehouseId, product.getProductId());

            //first time receiving product
            if (warehouseQuantity == null) {
                warehouseQuantity = new WarehouseQuantity(warehouse, product.getProductId(), quantity);
                warehouseQuantityRepository.save(warehouseQuantity);
            }
            else {
                warehouseQuantity.setInWarehouseQuantity(warehouseQuantity.getInWarehouseQuantity() + quantity);
                warehouseQuantityRepository.save(warehouseQuantity);
            }
        }

        shipment.setReceivedTimestamp(localPickUpTime);
        shipmentRepository.saveAndFlush(shipment);

        return new ResponseEntity<>("Shipment received by warehouse!", HttpStatus.OK);
    }
}
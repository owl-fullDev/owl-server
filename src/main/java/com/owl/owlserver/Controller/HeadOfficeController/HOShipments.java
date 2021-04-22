package com.owl.owlserver.Controller.HeadOfficeController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.owl.owlserver.DTO.ShipmentDTO;
import com.owl.owlserver.Service.ShipmentService;
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
    ProductRepository productRepository;
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


    //injecting services
    @Autowired
    private ShipmentService shipmentService;

    //REST endpoints
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String ping() {
        return "Head office api for shipments, GET request received";
    }

    @GetMapping("/getAllActiveShipments")
    public List<ShipmentDTO> getAllActiveShipments() {
        List<Shipment> shipmentList = shipmentRepository.findAllByReceivedTimestampIsNull();
        return shipmentService.shipmentToDTO(shipmentList);
    }

    @GetMapping("/getReceivedShipmentsPeriod")
    public List<ShipmentDTO> getReceivedSupplierShipmentsPeriod(String start, String end, boolean isSupplier){

        LocalDate localDateStart = LocalDate.parse(start);
        LocalDate localDateEnd = LocalDate.parse(end);
        LocalDateTime startPeriod = localDateStart.atStartOfDay();
        LocalDateTime endPeriod = localDateEnd.atTime(LocalTime.MAX);

        List<Shipment> shipmentList;
        if (isSupplier) {
            shipmentList = shipmentRepository.findAllByReceivedTimestampIsNotNullAndReceivedTimestampIsBetweenAndOriginTypeIsOrderByReceivedTimestamp(startPeriod, endPeriod, 1);
        }
        else {
            shipmentList = shipmentRepository.findAllByReceivedTimestampIsNotNullAndReceivedTimestampIsBetweenAndOriginTypeIsNotOrderByReceivedTimestamp(startPeriod, endPeriod, 1);
        }

        return shipmentService.shipmentToDTO(shipmentList);
    }

    @GetMapping("/checkProductId")
    public ResponseEntity<String> checkProductId(String productId){
        Product product = productRepository.findById(productId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No product with ID of: "+productId+" exists!"));
        return new ResponseEntity<>("Product ID ok",HttpStatus.OK);
    }

    @GetMapping("/checkWarehouseQuantity")
    public ResponseEntity<String> checkWarehouseQuantity(int warehouseId, String productId, int quantity){
        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No warehouse with ID of: "+warehouseId+" exists!"));
        if (warehouseQuantityRepository.findByProduct_ProductId(productId) == null) {
            return new ResponseEntity<>("There is no product with ID of: "+productId+" in stock in warehouse",HttpStatus.NOT_FOUND);
        }
        if (warehouseQuantityRepository.findByProduct_ProductId(productId).getInWarehouseQuantity() < quantity) {
            return new ResponseEntity<>("There is not enough quantity of product with specified ID in stock in the warehouse, Requested quantity: "+quantity+" Current Quantity in warehouse: "+warehouseQuantityRepository.findByProduct_ProductId(productId).getInWarehouseQuantity(),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Product in store with enough quantity",HttpStatus.OK);
    }

    @GetMapping("/checkStoreQuantity")
    public ResponseEntity<String> checkStoreQuantity(int storeId, String productId, int quantity){
        Store store = storeRepository.findById(storeId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with ID of: "+storeId+" exists!"));
        if (storeQuantityRepository.findByStoreAndProduct_ProductId(store, productId) == null) {
            return new ResponseEntity<>("There is no product with ID of: "+productId+" in stock in store",HttpStatus.NOT_FOUND);
        }
        if (storeQuantityRepository.findByStoreAndProduct_ProductId(store, productId).getInstoreQuantity() < quantity) {
            return new ResponseEntity<>("There is not enough quantity of product with specified ID in stock in the store, Requested quantity: "+quantity+" Current Quantity in store: "+storeQuantityRepository.findByStoreAndProduct_ProductId(store, productId).getInstoreQuantity(),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Product in store with enough quantity",HttpStatus.OK);
    }

    @Transactional
    @PostMapping(value = "/addShipment")
    public ResponseEntity<String> addShipment(@RequestBody Shipment shipment) {
        if (shipment==null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All shipment details are empty!");
        }
        shipmentService.persistShipment(shipment);
        return new ResponseEntity<>("Successfully created new transfer shipment, ID: "+shipment.getShipmentId(), HttpStatus.OK);
    }

    @Transactional
    @PostMapping(value = "/cancelShipment")
    public ResponseEntity<String> cancelShipment(int shipmentId) {
        shipmentRepository.findById(shipmentId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No shipment with ID of: "+shipmentId+" exists!"));
        shipmentRepository.deleteById(shipmentId);
        return new ResponseEntity<>("Successfully deleted shipment, ID: "+shipmentId, HttpStatus.OK);
    }
}
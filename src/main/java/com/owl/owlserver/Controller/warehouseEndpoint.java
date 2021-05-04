package com.owl.owlserver.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.owl.owlserver.DTO.ShipmentDTO;
import com.owl.owlserver.Service.ShipmentService;
import com.owl.owlserver.model.*;
import com.owl.owlserver.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import java.time.*;
import java.util.List;

@CrossOrigin
@PreAuthorize("hasRole('warehouse') or hasRole('office') or hasRole('admin') or hasRole('boss')")
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

    @Autowired
    ShipmentService shipmentService;

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
    public List<ShipmentDTO> getAllSupplierShipments(@RequestParam int warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Warehouse with ID of: " +warehouseId+ " exists!"));
        List<Shipment> shipmentList = shipmentRepository.findAllByReceivedTimestampIsNullAndOriginTypeEqualsAndDestinationTypeEqualsAndDestinationId(1,2, warehouseId);
        return shipmentService.shipmentToDTO(shipmentList);
    }

    @GetMapping("/getAllWarehouseAndStoreShipments")
    public List<ShipmentDTO> getAllWarehouseAndStoreShipments(@RequestParam int warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Warehouse with ID of: " +warehouseId+ " exists!"));
        List<Shipment> shipmentList = shipmentRepository.findAllByReceivedTimestampIsNullAndOriginTypeIsNotAndDestinationTypeIsAndDestinationId(1, 2, warehouseId);
        return shipmentService.shipmentToDTO(shipmentList);
    }

    @GetMapping("/getAllOrderedShipments")
    public List<ShipmentDTO> getAllOrderedShipments(@RequestParam int warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Warehouse with ID of: " +warehouseId+ " exists!"));
        List<Shipment> shipmentList = shipmentRepository.findAllByReceivedTimestampIsNullAndSendTimestampIsNullAndOriginTypeIsAndOriginIdIs(2, warehouseId);
        return shipmentService.shipmentToDTO(shipmentList);
    }

    @PostMapping("/receiveShipment")
    public ResponseEntity<String> receiveShipment(@RequestBody String jsonString) throws JsonProcessingException {
        JsonNode wholeJSON = objectMapper.readTree(jsonString);
        int shipmentId = wholeJSON.get("shipmentId").asInt();
        Shipment shipment = shipmentRepository.findById(shipmentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No shipment with specified ID exists"));

        if (shipment.getOriginType() == 1) {
            shipmentService.receiveSupplierShipment(shipment, wholeJSON.get("receivedQuantityList"), wholeJSON.get("warehouseId").asInt() );
        }
        else if (shipment.getOriginType() == 2 || shipment.getOriginType() == 3){
            shipmentService.receiveInternalShipment(shipment, 2, wholeJSON.get("warehouseId").asInt() );
        }

        return new ResponseEntity<>("Shipment received by warehouse!", HttpStatus.OK);
    }

    @PostMapping("/sendShipment")
    public ResponseEntity<String> sendShipment(@RequestBody String jsonString) throws JsonProcessingException {
        JsonNode wholeJSON = objectMapper.readTree(jsonString);
        int shipmentId = wholeJSON.get("shipmentId").asInt();
        Shipment shipment = shipmentRepository.findById(shipmentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No shipment with specified ID exists"));

        shipment.setSendTimestamp(LocalDateTime.now());
        shipmentRepository.save(shipment);
        return new ResponseEntity<>("Shipment has been sent", HttpStatus.OK);
    }
}
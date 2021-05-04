package com.owl.owlserver.Controller.HeadOfficeController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.owl.owlserver.model.Store;
import com.owl.owlserver.model.StoreQuantity;
import com.owl.owlserver.model.Warehouse;
import com.owl.owlserver.model.WarehouseQuantity;
import com.owl.owlserver.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@PreAuthorize("hasRole('office') or hasRole('admin') or hasRole('boss')")
@CrossOrigin
@RestController
@RequestMapping("/hoWarehousesEndpoint")
public class HOWarehouses {

    //injecting repositories for database access
    @Autowired
    WarehouseRepository warehouseRepository;
    @Autowired
    WarehouseQuantityRepository warehouseQuantityRepository;

    //REST endpoints
    @GetMapping
    public ResponseEntity<String> ping() {
        return new ResponseEntity<>("Head office api for warehouses, GET request received",HttpStatus.OK);
    }

    @GetMapping("/getAllWarehouses")
    public List<Warehouse> getAllWarehouses(){
        return warehouseRepository.findAll();
    }

    @PostMapping("/addWarehouse")
    public ResponseEntity<String> addWarehouse(@RequestBody String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode wholeJSON = objectMapper.readTree(jsonString);

        String name = wholeJSON.get("name").asText();
        String address = wholeJSON.get("address").asText();
        String phoneNumber = wholeJSON.get("phoneNumber").asText();

        Warehouse warehouse = new Warehouse(name,address,phoneNumber);
        warehouseRepository.save(warehouse);
        return new ResponseEntity<>("successfully added new warehouse",HttpStatus.CREATED);
    }

    @GetMapping("/getWarehouseQuantity")
    public List<WarehouseQuantity> getWarehouseQuantity(int warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, "No warehouse with ID of: "+warehouseId+" exists!"));
        return warehouse.getWarehouseQuantityList();
    }
}

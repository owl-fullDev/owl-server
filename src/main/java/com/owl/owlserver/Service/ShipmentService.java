package com.owl.owlserver.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.owl.owlserver.DTO.ShipmentDTO;
import com.owl.owlserver.model.*;
import com.owl.owlserver.repositories.*;
import io.vavr.Tuple;
import io.vavr.Tuple3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Service
public class ShipmentService {

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

    public void persistShipment(Shipment shipment) {

        int originType = shipment.getOriginType();
        int originId = shipment.getOriginId();
        int destinationType = shipment.getDestinationType();
        int destinationId = shipment.getDestinationId();

        //input checking origin type and id
        if (originType < 1 || originType > 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Origin type not acceptable!, must be between 1 and 3, value received: " + originType);
        }
        else if (originType == 1) {
            Supplier supplier = supplierRespository.findById(originId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Origin Error: No supplier with ID of: " + originId + " exists!"));
        }
        else if (originType == 2) {
            Warehouse warehouse = warehouseRepository.findById(originId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Origin Error: No warehouse with ID of: " + originId + " exists!"));
        }
        else {
            Store store = storeRepository.findById(originId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Origin Error: No store with ID of: " + originId + " exists!"));
        }

        //input checking destination type and id
        if (destinationType < 2 || destinationType > 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Destination type not acceptable!, must be between 3 and 3, value received: " + destinationType);
        }
        else if (destinationType == 2) {
            Warehouse warehouse = warehouseRepository.findById(destinationId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Origin Error: No warehouse with ID of: " + destinationId + " exists!"));
        }
        else {
            Store store = storeRepository.findById(destinationId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Origin Error: No store with ID of: " + destinationId + " exists!"));
        }

        //extracts all product Ids form shipmentDetailList, stream is a for loop, foreach
        List<String> productIds = emptyIfNull(shipment.getShipmentDetailList()).stream()
                //extracts productId from shipmentDetail array
                .map(ShipmentDetail::getProductId)
                //collect appends each extracted productId into the List
                .collect(Collectors.toList());

        //validation for product IDs
        List<String> validProductIds = productRepository.findProductIdByProductIdIn(productIds);

        if (productIds.size() != validProductIds.size()) {
            Set<String> result = productIds.stream()
                    .distinct()
                    .filter(id -> !validProductIds.contains(id))
                    .collect(Collectors.toSet());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The following product IDs do not exist!: [" + result.toString() + "]");
        }

        List<ShipmentDetail> shipmentDetailList = shipment.getShipmentDetailList();

        //Quantity check for Warehouse
        if (originType == 2) {
            List<WarehouseQuantity> warehouseQuantityList = warehouseQuantityRepository.findAllByProduct_ProductIdIn(validProductIds);
            for (int c = 0; c < warehouseQuantityList.size(); c++) {
                int available = warehouseQuantityList.get(c).getInWarehouseQuantity();
                int requested = shipmentDetailList.get(c).getQuantity();
                if (available < requested) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough product in stock in warehouse!, Requested quantity: " + requested + ", Available: " + available);
                }
                warehouseQuantityList.get(c).setInWarehouseQuantity(available - requested);
            }
            warehouseQuantityRepository.saveAll(warehouseQuantityList);
        }
        //Quantity check for Store
        else if (originType == 3) {
            List<StoreQuantity> storeQuantityList = new ArrayList<>();
            for (ShipmentDetail shipmentDetail : shipmentDetailList) {
                StoreQuantity storeQuantity = storeQuantityRepository.findByStore_StoreIdAndProduct_ProductId(originId, shipmentDetail.getProductId());
                storeQuantityList.add(storeQuantity);
                int available = storeQuantity.getInstoreQuantity();
                int requested = shipmentDetail.getQuantity();
                if (available < requested) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough product in stock in store!, Requested quantity: " + requested + ", Available: " + available);
                }
                storeQuantity.setInstoreQuantity(available - requested);
            }
            storeQuantityRepository.saveAll(storeQuantityList);
        }

        if (originType==1 || originType==3) {
            shipment.setSendTimestamp(LocalDateTime.now());
        }

        shipmentRepository.save(shipment);

        //for every shipmentDetail
        shipmentDetailRepository.saveAll(emptyIfNull(shipment.getShipmentDetailList()).stream()
                //peek means accessing the element in the array, MAP means transform/modify, peek means accesing
                .peek(shipmentDetail -> shipmentDetail.setProduct(new Product(shipmentDetail.getProductId())))
                .peek(shipmentDetail -> shipmentDetail.setShipment(shipment))
                .collect(Collectors.toList()));
    }

    public List<ShipmentDTO> shipmentToDTO(List<Shipment> shipmentList){
        Map<Integer, Supplier> suppliers = supplierRespository.findAll().stream().collect(Collectors.toMap(s -> s.getSupplierId(), Function.identity()));
        Map<Integer, Warehouse> warehouses = warehouseRepository.findAll().stream().collect(Collectors.toMap(w -> w.getWarehouseId(), Function.identity()));
        Map<Integer, Store> stores = storeRepository.findAll().stream().collect(Collectors.toMap(s -> s.getStoreId(), Function.identity()));

        BiFunction<Integer, Integer, Tuple3<String, String, String>> fun = (type, typeId) -> {

            switch (type) {
                case 1:
                    Supplier supplier = suppliers.get(typeId);
                    return Tuple.of("supplier", supplier.getName(), supplier.getAddress());

                case 2:
                    Warehouse warehouse = warehouses.get(typeId);
                    return Tuple.of("warehouse", warehouse.getName(), warehouse.getAddress());

                case 3:
                    Store store = stores.get(typeId);
                    return Tuple.of("store", store.getName(), store.getAddress());

                default:
                    throw new RuntimeException();
            }
        };

        return (shipmentList.stream()
                .map(shipment -> {
                    Tuple3<String, String, String> originDetail = fun.apply(shipment.getOriginType(), shipment.getOriginId());
                    Tuple3<String, String, String> destinationDetail = fun.apply(shipment.getDestinationType(), shipment.getDestinationId());

                    String status;
                    if (shipment.getSendTimestamp()!=null&&shipment.getReceivedTimestamp()==null){
                        status = "Shipment is being delivered";
                    }
                    else if (shipment.getSendTimestamp()==null){
                        status = "Shipment has not yet left origin";
                    }
                    else
                        status = "Shipment has been received by destination";

                    return ShipmentDTO.builder()
                            .shipmentId(shipment.getShipmentId())
                            .sendTimestamp(shipment.getSendTimestamp())
                            .receivedTimestamp(shipment.getReceivedTimestamp())
                            .originId(shipment.getOriginId())
                            .originType(originDetail._1)
                            .originName(originDetail._2)
                            .originAddress(originDetail._3)
                            .destinationId(shipment.getDestinationId())
                            .destinationType(destinationDetail._1)
                            .destinationName(destinationDetail._2)
                            .destinationAddress(destinationDetail._3)
                            .shipmentDetailList(shipment.getShipmentDetailList())
                            .status(status)
                            .build();
                })
                .collect(Collectors.toList()));
    }

    public void receiveSupplierShipment(Shipment shipment, JsonNode receivedQuantityList, int receivedDestinationId) {
        if (shipment.getDestinationId() != receivedDestinationId) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This shipment is not meant for this warehouse, the correct warehouse has ID of: " + shipment.getDestinationId());
        }
        if (shipment.getReceivedTimestamp() != null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This shipment has already been received");
        }
        if (shipment.getSendTimestamp() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This shipment has not left origin!");
        }

        Warehouse warehouse = warehouseRepository.findById(receivedDestinationId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No warehouse with specified ID exists"));

        //ShipmentDetails
        List<ShipmentDetail> shipmentDetailList = shipment.getShipmentDetailList();
        List<WarehouseQuantity> warehouseQuantityList = warehouseQuantityRepository.findAllByWarehouse_WarehouseId(warehouse.getWarehouseId());
        for (int i = 0; i < shipmentDetailList.size(); i++) {
            ShipmentDetail shipmentDetail = shipmentDetailList.get(i);

            if (shipmentDetail.getShipmentDetailId()!=receivedQuantityList.get(i).get("shipmentDetailId").asInt()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This shipment detail ID does not match expected shipmentDetailId, Expected: "+shipmentDetail.getShipmentDetailId()+", Received:"+receivedQuantityList.get(i).get("shipmentDetailId").asInt());
            }

            int receivedQuantity = receivedQuantityList.get(i).get("receivedQuantity").asInt();
            shipmentDetail.setReceivedQuantity(receivedQuantity);
            String comment = receivedQuantityList.get(i).get("comment").asText();
            shipmentDetail.setComment(comment);

            Product product = shipmentDetail.getProduct();
            WarehouseQuantity warehouseQuantity = warehouseQuantityList.stream()
                    .filter(warehouseQuantity1 -> warehouseQuantity1.getProduct().getProductId().equals(product.getProductId()))
                    .findFirst()
                    .orElse(null);

            //first time receiving product
            if(warehouseQuantity==null){
                warehouseQuantity = new WarehouseQuantity(warehouse,product,receivedQuantity);
            }
            else {
                warehouseQuantity.setInWarehouseQuantity(warehouseQuantity.getInWarehouseQuantity() + receivedQuantity);
            }
        }

        shipmentDetailRepository.saveAll(shipmentDetailList);
        warehouseQuantityRepository.saveAll(warehouseQuantityList);
        shipment.setReceivedTimestamp(LocalDateTime.now());
        shipmentRepository.save(shipment);
    }

    public void receiveInternalShipment(Shipment shipment, int receivedDestinationType, int receivedDestinationId) {

        if (shipment.getDestinationType() != receivedDestinationType) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This shipment is not meant for this type of destination!");
        }
        if (shipment.getDestinationId() != receivedDestinationId) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This shipment is not meant for this destination, the correct destination is" + shipment.getDestinationId());
        }
        if (shipment.getReceivedTimestamp() != null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This shipment has already been received");
        }
        if (shipment.getSendTimestamp() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This shipment has not left origin!");
        }

        int destinationType = shipment.getDestinationType();
        int destinationId = shipment.getDestinationId();

        if (destinationType == 2) {
            Warehouse destinationWarehouse = warehouseRepository.findById(destinationId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No warehouse with specified ID exists"));

            //receiving shipment in warehouse
            List<ShipmentDetail> shipmentDetailList = shipment.getShipmentDetailList();
            List<WarehouseQuantity> warehouseQuantityList = warehouseQuantityRepository.findAllByWarehouse_WarehouseId(destinationId);

            for (ShipmentDetail shipmentDetail : shipmentDetailList) {
                Product product = shipmentDetail.getProduct();
                int quantity = shipmentDetail.getQuantity();
                shipmentDetail.setReceivedQuantity(quantity);
                WarehouseQuantity warehouseQuantity = warehouseQuantityList.stream()
                        .filter(warehouseQuantity1 -> warehouseQuantity1.getProduct().getProductId().equals(product.getProductId()))
                        .findFirst()
                        .orElse(null);

                //first time receiving product
                if (warehouseQuantity == null) {
                    warehouseQuantity = new WarehouseQuantity(destinationWarehouse, product, quantity);
                    warehouseQuantityList.add(warehouseQuantity);
                    shipmentDetail.setReceivedQuantity(quantity);
                }
                else {
                    warehouseQuantity.setInWarehouseQuantity(warehouseQuantity.getInWarehouseQuantity() + quantity);
                    shipmentDetail.setReceivedQuantity(quantity);
                }
            }
            shipment.setReceivedTimestamp(LocalDateTime.now());
            shipmentDetailRepository.saveAll(shipmentDetailList);
            warehouseQuantityRepository.saveAll(warehouseQuantityList);
            shipmentRepository.save(shipment);
        }

        else if (destinationType == 3) {
            Store destinationStore = storeRepository.findById(destinationId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with specified ID exists"));

            //receiving shipment in store
            List<ShipmentDetail> shipmentDetailList = shipment.getShipmentDetailList();
            List<StoreQuantity> storeQuantityList = storeQuantityRepository.findAllByStore_StoreId(destinationStore.getStoreId());
            for (ShipmentDetail shipmentDetail : shipmentDetailList) {
                Product product = shipmentDetail.getProduct();
                int quantity = shipmentDetail.getQuantity();
                shipmentDetail.setReceivedQuantity(quantity);
                StoreQuantity storeQuantity = storeQuantityList.stream()
                        .filter(storeQuantity1 -> storeQuantity1.getProduct().getProductId().equals(product.getProductId()))
                        .findFirst()
                        .orElse(null);

                //first time receiving product
                if (storeQuantity == null) {
                    storeQuantity = new StoreQuantity(destinationStore, product, quantity, quantity);
                    storeQuantityList.add(storeQuantity);
                    shipmentDetail.setReceivedQuantity(quantity);
                } else {
                    storeQuantity.setInstoreQuantity(storeQuantity.getInstoreQuantity() + quantity);
                    shipmentDetail.setReceivedQuantity(quantity);
                }
            }
            shipment.setReceivedTimestamp(LocalDateTime.now());
            shipmentDetailRepository.saveAll(shipmentDetailList);
            storeQuantityRepository.saveAll(storeQuantityList);
            shipmentRepository.save(shipment);
        }

    }

    public ArrayNode getAllActiveShipments() {
        ObjectMapper mapper = new ObjectMapper();
        List<Shipment> shipmentList = shipmentRepository.findAllByReceivedTimestampIsNull();
        ArrayNode arrayNode = mapper.createArrayNode();

        if (shipmentList == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There are currently no active shipments");
        } else {
            List<Supplier> supplierList = supplierRespository.findAll();
            List<Warehouse> warehouseList = warehouseRepository.findAll();
            List<Store> storeList = storeRepository.findAll();

            for (Shipment shipment : shipmentList) {
                JsonNode jsonNode = mapper.convertValue(shipment, JsonNode.class);

                //find origin type
                if (shipment.getOriginType() == 1) {
                    Supplier supplier = supplierList.stream()
                            .filter(supplier1 -> supplier1.getSupplierId() == shipment.getOriginId())
                            .findFirst()
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No supplier with ID of: " + shipment.getOriginId() + " exists!"));
                    ((ObjectNode) jsonNode).put("originType", "Supplier");
                    ((ObjectNode) jsonNode).put("originName", supplier.getName());
                    ((ObjectNode) jsonNode).put("status", "This is a supplier shipment");
                } else if (shipment.getOriginType() == 2) {
                    Warehouse warehouse = warehouseList.stream()
                            .filter(warehouse1 -> warehouse1.getWarehouseId() == shipment.getOriginId())
                            .findFirst()
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No warehouse with ID of: " + shipment.getOriginId() + " exists!"));
                    ((ObjectNode) jsonNode).put("originType", "Warehouse");
                    ((ObjectNode) jsonNode).put("originName", warehouse.getName());
                    if (shipment.getSendTimestamp() == null) {
                        ((ObjectNode) jsonNode).put("status", "Shipment has not left origin warehouse");
                    }
                } else if (shipment.getOriginType() == 3) {
                    Store store = storeList.stream()
                            .filter(store1 -> store1.getStoreId() == shipment.getOriginId())
                            .findFirst()
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with ID of: " + shipment.getOriginId() + " exists!"));
                    ((ObjectNode) jsonNode).put("originType", "store");
                    ((ObjectNode) jsonNode).put("originName", store.getName());
                    ((ObjectNode) jsonNode).put("status", "Shipment has not left origin store");
                }

                //find destination type
                if (shipment.getDestinationType() == 1) {
                    Supplier supplier = supplierList.stream()
                            .filter(supplier1 -> supplier1.getSupplierId() == shipment.getDestinationId())
                            .findFirst()
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No supplier with ID of: " + shipment.getDestinationId() + " exists!"));
                    ((ObjectNode) jsonNode).put("destinationType", "Supplier");
                    ((ObjectNode) jsonNode).put("destinationName", supplier.getName());
                } else if (shipment.getDestinationType() == 2) {
                    ((ObjectNode) jsonNode).put("destinationType", "Warehouse");
                    Warehouse warehouse = warehouseList.stream()
                            .filter(warehouse1 -> warehouse1.getWarehouseId() == shipment.getDestinationId())
                            .findFirst()
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No warehouse with ID of: " + shipment.getDestinationId() + " exists!"));
                    ((ObjectNode) jsonNode).put("destinationName", warehouse.getName());
                } else if (shipment.getDestinationType() == 3) {
                    ((ObjectNode) jsonNode).put("destinationType", "Store");
                    Store store = storeList.stream()
                            .filter(store1 -> store1.getStoreId() == shipment.getDestinationId())
                            .findFirst()
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with ID of: " + shipment.getDestinationId() + " exists!"));
                    ((ObjectNode) jsonNode).put("destinationName", store.getName());
                }
                arrayNode.add(jsonNode);
            }
            return arrayNode;
        }
    }


    public ArrayNode getReceivedShipmentsPeriod(String start, String end, boolean isSupplier) {
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
            List<Supplier> supplierList = supplierRespository.findAll();
            List<Warehouse> warehouseList = warehouseRepository.findAll();
            List<Store> storeList = storeRepository.findAll();

            for (Shipment shipment : shipmentList) {
                JsonNode jsonNode = mapper.convertValue(shipment, JsonNode.class);

                //find origin type
                if (shipment.getOriginType()==1) {
                    Supplier supplier = supplierList.stream()
                            .filter(supplier1 -> supplier1.getSupplierId()==shipment.getOriginId())
                            .findFirst()
                            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No supplier with ID of: "+shipment.getOriginId()+" exists!"));
                    ((ObjectNode) jsonNode).put("originType", "Supplier");
                    ((ObjectNode) jsonNode).put("originName", supplier.getName());
                }
                else if (shipment.getOriginType()==2) {
                    Warehouse warehouse = warehouseList.stream()
                            .filter(warehouse1 -> warehouse1.getWarehouseId()==shipment.getOriginId())
                            .findFirst()
                            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No warehouse with ID of: "+shipment.getOriginId()+" exists!"));
                    ((ObjectNode) jsonNode).put("originType", "Warehouse");
                    ((ObjectNode) jsonNode).put("originName", warehouse.getName());

                }
                else if (shipment.getOriginType()==3) {
                    Store store = storeList.stream()
                            .filter(store1 -> store1.getStoreId()==shipment.getOriginId())
                            .findFirst()
                            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with ID of: "+shipment.getOriginId()+" exists!"));
                    ((ObjectNode) jsonNode).put("originType", "store");
                    ((ObjectNode) jsonNode).put("originName", store.getName());
                }

                //find destination type
                if (shipment.getDestinationType()==1) {
                    Supplier supplier = supplierList.stream()
                            .filter(supplier1 -> supplier1.getSupplierId()==shipment.getDestinationId())
                            .findFirst()
                            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No supplier with ID of: "+shipment.getDestinationId()+" exists!"));
                    ((ObjectNode) jsonNode).put("destinationType", "Supplier");
                    ((ObjectNode) jsonNode).put("destinationName", supplier.getName());
                    ((ObjectNode) jsonNode).put("sendTimestamp", shipment.getSendTimestamp().toString());
                    ((ObjectNode) jsonNode).put("receivedTimestamp", shipment.getReceivedTimestamp().toString());
                }
                else if (shipment.getDestinationType()==2) {
                    ((ObjectNode) jsonNode).put("destinationType", "Warehouse");
                    Warehouse warehouse = warehouseList.stream()
                            .filter(warehouse1 -> warehouse1.getWarehouseId()==shipment.getDestinationId())
                            .findFirst()
                            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No warehouse with ID of: "+shipment.getDestinationId()+" exists!"));
                    ((ObjectNode) jsonNode).put("destinationName", warehouse.getName());
                    ((ObjectNode) jsonNode).put("sendTimestamp", shipment.getSendTimestamp().toString());
                    ((ObjectNode) jsonNode).put("receivedTimestamp", shipment.getReceivedTimestamp().toString());
                }
                else if (shipment.getDestinationType()==3) {
                    ((ObjectNode) jsonNode).put("destinationType", "Store");
                    Store store = storeList.stream()
                            .filter(store1 -> store1.getStoreId()==shipment.getDestinationId())
                            .findFirst()
                            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No store with ID of: "+shipment.getDestinationId()+" exists!"));
                    ((ObjectNode) jsonNode).put("destinationName", store.getName());
                    ((ObjectNode) jsonNode).put("sendTimestamp", shipment.getSendTimestamp().toString());
                    ((ObjectNode) jsonNode).put("receivedTimestamp", shipment.getReceivedTimestamp().toString());
                }

                arrayNode.add(jsonNode);
            }
            return arrayNode;
        }
    }
}
package com.owl.owlserver.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.owl.owlserver.model.Product;
import com.owl.owlserver.model.Shipment;
import com.owl.owlserver.model.ShipmentDetail;
import com.owl.owlserver.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;

public class ShipmentDeserializer extends StdDeserializer<Shipment> {

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

    public ShipmentDeserializer() {
        this(null);
    }

    public ShipmentDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Shipment deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode wholeJson = jp.getCodec().readTree(jp);
        int originType = wholeJson.get("originType").asInt();
        int destinationType = wholeJson.get("destinationType").asInt();
        int originId = wholeJson.get("originId").asInt();
        int destinationId = wholeJson.get("destinationId").asInt();

        Shipment newShipment = new Shipment(originType, destinationType, originId, destinationId);
        shipmentRepository.save(newShipment);

        JsonNode shipmentDetailsArray = wholeJson.get("shipmentDetails");
        int shipmentDetailsArrayLength = shipmentDetailsArray.size();
        for (int c = 0; c < shipmentDetailsArrayLength; c++) {
            JsonNode node = shipmentDetailsArray.get(c);
            String productId = node.get("productId").asText();
            int quantity = node.get("quantity").asInt();
            Product product = productRepository.findById(productId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No product with ID of: " + productId + " exists!"));
            ShipmentDetail shipmentDetail = new ShipmentDetail(newShipment, product, quantity);
            shipmentDetailRepository.save(shipmentDetail);
        }

        return newShipment;
    }
}
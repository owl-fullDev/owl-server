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
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShipmentDeserializer extends StdDeserializer<Shipment> {

    //injecting repositories for database access
    @Autowired
    ProductRepository productRepository;

    public ShipmentDeserializer() {
        this(null);
    }

    public ShipmentDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Shipment deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        JsonNode wholeJson = jp.getCodec().readTree(jp);
        int originType = wholeJson.get("originType").asInt();
        int destinationType = wholeJson.get("destinationType").asInt();
        int originId = wholeJson.get("originId").asInt();
        int destinationId = wholeJson.get("destinationId").asInt();

        Shipment newShipment = new Shipment(originType, destinationType, originId, destinationId);

        JsonNode shipmentDetailsArray = wholeJson.get("shipmentDetails");
        int shipmentDetailsArrayLength = shipmentDetailsArray.size();
        List<String> productIdList = new ArrayList<>();
        for (int c = 0; c < shipmentDetailsArrayLength; c++) {
            productIdList.add(shipmentDetailsArray.get(c).get("productId").asText());
        }
        List<Product> productList = productRepository.findAllById(productIdList);
        List<ShipmentDetail> shipmentDetailList = newShipment.getShipmentDetailList();

        for (int c = 0; c < shipmentDetailsArrayLength; c++) {
            ShipmentDetail newShipmentDetail = new ShipmentDetail(newShipment,productList.get(c),shipmentDetailsArray.get(c).get("quantity").asInt());
            shipmentDetailList.add(newShipmentDetail);
        }

        return newShipment;
    }
}
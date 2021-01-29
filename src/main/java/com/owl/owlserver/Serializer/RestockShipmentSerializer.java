package com.owl.owlserver.Serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.owl.owlserver.model.RestockShipment;
import com.owl.owlserver.model.RestockShipmentDetail;
import com.owl.owlserver.model.Store;
import com.owl.owlserver.model.Warehouse;
import com.owl.owlserver.repositories.ProductRepository;
import com.owl.owlserver.repositories.StoreRepository;
import com.owl.owlserver.repositories.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class RestockShipmentSerializer extends StdSerializer<RestockShipment> {

    @Autowired
    StoreRepository storeRepository;
    @Autowired
    WarehouseRepository warehouseRepository;
    @Autowired
    ProductRepository productRepository;

    public RestockShipmentSerializer(){
        this(null);
    }

    public RestockShipmentSerializer(Class<RestockShipment> t) {
        super(t);
    }

    @Override
    public void serialize(RestockShipment restockShipment, JsonGenerator jgen, SerializerProvider provider) throws IOException {

        jgen.writeStartObject();
        jgen.writeNumberField("internalShipmentId", restockShipment.getRestockShipmentId());

        Warehouse warehouse = restockShipment.getWarehouse();
        jgen.writeNumberField("warehouseId", warehouse.getWarehouseId());
        jgen.writeStringField("warehouseName", warehouse.getName());

        Store store = restockShipment.getStore();
        jgen.writeNumberField("storeid", store.getStoreId());
        jgen.writeStringField("storeName", store.getName());

        //if shipment hasn't left origin
        if (restockShipment.getSendTimestamp()==null){
            jgen.writeStringField("dateSent", "-");
            jgen.writeStringField("dateReceived", "-");
            jgen.writeStringField("status", "Shipment hasn't left warehouse");
        }
        //if shipment is in progress
        else if (restockShipment.getSendTimestamp()!=null&& restockShipment.getReceivedTimestamp()==null){
            jgen.writeStringField("dateSent", restockShipment.getSendTimestamp().toString());
            jgen.writeStringField("dateReceived", "-");
            jgen.writeStringField("status", "Shipment in progress");
        }
        //if shipment has already been received
        else if (restockShipment.getReceivedTimestamp()!=null){
            jgen.writeStringField("dateSent", restockShipment.getSendTimestamp().toString());
            jgen.writeStringField("dateReceived", restockShipment.getReceivedTimestamp().toString());
            jgen.writeStringField("status", "Shipment has been delivered");
        }

        jgen.writeFieldName("internalShipmentDetails");
        jgen.writeStartArray();
        for (RestockShipmentDetail restockShipmentDetail : restockShipment.getRestockShipmentDetailList()){
            jgen.writeStartObject();
            jgen.writeFieldName("product");
            jgen.writeStartObject();
            jgen.writeStringField("productId", restockShipmentDetail.getProduct().getProductId());
            jgen.writeStringField("productName", restockShipmentDetail.getProduct().getProductName());
            jgen.writeNumberField("quantity", restockShipmentDetail.getQuantity());
            jgen.writeEndObject();
            jgen.writeEndObject();
        }
        jgen.writeEndArray();
        jgen.writeEndObject();
    }
}

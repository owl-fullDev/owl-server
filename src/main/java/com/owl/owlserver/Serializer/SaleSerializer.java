package com.owl.owlserver.Serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.owl.owlserver.model.Sale;
import com.owl.owlserver.model.SaleDetail;
import com.owl.owlserver.repositories.ProductRepository;
import com.owl.owlserver.repositories.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class SaleSerializer extends StdSerializer<Sale> {

    @Autowired
    ProductRepository productRepository;

    public SaleSerializer(){
        this(null);
    }

    public SaleSerializer(Class<Sale> t) {
        super(t);
    }

    @Override
    public void serialize(Sale sale, JsonGenerator jgen, SerializerProvider provider) throws IOException {

        jgen.writeStartObject();
        jgen.writeNumberField("saleId", sale.getSaleId());
        jgen.writeStringField("time", sale.getInitialDepositDate().toString());
        jgen.writeStringField("promotion", sale.getPromotion().getPromotionName());
        jgen.writeStringField("store", sale.getStore().getLocation());
        jgen.writeNumberField("revenue", sale.getGrandTotal());
        jgen.writeFieldName("saleDetails");
        jgen.writeStartArray();
        for (SaleDetail saleDetail:sale.getSaleDetailList()){
            jgen.writeStartObject();
            jgen.writeFieldName("product");
            jgen.writeStartObject();
            jgen.writeStringField("productName", saleDetail.getProduct().getProductName());
            jgen.writeNumberField("quantity", saleDetail.getQuantity());
            jgen.writeEndObject();
            jgen.writeEndObject();
        }
        jgen.writeEndArray();
        jgen.writeEndObject();
    }
}

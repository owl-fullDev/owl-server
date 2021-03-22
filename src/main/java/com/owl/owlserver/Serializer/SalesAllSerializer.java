package com.owl.owlserver.Serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.owl.owlserver.model.Sale;
import com.owl.owlserver.model.SaleDetail;

import java.io.IOException;

public class SalesAllSerializer extends StdSerializer<Sale> {

    public SalesAllSerializer(){
        this(null);
    }

    public SalesAllSerializer(Class<Sale> t) {
        super(t);
    }

    @Override
    public void serialize(Sale sale, JsonGenerator jgen, SerializerProvider provider) throws IOException {

        jgen.writeStartObject();
        jgen.writeNumberField("saleId", sale.getSaleId());
        jgen.writeStringField("time", sale.getInitialDepositDate().toString());
        if (sale.getPromotion()!=null) {
            jgen.writeStringField("promotion", sale.getPromotion().getPromotionName());
        }
        else {
            jgen.writeStringField("promotion", "no promotion");
        }
        jgen.writeStringField("store", sale.getStore().getName());
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
        if (sale.getPromotionParentSaleId()!=0) {
            jgen.writeNumberField("promo_saleId", sale.getPromotionParentSaleId());
        }
        jgen.writeEndObject();
    }
}

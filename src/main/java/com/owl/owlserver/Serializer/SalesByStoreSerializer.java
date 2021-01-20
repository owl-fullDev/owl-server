package com.owl.owlserver.Serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.owl.owlserver.model.Sale;
import com.owl.owlserver.model.SaleDetail;
import com.owl.owlserver.model.Store;
import com.owl.owlserver.repositories.ProductRepository;
import com.owl.owlserver.repositories.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class SalesByStoreSerializer extends StdSerializer<Store> {

    @Autowired
    StoreRepository storeRepository;

    public SalesByStoreSerializer(){
        this(null);
    }

    public SalesByStoreSerializer(Class<Store> t) {
        super(t);
    }

    @Override
    public void serialize(Store store, JsonGenerator jgen, SerializerProvider provider) throws IOException {

        jgen.writeStartObject();
        jgen.writeStringField("store", store.getLocation());

        jgen.writeEndObject();
    }
}

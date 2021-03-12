package com.owl.owlserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "STORE_QUANTITY")
public class StoreQuantity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STORE_QUANTITY_ID", nullable = false)
    private int storeQuantityId;

    @Column(name = "PRODUCT_ID", nullable = false)
    private String productId;

    @Column(name = "INSTORE_QUANTITY")
    private int instoreQuantity;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "STORE_ID", nullable = false)
    private Store store;

    public StoreQuantity() {
    }

    public StoreQuantity(Store store, String productId, int instoreQuantity) {
        this.store = store;
        this.productId = productId;
        this.instoreQuantity = instoreQuantity;
    }

}

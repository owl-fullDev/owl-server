package com.owl.owlserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "STORE_QUANTITY")
public class StoreQuantity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STORE_QUANTITY_ID", nullable = false)
    private int promotionId;

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

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public int getPromotionId() {
        return promotionId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getInstoreQuantity() {
        return instoreQuantity;
    }

    public void setInstoreQuantity(int instoreQuantity) {
        this.instoreQuantity = instoreQuantity;
    }
}

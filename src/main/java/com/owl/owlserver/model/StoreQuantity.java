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

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @Column(name = "INSTORE_QUANTITY")
    private int instoreQuantity;

    @Column(name = "SET_QUANTITY")
    private int setQuantity;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "STORE_ID", nullable = false)
    private Store store;

    public StoreQuantity() {
    }

    public StoreQuantity(Store store, Product product, int instoreQuantity, int setQuantity) {
        this.store = store;
        this.product = product;
        this.instoreQuantity = instoreQuantity;
        this.setQuantity = setQuantity;
    }

}

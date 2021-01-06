package com.owl.owlserver.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="STORE_PROMOTION")
public class StorePromotion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STORE_PROMOTION_ID")
    private int store_promotion_id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "STOREID_FK")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "PROMOTIONID_FK")
    private Promotion promotion;

    public StorePromotion() {
    }

    public StorePromotion(Store store, Promotion promotion) {
        this.store = store;
        this.promotion = promotion;
    }

    public int getStore_promotion_id() {
        return store_promotion_id;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }
}
package com.owl.owlserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "PROMOTION")
public class Promotion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROMOTION_ID", nullable = false)
    private int promotionId;

    @Column(name = "PROMOTION_NAME", nullable = false)
    private String promotionName;

    @Column(name = "PERCENTAGE", nullable = false)
    private int percentage;

    @Column(name = "ACTIVE_ALL_STORES",nullable = false)
    private boolean activeInAllStores;

    @JsonIgnore
    @OneToMany(mappedBy = "promotion")
    List<Sale> saleList;

    @JsonIgnore
    @ManyToMany(mappedBy = "promotionList")
    List<Store> storeList;

    public Promotion() {
        saleList = new ArrayList<>();
        storeList = new ArrayList<>();
        this.activeInAllStores = false;
    }

    public Promotion(int percentage, String promotionName) {
        saleList = new ArrayList<>();
        storeList = new ArrayList<>();
        this.promotionName = promotionName;
        this.percentage = percentage;
        this.activeInAllStores = false;
    }

    public void addSale(Sale sale) {
        this.saleList.add(sale);
    }

    public void removeSale(Sale sale) {
        this.saleList.remove(sale);
    }

    public void addStore(Store store) {
        this.storeList.add(store);
    }

    public void removeStore(Store store) {
        this.storeList.remove(store);
    }
}

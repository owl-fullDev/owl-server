package com.owl.owlserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
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
    @ManyToMany(mappedBy = "promotionList")
    List<Store> storeList;

    public Promotion() {
        storeList = new ArrayList<>();
    }

    public Promotion(int percentage, String promotionName) {
        storeList = new ArrayList<>();
        this.promotionName = promotionName;
        this.percentage = percentage;
        this.activeInAllStores = false;
    }

    public List<Store> getStoreList() {
        return storeList;
    }

    public void addStore(Store store) {
        storeList.add(store);
    }

    public void removeStore(Store store) {
        storeList.remove(store);
    }

    public int getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(int promotionId) {
        this.promotionId = promotionId;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public boolean isActiveInAllStores() {
        return activeInAllStores;
    }

    public void setActiveInAllStores(boolean activeInAllStores) {
        this.activeInAllStores = activeInAllStores;
    }

    @Override
    public String toString() {
        return "Promotion{" +
                "promotionId=" + promotionId +
                ", promotionName='" + promotionName + '\'' +
                ", percentage=" + percentage +
                ", activeInAllStores=" + activeInAllStores +
                '}';
    }
}

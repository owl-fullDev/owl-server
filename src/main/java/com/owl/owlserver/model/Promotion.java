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

    @Column(name = "PERCENTAGE")
    private int percentage;

    @JsonIgnore
    @ManyToMany(mappedBy = "promotionList")
    List<Store> storeList;

    public Promotion() {
        storeList = new ArrayList<>();
    }

    public Promotion(int promotionId, String promotionName) {
        storeList = new ArrayList<>();
        this.promotionId = promotionId;
        this.promotionName = promotionName;
    }

    public Promotion(int promotionId, String promotionName, int percentage) {
        storeList = new ArrayList<>();
        this.promotionId = promotionId;
        this.promotionName = promotionName;
        this.percentage = percentage;
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

}

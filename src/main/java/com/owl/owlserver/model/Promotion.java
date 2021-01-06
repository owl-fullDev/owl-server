package com.owl.owlserver.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="PROMOTION")
public class Promotion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROMOTION_ID")
    private int promotion_id;

    @OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<StorePromotion> storePromotionList;

    @Column(name = "PROMOTION_NAME")
    private String promotion_name;

    @Column(name = "PERCENTAGE")
    private int percentage;

    public Promotion() {
    }

    public Promotion(String promotion_details) {
        this.promotion_name = promotion_details;
    }

    public Promotion(String promotion_details, int percentage) {
        this.promotion_name = promotion_details;
        this.percentage = percentage;
    }

    public List<StorePromotion> getStorePromotions() {
        return storePromotionList;
    }

    public void addStorePromotion (StorePromotion storePromotion){
        storePromotionList.add(storePromotion);
        storePromotion.setPromotion(this);
    }

    public void removeSaleDetail (StorePromotion storePromotion){
        storePromotionList.remove(storePromotion);
        storePromotion.setPromotion(null);
    }

    public int getPromotion_id() {
        return promotion_id;
    }

    public String getPromotion_name() {
        return promotion_name;
    }

    public void setPromotion_name(String promotion_details) {
        this.promotion_name = promotion_details;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        return "Promotions{" +
                "promotion_id=" + promotion_id +
                ", storePromotionsList=" + storePromotionList +
                ", promotion_details='" + promotion_name + '\'' +
                ", percentage=" + percentage +
                '}';
    }
}

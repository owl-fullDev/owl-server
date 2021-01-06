package com.owl.owlserver.model;


import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="STORE")
public class Store implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private int store_id;

    @Column(name = "location")
    private String location;

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<StoreInventory> storeInventoryList;

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<StorePromotion> storePromotionList;

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<StoreEmployee> storeEmployeeList;

    public List<StoreInventory> getStoreInventoryList() {
        return storeInventoryList;
    }

    public List<StorePromotion> getStorePromotionList() {
        return storePromotionList;
    }

    public List<StoreEmployee> getStoreEmployeeList() {
        return storeEmployeeList;
    }

    public Store() {
        storeInventoryList = new ArrayList<>();
        storePromotionList = new ArrayList<>();
        storeEmployeeList = new ArrayList<>();

    }

    public Store(String location) {
        this.location = location;
        storeInventoryList = new ArrayList<>();
        storePromotionList = new ArrayList<>();
        storeEmployeeList = new ArrayList<>();
    }

    public int getStore_id() {
        return store_id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void addStoreInventory(StoreInventory storeInventory) {
        storeInventoryList.add(storeInventory);
        storeInventory.setStore(this);
    }

    public void removeStoreInventory (StoreInventory storeInventory){
        storeInventoryList.remove(storeInventory);
        storeInventory.setStore(null);
    }

    public void addStorePromotion (StorePromotion storePromotion){
        storePromotionList.add(storePromotion);
        storePromotion.setStore(this);
    }

    public void removeSaleDetail (StorePromotion storePromotion){
        storePromotionList.remove(storePromotion);
        storePromotion.setStore(null);
    }

    public void addStoreEmployee(StoreEmployee storeEmployee) {
        storeEmployeeList.add(storeEmployee);
        storeEmployee.setStore(this);
    }

    public void removeStoreEmployee(StoreEmployee storeEmployee){
        storeEmployeeList.remove(storeEmployee);
        storeEmployee.setStore(null);
    }
}

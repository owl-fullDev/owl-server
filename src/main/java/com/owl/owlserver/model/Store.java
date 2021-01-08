package com.owl.owlserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "store")
public class Store implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STORE_ID", nullable = false)
    private int storeId;

    @Column(name = "LOCATION", nullable = false)
    private String location;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "STORE_PROMOTION",
            joinColumns = @JoinColumn(name = "STORE_ID"),
            inverseJoinColumns = @JoinColumn(name = "PROMOTION_ID")
    )
    List<Promotion> promotionList;

    @JsonIgnore
    @OneToMany(mappedBy = "store")
    private List<Employee> employeesList;

    @JsonIgnore
    @OneToMany(mappedBy = "store")
    private List<StoreQuantity> storeQuantityList;

    public Store() {
        promotionList = new ArrayList<>();
        employeesList = new ArrayList<>();
        storeQuantityList = new ArrayList<>();
    }

    public Store(String location) {
        promotionList = new ArrayList<>();
        employeesList = new ArrayList<>();
        storeQuantityList = new ArrayList<>();
        this.location = location;
    }

    public List<Promotion> getPromotionList() {
        return promotionList;
    }

    public void addPromotion(Promotion promotion) {
        promotionList.add(promotion);
    }

    public void removePromotion(Promotion promotion) {
        promotionList.remove(promotion);
    }

    public List<Employee> getEmployeesList() {
        return employeesList;
    }

    public void addEmployee(Employee employee) {
        employeesList.add(employee);
    }

    public void removeEmployee(Employee employee) {
        employeesList.remove(employee);
    }

    public List<StoreQuantity> getStoreQuantityList() {
        return storeQuantityList;
    }

    public void addStoreQuantity(StoreQuantity storeQuantity) {
        storeQuantityList.add(storeQuantity);
    }

    public void removeEmployee(StoreQuantity storeQuantity) {
        storeQuantityList.remove(storeQuantity);
    }

    public int getStoreId() {
        return storeId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}

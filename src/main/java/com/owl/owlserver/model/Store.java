package com.owl.owlserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "store")
public class Store implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STORE_ID", nullable = false)
    private int storeId;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "ADDRESS", nullable = false)
    private String address;

    @Column(name = "CITY", nullable = false)
    private String city;

    @Column(name = "PHONE_NUMBER", nullable = false)
    private String phoneNumber;

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
    private List<Sale> saleList;

    @JsonIgnore
    @OneToMany(mappedBy = "store")
    private List<Employee> employeesList;

    @JsonIgnore
    @OneToMany(mappedBy = "store")
    private List<StoreQuantity> storeQuantityList;

    public Store() {
        saleList = new ArrayList<>();
        promotionList = new ArrayList<>();
        employeesList = new ArrayList<>();
        storeQuantityList = new ArrayList<>();
    }

    public Store(String name, String address, String city, String phoneNumber) {
        saleList = new ArrayList<>();
        promotionList = new ArrayList<>();
        employeesList = new ArrayList<>();
        storeQuantityList = new ArrayList<>();
        this.name = name;
        this.address = address;
        this.city = city;
        this.phoneNumber = phoneNumber;
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

    public List<Sale> getSaleList() {
        return saleList;
    }

    public void addSale(Sale sale) {
        saleList.add(sale);
    }

    public void removeSale(Sale sale) {
        saleList.remove(sale);
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

    public void removeStoreQuantity(StoreQuantity storeQuantity) {
        storeQuantityList.remove(storeQuantity);
    }

    public int getStoreId() {
        return storeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "Store{" +
                "storeId=" + storeId +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}

package com.owl.owlserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "WAREHOUSE")
public class Warehouse implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WAREHOUSE_ID", nullable = false)
    private int warehouseId;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "ADDRESS", nullable = false)
    private String address;

    @Column(name = "PHONE_NUMBER", nullable = false)
    private String phone_number;

    @JsonIgnore
    @OneToMany(mappedBy = "warehouse")
    private List<WarehouseQuantity> warehouseQuantityList;

    public Warehouse() {
    }

    public Warehouse(String name, String address, String phone_number) {
        this.name = name;
        this.phone_number = phone_number;
        this.address = address;
        warehouseQuantityList = new ArrayList<>();
    }

    public int getWarehouseId() {
        return warehouseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<WarehouseQuantity> getWarehouseQuantityList() {
        return warehouseQuantityList;
    }

    public void addWarehouseQuantityList(WarehouseQuantity warehouseQuantity) {
        warehouseQuantityList.add(warehouseQuantity);
    }

    public void removeWarehouseQuantityList(WarehouseQuantity warehouseQuantity) {
        warehouseQuantityList.remove(warehouseQuantity);
    }

    @Override
    public String toString() {
        return "Warehouse{" +
                "warehouseId=" + warehouseId +
                ", name='" + name + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}


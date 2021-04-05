package com.owl.owlserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
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

    public void addWarehouseQuantityList(WarehouseQuantity warehouseQuantity) {
        this.warehouseQuantityList.add(warehouseQuantity);
    }
}


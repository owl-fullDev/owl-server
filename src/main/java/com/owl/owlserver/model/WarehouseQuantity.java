package com.owl.owlserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "WAREHOUSE_QUANTITY")
public class WarehouseQuantity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WAREHOUSE_QUANTITY_ID", nullable = false)
    private int warehouseQuantityId;

    @Column(name = "PRODUCT_ID")
    private String productId;

    @Column(name = "INWAREHOUSE_QUANTITY", nullable = false)
    private int inWarehouseQuantity ;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "WAREHOUSE_ID", nullable = false)
    private Warehouse warehouse;

    public WarehouseQuantity() {
    }

    public WarehouseQuantity(Warehouse warehouse, String productId, int inWarehouseQuantity) {
        this.warehouse = warehouse;
        this.productId = productId;
        this.inWarehouseQuantity = inWarehouseQuantity;
    }

    public int getWarehouseQuantityId() {
        return warehouseQuantityId;
    }

    public String getProductId() {
        return productId;
    }

    public int getInWarehouseQuantity() {
        return inWarehouseQuantity;
    }

    public void setInWarehouseQuantity(int inWarehouseQuantity) {
        this.inWarehouseQuantity = inWarehouseQuantity;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    @Override
    public String toString() {
        return "WarehouseQuantity{" +
                ", warehouse=" + warehouse.getName() +
                "warehouseQuantityId=" + warehouseQuantityId +
                ", productId='" + productId + '\'' +
                ", inWarehouseQuantity=" + inWarehouseQuantity +
                '}';
    }
}

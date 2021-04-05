package com.owl.owlserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "WAREHOUSE_QUANTITY")
public class WarehouseQuantity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WAREHOUSE_QUANTITY_ID", nullable = false)
    private int warehouseQuantityId;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @Column(name = "INWAREHOUSE_QUANTITY", nullable = false)
    private int inWarehouseQuantity ;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "WAREHOUSE_ID", nullable = false)
    private Warehouse warehouse;

    public WarehouseQuantity() {
    }

    public WarehouseQuantity(Warehouse warehouse, Product product, int inWarehouseQuantity) {
        this.warehouse = warehouse;
        this.product = product;
        this.inWarehouseQuantity = inWarehouseQuantity;
    }
}

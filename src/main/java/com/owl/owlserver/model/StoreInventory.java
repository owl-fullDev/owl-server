package com.owl.owlserver.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="STORE_INVENTORY")
public class StoreInventory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_inventory_id")
    private int store_id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "storeid_fk")
    private Store store;

    @Column(name = "productid_fk")
    private String productid_fk;

    @Column(name = "instore_quantity")
    private int instore_quantity;

    @Column(name = "last_shipmentid_fk")
    private String last_shipmentid_fk;

    public StoreInventory() {
    }

    public StoreInventory(int store_id, String productid_fk) {
        this.store_id = store_id;
        this.productid_fk = productid_fk;
    }

    public StoreInventory(int store_id, String productid_fk, int instore_quantity) {
        this.store_id = store_id;
        this.productid_fk = productid_fk;
        this.instore_quantity = instore_quantity;
    }

    public StoreInventory(int store_id, String productid_fk, int instore_quantity, String last_shipmentid_fk) {
        this.store_id = store_id;
        this.productid_fk = productid_fk;
        this.instore_quantity = instore_quantity;
        this.last_shipmentid_fk = last_shipmentid_fk;
    }

    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public String getProductid_fk() {
        return productid_fk;
    }

    public void setProductid_fk(String productid_fk) {
        this.productid_fk = productid_fk;
    }

    public int getInstore_quantity() {
        return instore_quantity;
    }

    public void setInstore_quantity(int instore_quantity) {
        this.instore_quantity = instore_quantity;
    }

    public String getLast_shipmentid_fk() {
        return last_shipmentid_fk;
    }

    public void setLast_shipmentid_fk(String last_shipmentid_fk) {
        this.last_shipmentid_fk = last_shipmentid_fk;
    }


}
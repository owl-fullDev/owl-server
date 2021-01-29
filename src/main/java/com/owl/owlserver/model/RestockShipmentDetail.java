package com.owl.owlserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "RESTOCK_SHIPMENT_DETAIL")
public class RestockShipmentDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RESTOCK_SHIPMENT_DETAIL_ID", nullable = false)
    private int restockShipmentDetailId;

    @Column(name = "QUANTITY", nullable = false)
    private int quantity;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "RESTOCK_SHIPMENT_ID", nullable = false)
    private RestockShipment restockShipment;

    public RestockShipmentDetail() {
    }

    public RestockShipmentDetail(RestockShipment restockShipment, Product product, int quantity) {
        this.quantity = quantity;
        this.product = product;
        this.restockShipment = restockShipment;
    }

    public int getRestockShipmentDetailId() {
        return restockShipmentDetailId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public RestockShipment getRestockShipment() {
        return restockShipment;
    }

    public void setRestockShipment(RestockShipment restockShipment) {
        this.restockShipment = restockShipment;
    }

    @Override
    public String toString() {
        return "RestockShipmentDetail{" +
                "restockShipmentDetailId=" + restockShipmentDetailId +
                ", quantity=" + quantity +
                ", product=" + product +
                '}';
    }
}
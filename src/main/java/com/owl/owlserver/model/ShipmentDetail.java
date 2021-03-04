package com.owl.owlserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "SHIPMENT_DETAIL")
public class ShipmentDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SHIPMENT_DETAIL_ID", nullable = false)
    private int shipmentDetailId;

    @Column(name = "QUANTITY", nullable = false)
    private int quantity;

    @Column(name = "RECEIVED_QUANTITY")
    private int receivedQuantity;

    @Column(name = "COMMENT")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "SHIPMENT_ID", nullable = false)
    private Shipment shipment;

    public ShipmentDetail() {
    }

    public ShipmentDetail(Shipment shipment, Product product, int quantity) {
        this.quantity = quantity;
        this.product = product;
        this.shipment = shipment;
        this.comment = "";
    }

    public int getShipmentDetailId() {
        return shipmentDetailId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getReceivedQuantity() {
        return receivedQuantity;
    }

    public void setReceivedQuantity(int receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    @Override
    public String toString() {
        return "ShipmentDetail{" +
                "ShipmentDetailId=" + shipmentDetailId +
                ", quantity=" + quantity +
                ", receivedQuantity=" + receivedQuantity +
                ", comment='" + comment + '\'' +
                ", product=" + product +
                ", shipment=" + shipment +
                '}';
    }
}
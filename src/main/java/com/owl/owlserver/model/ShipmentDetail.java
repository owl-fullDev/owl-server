package com.owl.owlserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@Data
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

    @Transient
    private String productId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "SHIPMENT_ID", nullable = false)
    private Shipment shipment;

    public ShipmentDetail(Shipment shipment, Product product, int quantity) {
        this.quantity = quantity;
        this.product = product;
        this.shipment = shipment;
        this.comment = "";
    }


}
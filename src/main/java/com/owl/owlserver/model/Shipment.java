package com.owl.owlserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "SHIPMENT")
public class Shipment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SHIPMENT_ID", nullable = false)
    private int shipmentId;

    @JsonIgnore
    @Column(name = "OUTGOING_DATE")
    private java.time.LocalDateTime sendTimestamp;

    @JsonIgnore
    @Column(name = "RECEIVED_DATE")
    private java.time.LocalDateTime receivedTimestamp;

    @Column(name = "ORIGIN_TYPE")
    private int originType;

    @Column(name = "DESTINATION_TYPE")
    private int destinationType;

    @Column(name = "ORIGIN_ID")
    private int originId;

    @Column(name = "DESTINATION_ID")
    private int destinationId;

    @OneToMany(mappedBy = "shipment")
    private List<ShipmentDetail> shipmentDetailList;

    public Shipment() {
        shipmentDetailList = new ArrayList<>();
    }

    public Shipment(int shipmentId) {
        shipmentDetailList = new ArrayList<>();
        this.shipmentId = shipmentId;
    }

    public Shipment(int originType, int destinationType, int originId, int destinationId) {
        shipmentDetailList = new ArrayList<>();
        this.originType = originType;
        this.destinationType = destinationType;
        this.originId = originId;
        this.destinationId = destinationId;
    }

}
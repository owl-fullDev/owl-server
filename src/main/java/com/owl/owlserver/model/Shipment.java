package com.owl.owlserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public Shipment(int originType, int destinationType, int originId, int destinationId) {
        shipmentDetailList = new ArrayList<>();
        this.originType = originType;
        this.destinationType = destinationType;
        this.originId = originId;
        this.destinationId = destinationId;
    }

    public int getShipmentId() {
        return shipmentId;
    }

    public LocalDateTime getSendTimestamp() {
        return sendTimestamp;
    }

    public void setSendTimestamp(LocalDateTime sendTimestamp) {
        this.sendTimestamp = sendTimestamp;
    }

    public LocalDateTime getReceivedTimestamp() {
        return receivedTimestamp;
    }

    public void setReceivedTimestamp(LocalDateTime receivedTimestamp) {
        this.receivedTimestamp = receivedTimestamp;
    }

    public int getOriginType() {
        return originType;
    }

    public void setOriginType(int originType) {
        this.originType = originType;
    }

    public int getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(int destinationType) {
        this.destinationType = destinationType;
    }

    public int getOriginId() {
        return originId;
    }

    public void setOriginId(int originId) {
        this.originId = originId;
    }

    public int getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(int destinationId) {
        this.destinationId = destinationId;
    }

    public List<ShipmentDetail> getShipmentDetailList() {
        return shipmentDetailList;
    }

    public void addShipmentDetail(ShipmentDetail shipmentDetail) {
        shipmentDetailList.add(shipmentDetail);
    }

    public void removeShipmentDetail(ShipmentDetail shipmentDetail) {
        shipmentDetailList.remove(shipmentDetail);
    }

    @Override
    public String toString() {
        return "Shipment{" +
                "ShipmentId=" + shipmentId +
                ", sendTimestamp=" + sendTimestamp +
                ", receivedTimestamp=" + receivedTimestamp +
                ", originType=" + originType +
                ", destinationType=" + destinationType +
                ", originId=" + originId +
                ", destinationId=" + destinationId +
                '}';
    }
}
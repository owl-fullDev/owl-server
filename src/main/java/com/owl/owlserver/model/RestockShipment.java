package com.owl.owlserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "RESTOCK_SHIPMENT")
public class RestockShipment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RESTOCK_SHIPMENT_ID", nullable = false)
    private int restockShipmentId;

    @Column(name = "STATUS", nullable = false)
    private boolean restockShipmentStatus;

    @Column(name = "OUTGOING_DATE")
    private java.time.LocalDateTime sendTimestamp;

    @Column(name = "RECEIVED_DATE")
    private java.time.LocalDateTime receivedTimestamp;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "WAREHOUSE_ID", nullable = false)
    private Warehouse warehouse;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "STORE_ID", nullable = false)
    private Store store;

    @JsonIgnore
    @OneToMany(mappedBy = "restockShipment", fetch = FetchType.EAGER)
    private List<RestockShipmentDetail> restockShipmentDetailList;

    public RestockShipment() {
        restockShipmentDetailList = new ArrayList<>();
    }

    public RestockShipment(Warehouse warehouse, Store store) {
        this.warehouse = warehouse;
        this.store = store;
        restockShipmentDetailList = new ArrayList<>();
    }

    public int getRestockShipmentId() {
        return restockShipmentId;
    }

    public boolean isRestockShipmentStatus() {
        return restockShipmentStatus;
    }

    public void setRestockShipmentStatus(boolean restockShipmentStatus) {
        this.restockShipmentStatus = restockShipmentStatus;
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

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public List<RestockShipmentDetail> getRestockShipmentDetailList() {
        return restockShipmentDetailList;
    }

    public void addRestockShipmentDetail(RestockShipmentDetail restockShipmentDetail) {
        restockShipmentDetailList.add(restockShipmentDetail);
    }

    public void removeRestockShipmentDetail(RestockShipmentDetail restockShipmentDetail) {
        restockShipmentDetailList.remove(restockShipmentDetail);
    }

    @Override
    public String toString() {
        return "RestockShipment{" +
                "restockShipmentId=" + restockShipmentId +
                ", restockShipmentStatus=" + restockShipmentStatus +
                ", sendTimestamp=" + sendTimestamp +
                ", receivedTimestamp=" + receivedTimestamp +
                ", warehouse=" + warehouse.getName() +
                ", store=" + store.getName() +
                ", restockShipmentDetailList=" + restockShipmentDetailList +
                '}';
    }
}
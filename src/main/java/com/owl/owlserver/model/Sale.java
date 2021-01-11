package com.owl.owlserver.model;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonAppend;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SALE")
public class Sale implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SALE_ID", nullable = false)
    private int saleId;
    
    @Column(name = "PROMOTION_ID", nullable = false)
    private int promotionId;
    
    @Column(name = "EMPLOYEE_ID", nullable = false)
    private int employeeId;

    @Column(name = "STORE_ID", nullable = false)
    private int storeId;

    @Column(name = "GRAND_TOTAL", nullable = false)
    private double grandTotal;

    @Column(name = "PICKUP_DATE")
    private java.time.LocalDateTime pickupDate;

    @Column(name = "INITIAL_DEPOSIT_DATE", nullable = false)
    private java.time.LocalDateTime initialDepositDate;

    @Column(name = "INITIAL_DEPOSIT_TYPE", nullable = false)
    private String initialDepositType;

    @Column(name = "INITIAL_DEPOSIT_AMOUNT", nullable = false)
    private double initialDepositAmount;

    @Column(name = "FINAL_DEPOSIT_DATE")
    private java.time.LocalDateTime finalDepositDate;

    @Column(name = "FINAL_DEPOSIT_TYPE")
    private String finalDepositType;

    @Column(name = "FINAL_DEPOSIT_AMOUNT")
    private double finalDepositAmount;

    @Column(name = "FULL_PAYMENT")
    private boolean fullyPaid;

    @ManyToOne
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "sale", fetch = FetchType.EAGER)
    private List<SaleDetail> saleDetailList;

    public Sale() {
        saleDetailList = new ArrayList<>();
    }

    public Sale(int promotionId, int employeeId, int storeId, double grandTotal, LocalDateTime initialDepositDate, String initialDepositType, double initialDepositAmount, boolean fullyPaid) {
        saleDetailList = new ArrayList<>();
        this.promotionId = promotionId;
        this.employeeId = employeeId;
        this.storeId = storeId;
        this.grandTotal = grandTotal;
        this.initialDepositDate = initialDepositDate;
        this.initialDepositType = initialDepositType;
        this.initialDepositAmount = initialDepositAmount;
        this.fullyPaid = fullyPaid;
    }

    public List<SaleDetail> getSaleDetailList() {
        return saleDetailList;
    }

    public void addSaleDetail(SaleDetail saleDetail) {
        saleDetailList.add(saleDetail);
    }

    public void removeSaleDetail(SaleDetail saleDetail) {
        saleDetailList.remove(saleDetail);
    }

    public Customer getCustomer(){
        return customer;
    }

    public void setCustomer(Customer customer){
        this.customer = customer;
    }

    public int getSaleId() {
        return saleId;
    }

    public int getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(int promotionId) {
        this.promotionId = promotionId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public LocalDateTime getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(LocalDateTime pickupDate) {
        this.pickupDate = pickupDate;
    }

    public LocalDateTime getInitialDepositDate() {
        return initialDepositDate;
    }

    public void setInitialDepositDate(LocalDateTime initialDepositDate) {
        this.initialDepositDate = initialDepositDate;
    }

    public String getInitialDepositType() {
        return initialDepositType;
    }

    public void setInitialDepositType(String initialDepositType) {
        this.initialDepositType = initialDepositType;
    }

    public double getInitialDepositAmount() {
        return initialDepositAmount;
    }

    public void setInitialDepositAmount(double initialDepositAmount) {
        this.initialDepositAmount = initialDepositAmount;
    }

    public LocalDateTime getFinalDepositDate() {
        return finalDepositDate;
    }

    public void setFinalDepositDate(LocalDateTime finalDepositDate) {
        this.finalDepositDate = finalDepositDate;
    }

    public String getFinalDepositType() {
        return finalDepositType;
    }

    public void setFinalDepositType(String finalDepositType) {
        this.finalDepositType = finalDepositType;
    }

    public double getFinalDepositAmount() {
        return finalDepositAmount;
    }

    public void setFinalDepositAmount(double finalDepositAmount) {
        this.finalDepositAmount = finalDepositAmount;
    }

    public boolean isFullyPaid() {
        return fullyPaid;
    }

    public void setFullyPaid(boolean fullyPaid) {
        this.fullyPaid = fullyPaid;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "saleId=" + saleId +
                ", promotionId=" + promotionId +
                ", employeeId=" + employeeId +
                ", storeId=" + storeId +
                ", grandTotal=" + grandTotal +
                ", pickupDate=" + pickupDate +
                ", initialDepositDate=" + initialDepositDate +
                ", initialDepositType='" + initialDepositType + '\'' +
                ", initialDepositAmount=" + initialDepositAmount +
                ", finalDepositDate=" + finalDepositDate +
                ", finalDepositType='" + finalDepositType + '\'' +
                ", finalDepositAmount=" + finalDepositAmount +
                ", fullyPaid=" + fullyPaid +
                '}';
    }


}

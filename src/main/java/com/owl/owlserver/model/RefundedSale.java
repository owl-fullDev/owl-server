package com.owl.owlserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "REFUNDED_SALE")
public class RefundedSale implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REFUNDED_SALE_ID", nullable = false)
    private int refundedSaleId;

    @Column(name = "EMPLOYEE_ID", nullable = false)
    private int employeeId;

    @Column(name = "GRAND_TOTAL", nullable = false)
    private double grandTotal;

    @Column(name = "PICKUP_DATE")
    private LocalDateTime pickupDate;

    @Column(name = "INITIAL_DEPOSIT_DATE", nullable = false)
    private LocalDateTime initialDepositDate;

    @Column(name = "INITIAL_DEPOSIT_TYPE", nullable = false)
    private String initialDepositType;

    @Column(name = "INITIAL_DEPOSIT_AMOUNT", nullable = false)
    private double initialDepositAmount;

    @Column(name = "FINAL_DEPOSIT_DATE")
    private LocalDateTime finalDepositDate;

    @Column(name = "FINAL_DEPOSIT_TYPE")
    private String finalDepositType;

    @Column(name = "FINAL_DEPOSIT_AMOUNT")
    private double finalDepositAmount;

    @Column(name = "FULL_PAYMENT")
    private boolean fullyPaid;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "PROMOTION_ID")
    private Promotion promotion;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "STORE_ID", nullable = false)
    private Store store;

    @ManyToOne
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "sale", fetch = FetchType.EAGER)
    private List<SaleDetail> saleDetailList;

    public RefundedSale() {
        saleDetailList = new ArrayList<>();
    }

    public RefundedSale(int employeeId, Store store, double grandTotal, LocalDateTime initialDepositDate, String initialDepositType, double initialDepositAmount, boolean fullyPaid) {
        saleDetailList = new ArrayList<>();
        this.employeeId = employeeId;
        this.store = store;
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

    public int getRefundedSaleId() {
        return refundedSaleId;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
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
                "refundedSaleId=" + refundedSaleId +
                ", employeeId=" + employeeId +
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

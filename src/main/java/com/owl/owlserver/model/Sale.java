package com.owl.owlserver.model;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="SALE")
public class Sale implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_id", nullable = false)
    private int sale_id;

    @Column(name = "promotionid_fk")
    private int promotionid_fk;

    @Column(name = "grand_total", nullable = false)
    private double grand_total;

    @Column(name = "salespersonid_fk", nullable = false)
    private int salespersonid_fk;

    @Column(name = "storeid_fk", nullable = false)
    private int storeid_fk;

    @Column(name = "pickup_date")
    private Date pickup_date;

    @Column(name = "initial_deposit_date", nullable = false)
    private LocalDateTime initial_deposit_date;

    @Column(name = "initial_deposit_type", nullable = false)
    private String initial_deposit_type;

    @Column(name = "initial_deposit_amount", nullable = false)
    private double initial_deposit_amount;

    @Column(name = "final_payment_date")
    private LocalDateTime final_payment_date;

    @Column(name = "final_payment_type")
    private String final_payment_type;

    @Column(name = "final_payment_amount")
    private double final_payment_amount;

    @Column(name = "delivered")
    private boolean delivered;

    //maps one sale to many sale details relationship, in this side a sale object is what maps to its sale_details
    @OneToMany(mappedBy = "sale", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SaleDetail> saleDetailList; //stores list of sale_detail entries where FK saleid_fk field is the ID of this sale

    //maps one sale to one customer
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    //default constructor, never used
    public Sale() {
        saleDetailList = new ArrayList<>();
    }

    //creates new sale
    public Sale(int promotionid_fk, double grand_total, int salespersonid_fk, int storeid_fk, LocalDateTime initial_payment_date, String initial_payment_type, double initial_payment_amount, boolean delivered) {
        saleDetailList = new ArrayList<>();
        this.promotionid_fk = promotionid_fk;
        this.grand_total = grand_total;
        this.salespersonid_fk = salespersonid_fk;
        this.storeid_fk = storeid_fk;
        this.initial_deposit_date = initial_payment_date;
        this.initial_deposit_type = initial_payment_type;
        this.initial_deposit_amount = initial_payment_amount;
        this.delivered = delivered;
    }

    public int getSale_id() {
        return sale_id;
    }

    public void setSale_id(int sale_id) {
        this.sale_id = sale_id;
    }

    public int getPromotionid_fk() {
        return promotionid_fk;
    }

    public void setPromotionid_fk(int promotionid_fk) {
        this.promotionid_fk = promotionid_fk;
    }

    public double getGrand_total() {
        return grand_total;
    }

    public void setGrand_total(double grand_total) {
        this.grand_total = grand_total;
    }

    public int getSalespersonid_fk() {
        return salespersonid_fk;
    }

    public void setSalespersonid_fk(int salespersonid_fk) {
        this.salespersonid_fk = salespersonid_fk;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getStoreid_fk() {
        return storeid_fk;
    }

    public void setStoreid_fk(int storeid_fk) {
        this.storeid_fk = storeid_fk;
    }

    public Date getPickup_date() {
        return pickup_date;
    }

    public void setPickup_date(Date pickup_date) {
        this.pickup_date = pickup_date;
    }

    public LocalDateTime getInitial_deposit_date() {
        return initial_deposit_date;
    }

    public void setInitial_deposit_date(LocalDateTime initial_deposit_date) {
        this.initial_deposit_date = initial_deposit_date;
    }

    public String getInitial_deposit_type() {
        return initial_deposit_type;
    }

    public void setInitial_deposit_type(String initial_deposit_type) {
        this.initial_deposit_type = initial_deposit_type;
    }

    public double getInitial_deposit_amount() {
        return initial_deposit_amount;
    }

    public void setInitial_deposit_amount(double initial_deposit_amount) {
        this.initial_deposit_amount = initial_deposit_amount;
    }

    public LocalDateTime getFinal_payment_date() {
        return final_payment_date;
    }

    public void setFinal_payment_date(LocalDateTime final_payment_date) {
        this.final_payment_date = final_payment_date;
    }

    public String getFinal_payment_type() {
        return final_payment_type;
    }

    public void setFinal_payment_type(String final_payment_type) {
        this.final_payment_type = final_payment_type;
    }

    public double getFinal_payment_amount() {
        return final_payment_amount;
    }

    public void setFinal_payment_amount(double final_payment_amount) {
        this.final_payment_amount = final_payment_amount;
    }

    public boolean getDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public List<SaleDetail> getSaleDetailsList() {
        return saleDetailList;
    }

    public void addSaleDetail (SaleDetail saleDetail){
        saleDetailList.add(saleDetail);
        saleDetail.setSale(this);
    }

    public void removeSaleDetail (SaleDetail saleDetail){
        saleDetailList.remove(saleDetail);
        saleDetail.setSale(null);
    }

}

package com.owl.owlserver.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "SALE_DETAIL")
public class SaleDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_detail_id")
    private int saleDetailId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "saleid_fk")
    private Sale sale;

    @Column(name = "productid_fk")
    private String productid_fk;

    @Column(name = "quantity_sold")
    private int quantity_sold;

    public SaleDetail(){
    }

    public SaleDetail(Sale sale, String productid_fk, int quantity_sold){
        this.sale = sale;
        this.productid_fk = productid_fk;
        this.quantity_sold = quantity_sold;
    }

    public int getSaleDetailId() {
        return saleDetailId;
    }

    public void setSaleDetailId(int sale_detail_id) {
        this.saleDetailId = sale_detail_id;
    }

    public Sale getSale() {
        return this.sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public String getProductid_fk() {
        return productid_fk;
    }

    public void setProductid_fk(String productid_fk) {
        this.productid_fk = productid_fk;
    }

    public int getQuantity_sold() {
        return quantity_sold;
    }

    public void setQuantity_sold(int quantity_sold) {
        this.quantity_sold = quantity_sold;
    }
}

package com.owl.owlserver.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "SALE_DETAIL")
public class SaleDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SALE_DETAIL_ID", nullable = false)
    private int saleId;

    @Column(name = "PRODUCT_ID", nullable = false)
    private String productId;

    @Column(name = "QUANTITY", nullable = false)
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "SALE_ID", nullable = false)
    private Sale sale;

    public SaleDetail() {
    }

    public SaleDetail(Sale sale, String productId, int quantity) {
        this.sale = sale;
        this.productId = productId;
        this.quantity = quantity;
    }

    public Sale getSale(){
        return sale;
    }

    public void setSale(Sale sale){
        this.sale = sale;
    }

    public int getSaleId() {
        return saleId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

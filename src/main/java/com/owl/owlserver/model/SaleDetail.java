package com.owl.owlserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "SALE_DETAIL")
public class SaleDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SALE_DETAIL_ID", nullable = false)
    private int saleDetailId;

    @Column(name = "QUANTITY", nullable = false)
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "SALE_ID", nullable = false)
    private Sale sale;

    public SaleDetail() {
    }

    public SaleDetail(Sale sale, Product product, int quantity) {
        this.sale = sale;
        this.product = product;
        this.quantity = quantity;
    }

    public Sale getSale(){
        return sale;
    }

    public void setSale(Sale sale){
        this.sale = sale;
    }

    public int getSaleDetailId() {
        return saleDetailId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "SaleDetail{" +
                "saleId=" + saleDetailId +
                ", productId='" + product.getProductId() + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}

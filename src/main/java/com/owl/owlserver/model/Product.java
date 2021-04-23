package com.owl.owlserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "Product")
public class Product implements Serializable {

    @Id
    @Column(name = "PRODUCT_ID", nullable = false)
    private String productId;

    @Column(name = "PRODUCT_NAME", nullable = false)
    private String productName;

    @Column(name = "PRODUCT_PRICE", nullable = false)
    private double productPrice;

    @JsonIgnore
    @Column(name = "SUPPLIER_CODE")
    private String supplierCode;

    @JsonIgnore
    @Column(name = "IMAGE_LINK")
    private String imageLink;

    public Product() {
    }

    public Product(String productId){
        this.productId = productId;
    }

    public Product(String productId, String productName, double productPrice, String supplierCode) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.supplierCode = supplierCode;
    }
}

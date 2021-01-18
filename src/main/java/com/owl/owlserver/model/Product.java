package com.owl.owlserver.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Product")
public class Product implements Serializable {

    @Id
    @Column(name = "PRODUCT_ID", nullable = false)
    private String productId;

    @Column(name = "PRODUCT_NAME", nullable = false)
    private String productName;

    @Column(name = "PRODUCT_PRICE", nullable = false)
    private double productPrice;

    @Column(name = "IMAGE_LINK")
    private String imageLink;

    @Transient
    private int storeQuantity;

    public Product() {
    }

    public Product(String productId, String productName, double productPrice) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public int getStoreQuantity() {
        return storeQuantity;
    }

    public void setStoreQuantity(int storeQuantity) {
        this.storeQuantity = storeQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Double.compare(product.productPrice, productPrice) == 0 && productId.equals(product.productId) && productName.equals(product.productName) && Objects.equals(imageLink, product.imageLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, productName, productPrice, imageLink);
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", productPrice=" + productPrice +
                ", imageLink='" + imageLink + '\'' +
                '}';
    }
}

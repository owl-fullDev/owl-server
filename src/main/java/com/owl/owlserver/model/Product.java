package com.owl.owlserver.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="PRODUCT")
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    private String product_id;

    @Column(name = "product_name", nullable = false)
    private String product_name;

    @Column(name = "product_price", nullable = false)
    private double product_price;

    @Column(name = "image_link")
    private String image_link;

    public Product() {
    }

    public Product(String product_SKU, String product_name) {
        this.product_id = product_SKU;
        this.product_name = product_name;
    }

    public Product(String product_SKU, String product_name, String image_link) {
        this.product_id = product_SKU;
        this.product_name = product_name;
        this.image_link = image_link;
    }


    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public double getProduct_price() {
        return product_price;
    }

    public void setProduct_price(double product_price) {
        this.product_price = product_price;
    }

    public String getImage_link() {
        return image_link;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }
}

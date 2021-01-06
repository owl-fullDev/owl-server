package com.owl.owlserver.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "CUSTOMER")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id", nullable = false)
    private int customer_id;

    @Column(name = "FIRST_NAME", nullable = false)
    private String first_name;

    @Column(name = "LAST_NAME", nullable = false)
    private String last_name;

    @Column(name = "phone_number", nullable = false)
    private String phone_number;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "pupil_distance", nullable = false)
    private int pupil_distance;
    @Column(name = "right_eye_sphere", nullable = false)
    private double right_eye_sphere;
    @Column(name = "right_eye_cylinder", nullable = false)
    private double right_eye_cylinder;
    @Column(name = "right_eye_axis", nullable = false)
    private int right_eye_axis;
    @Column(name = "right_eye_add", nullable = false)
    private double right_eye_add;
    @Column(name = "right_eye_prism", nullable = false)
    private String right_eye_prism;
    @Column(name = "left_eye_sphere", nullable = false)
    private double left_eye_sphere;
    @Column(name = "left_eye_cylinder", nullable = false)
    private double left_eye_cylinder;
    @Column(name = "left_eye_axis", nullable = false)
    private int left_eye_axis;
    @Column(name = "left_eye_add", nullable = false)
    private double left_eye_add;
    @Column(name = "left_eye_prism", nullable = false)
    private String left_eye_prism;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Sale> saleList; //stores list of sale entries

    public Customer() {
        saleList = new ArrayList<>();
    }

    public Customer(String first_name, String last_name, String phone_number, String email, int pupil_distance, double right_eye_sphere, double right_eye_cylinder, int right_eye_axis, double right_eye_add, String right_eye_prism, double left_eye_sphere, double left_eye_cylinder, int left_eye_axis, double left_eye_add, String left_eye_prism) {
        saleList = new ArrayList<>();
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone_number = phone_number;
        this.email = email;
        this.pupil_distance = pupil_distance;
        this.right_eye_sphere = right_eye_sphere;
        this.right_eye_cylinder = right_eye_cylinder;
        this.right_eye_axis = right_eye_axis;
        this.right_eye_add = right_eye_add;
        this.right_eye_prism = right_eye_prism;
        this.left_eye_sphere = left_eye_sphere;
        this.left_eye_cylinder = left_eye_cylinder;
        this.left_eye_axis = left_eye_axis;
        this.left_eye_add = left_eye_add;
        this.left_eye_prism = left_eye_prism;
    }

    public List<Sale> getSaleList() {
        return saleList;
    }

    public void setSaleList(List<Sale> saleList) {
        this.saleList = saleList;
    }

    public void addSale(Sale sale) {
        saleList.add(sale);
        sale.setCustomer(this);
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPupil_distance() {
        return pupil_distance;
    }

    public void setPupil_distance(int pupil_distance) {
        this.pupil_distance = pupil_distance;
    }

    public double getRight_eye_sphere() {
        return right_eye_sphere;
    }

    public void setRight_eye_sphere(double right_eye_sphere) {
        this.right_eye_sphere = right_eye_sphere;
    }

    public double getRight_eye_cylinder() {
        return right_eye_cylinder;
    }

    public void setRight_eye_cylinder(double right_eye_cylinder) {
        this.right_eye_cylinder = right_eye_cylinder;
    }

    public int getRight_eye_axis() {
        return right_eye_axis;
    }

    public void setRight_eye_axis(int right_eye_axis) {
        this.right_eye_axis = right_eye_axis;
    }

    public double getRight_eye_add() {
        return right_eye_add;
    }

    public void setRight_eye_add(double right_eye_add) {
        this.right_eye_add = right_eye_add;
    }

    public String getRight_eye_prism() {
        return right_eye_prism;
    }

    public void setRight_eye_prism(String right_eye_prism) {
        this.right_eye_prism = right_eye_prism;
    }

    public double getLeft_eye_sphere() {
        return left_eye_sphere;
    }

    public void setLeft_eye_sphere(double left_eye_sphere) {
        this.left_eye_sphere = left_eye_sphere;
    }

    public double getLeft_eye_cylinder() {
        return left_eye_cylinder;
    }

    public void setLeft_eye_cylinder(double left_eye_cylinder) {
        this.left_eye_cylinder = left_eye_cylinder;
    }

    public int getLeft_eye_axis() {
        return left_eye_axis;
    }

    public void setLeft_eye_axis(int left_eye_axis) {
        this.left_eye_axis = left_eye_axis;
    }

    public double getLeft_eye_add() {
        return left_eye_add;
    }

    public void setLeft_eye_add(double left_eye_add) {
        this.left_eye_add = left_eye_add;
    }

    public String getLeft_eye_prism() {
        return left_eye_prism;
    }

    public void setLeft_eye_prism(String left_eye_prism) {
        this.left_eye_prism = left_eye_prism;
    }


}
package com.owl.owlserver.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "CUSTOMER")
public class Customer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUSTOMER_ID", nullable = false)
    private int customerId;

    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;

    @Column(name = "PHONE_NUMBER", nullable = false)
    private String phoneNumber;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PUPIL_DISTANCE", nullable = false)
    private int pupilDistance;
    @Column(name = "RIGHT_EYE_SPHERE", nullable = false)
    private double rightEyeSphere;
    @Column(name = "RIGHT_EYE_CYLINDER", nullable = false)
    private double rightEyeCylinder;
    @Column(name = "RIGHT_EYE_AXIS", nullable = false)
    private int rightEyeAxis;
    @Column(name = "RIGHT_EYE_ADD", nullable = false)
    private double rightEyeAdd;
    @Column(name = "RIGHT_EYE_PRISM", nullable = false)
    private String rightEyePrism;
    @Column(name = "LEFT_EYE_SPHERE", nullable = false)
    private double leftEyeSphere;
    @Column(name = "LEFT_EYE_CYLINDER", nullable = false)
    private double leftEyeCylinder;
    @Column(name = "LEFT_EYE_AXIS", nullable = false)
    private int leftEyeAxis;
    @Column(name = "LEFT_EYE_ADD", nullable = false)
    private double leftEyeAdd;
    @Column(name = "LEFT_EYE_PRISM", nullable = false)
    private String leftEyePrism;

    @JsonIgnore
    @OneToMany(mappedBy = "customer")
    private List<Sale> saleList;

    public Customer() {
        saleList = new ArrayList<>();
    }

    public Customer(String firstName, String lastName, String phoneNumber, int pupilDistance, double rightEyeSphere, double rightEyeCylinder, int rightEyeAxis, double rightEyeAdd, String rightEyePrism, double leftEyeSphere, double leftEyeCylinder, int leftEyeAxis, double leftEyeAdd, String leftEyePrism) {
        saleList = new ArrayList<>();
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.pupilDistance = pupilDistance;
        this.rightEyeSphere = rightEyeSphere;
        this.rightEyeCylinder = rightEyeCylinder;
        this.rightEyeAxis = rightEyeAxis;
        this.rightEyeAdd = rightEyeAdd;
        this.rightEyePrism = rightEyePrism;
        this.leftEyeSphere = leftEyeSphere;
        this.leftEyeCylinder = leftEyeCylinder;
        this.leftEyeAxis = leftEyeAxis;
        this.leftEyeAdd = leftEyeAdd;
        this.leftEyePrism = leftEyePrism;
    }

    public Customer(String firstName, String lastName, String phoneNumber, String email, int pupilDistance, double rightEyeSphere, double rightEyeCylinder, int rightEyeAxis, double rightEyeAdd, String rightEyePrism, double leftEyeSphere, double leftEyeCylinder, int leftEyeAxis, double leftEyeAdd, String leftEyePrism) {
        saleList = new ArrayList<>();
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.pupilDistance = pupilDistance;
        this.rightEyeSphere = rightEyeSphere;
        this.rightEyeCylinder = rightEyeCylinder;
        this.rightEyeAxis = rightEyeAxis;
        this.rightEyeAdd = rightEyeAdd;
        this.rightEyePrism = rightEyePrism;
        this.leftEyeSphere = leftEyeSphere;
        this.leftEyeCylinder = leftEyeCylinder;
        this.leftEyeAxis = leftEyeAxis;
        this.leftEyeAdd = leftEyeAdd;
        this.leftEyePrism = leftEyePrism;
    }

    public List<Sale> getSaleList() {
        return saleList;
    }

    public Sale getSale(Sale sale){
        return saleList.get(saleList.indexOf(sale));
    }

    public void addSale(Sale sale) {
        saleList.add(sale);
    }

    public void removeSale(Sale sale) {
        saleList.remove(sale);
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String first_name) {
        this.firstName = first_name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String last_name) {
        this.lastName = last_name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phone_number) {
        this.phoneNumber = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPupilDistance() { return pupilDistance; }

    public void setPupilDistance(int pupilDistance) {
        this.pupilDistance = pupilDistance;
    }

    public double getRightEyeSphere() {
        return rightEyeSphere;
    }

    public void setRightEyeSphere(double rightEyeSphere) {
        this.rightEyeSphere = rightEyeSphere;
    }

    public double getRightEyeCylinder() {
        return rightEyeCylinder;
    }

    public void setRightEyeCylinder(double rightEyeCylinder) {
        this.rightEyeCylinder = rightEyeCylinder;
    }

    public int getRightEyeAxis() {
        return rightEyeAxis;
    }

    public void setRightEyeAxis(int rightEyeAxis) {
        this.rightEyeAxis = rightEyeAxis;
    }

    public double getRightEyeAdd() {
        return rightEyeAdd;
    }

    public void setRightEyeAdd(double rightEyeAdd) {
        this.rightEyeAdd = rightEyeAdd;
    }

    public String getRightEyePrism() {
        return rightEyePrism;
    }

    public void setRightEyePrism(String rightEyePrism) {
        this.rightEyePrism = rightEyePrism;
    }

    public double getLeftEyeSphere() {
        return leftEyeSphere;
    }

    public void setLeftEyeSphere(double leftEyeSphere) {
        this.leftEyeSphere = leftEyeSphere;
    }

    public double getLeftEyeCylinder() {
        return leftEyeCylinder;
    }

    public void setLeftEyeCylinder(double leftEyeCylinder) {
        this.leftEyeCylinder = leftEyeCylinder;
    }

    public int getLeftEyeAxis() {
        return leftEyeAxis;
    }

    public void setLeftEyeAxis(int leftEyeAxis) {
        this.leftEyeAxis = leftEyeAxis;
    }

    public double getLeftEyeAdd() {
        return leftEyeAdd;
    }

    public void setLeftEyeAdd(double leftEyeAdd) {
        this.leftEyeAdd = leftEyeAdd;
    }

    public String getLeftEyePrism() {
        return leftEyePrism;
    }

    public void setLeftEyePrism(String leftEyePrism) {
        this.leftEyePrism = leftEyePrism;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", pupilDistance=" + pupilDistance +
                ", rightEyeSphere=" + rightEyeSphere +
                ", rightEyeCylinder=" + rightEyeCylinder +
                ", rightEyeAxis=" + rightEyeAxis +
                ", rightEyeAdd=" + rightEyeAdd +
                ", rightEyePrism='" + rightEyePrism + '\'' +
                ", leftEyeSphere=" + leftEyeSphere +
                ", leftEyeCylinder=" + leftEyeCylinder +
                ", leftEyeAxis=" + leftEyeAxis +
                ", leftEyeAdd=" + leftEyeAdd +
                ", leftEyePrism='" + leftEyePrism + '\'' +
                '}';
    }
}
package com.owl.owlserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
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

    public void addSale(Sale sale) {
        saleList.add(sale);
    }
}
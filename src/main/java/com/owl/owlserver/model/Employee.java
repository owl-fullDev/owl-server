package com.owl.owlserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "EMPLOYEE")
public class Employee implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMPLOYEE_ID", nullable = false)
    private int employeeId;

    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", nullable = false)
    private String lastname;

    @Column(name = "JOB_TITLE", nullable = false)
    private String jobTitle;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "EMAIL")
    private String email;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "STORE_ID")
    private Store store;

    public Employee() {
    }

    public Employee(String firstName, String lastname, String jobTitle, String phoneNumber, String email, Store store) {
        this.firstName = firstName;
        this.lastname = lastname;
        this.jobTitle = jobTitle;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.store = store;
    }
}

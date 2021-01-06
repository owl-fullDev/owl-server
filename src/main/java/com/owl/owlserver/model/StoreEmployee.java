package com.owl.owlserver.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="STORE_EMPLOYEE")
public class StoreEmployee implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STORE_EMPLOYEE_ID")
    private int store_employee_id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "STOREID_FK")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "EMPLOYEEID_FK")
    private Employee employee;

    public StoreEmployee() {
    }

    public StoreEmployee(Store store, Employee employee) {
        this.store = store;
        this.employee = employee;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}

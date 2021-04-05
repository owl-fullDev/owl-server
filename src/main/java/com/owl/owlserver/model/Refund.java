package com.owl.owlserver.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "REFUND")
public class Refund implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REFUND_ID", nullable = false)
    private int refundId;

    @Column(name = "REFUND_DETAILS", nullable = false)
    private String refundDetails;

    @Column(name = "REMARKS", nullable = false)
    private String remarks;

    @Column(name = "REFUND_DATE")
    private java.time.LocalDateTime refundDate;

}

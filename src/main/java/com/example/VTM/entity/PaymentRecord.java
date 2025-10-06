package com.example.VTM.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "AppPayment_record")
public class PaymentRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "contact", nullable = false)
    private String contact;

    @Column(name = "REGNO", nullable = false)
    private String REGNO;

    @Column(name = "GROUPCODE", nullable = false)
    private String GROUPCODE;

    @Column(name = "amount", nullable = false)
    private int amount;


    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public PaymentRecord(Long id, String orderId, String name, String contact, String REGNO, String GROUPCODE, int amount, String status, LocalDateTime createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.name = name;
        this.contact = contact;
        this.REGNO = REGNO;
        this.GROUPCODE = GROUPCODE;
        this.amount = amount;

        this.status = status;
        this.createdAt = createdAt;
    }

    public PaymentRecord(String orderId, String name, String contact, String REGNO, String GROUPCODE, int amount, String status, LocalDateTime createdAt) {
        this.orderId = orderId;
        this.name = name;
        this.contact = contact;
        this.REGNO = REGNO;
        this.GROUPCODE = GROUPCODE;
        this.amount = amount;

        this.status = status;
        this.createdAt = createdAt;
    }

    public PaymentRecord() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getREGNO() {
        return REGNO;
    }

    public void setREGNO(String REGNO) {
        this.REGNO = REGNO;
    }

    public String getGROUPCODE() {
        return GROUPCODE;
    }

    public void setGROUPCODE(String GROUPCODE) {
        this.GROUPCODE = GROUPCODE;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

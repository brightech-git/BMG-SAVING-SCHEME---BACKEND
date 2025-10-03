package com.example.VTM.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class PaymentRequest {
    @JsonProperty("amount")
    private BigDecimal amount;



    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("customer")
    private Customer customer;



    public PaymentRequest() {
    }
// Getters and Setters


    public PaymentRequest(BigDecimal amount, String orderId, Customer customer) {
        this.amount = amount;
        this.orderId = orderId;
        this.customer = customer;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}

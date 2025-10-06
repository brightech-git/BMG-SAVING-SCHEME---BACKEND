package com.example.VTM.ICICI.entity;

import lombok.Data;

@Data
public class StatusRequest {
    private String merchantId;
    private String merchantTxnNo;
    private String originalTxnNo;
    private String transactionType; // Should be "STATUS"
    private String amount; // optional
    private String aggregatorID;

    // optional

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantTxnNo() {
        return merchantTxnNo;
    }

    public void setMerchantTxnNo(String merchantTxnNo) {
        this.merchantTxnNo = merchantTxnNo;
    }

    public String getOriginalTxnNo() {
        return originalTxnNo;
    }

    public void setOriginalTxnNo(String originalTxnNo) {
        this.originalTxnNo = originalTxnNo;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAggregatorID() {
        return aggregatorID;
    }

    public void setAggregatorID(String aggregatorID) {
        this.aggregatorID = aggregatorID;
    }
}



package com.example.VTM.ICICI.entity;


import lombok.Data;

@Data
public class InitiateSaleRequest {
    private String merchantId;
    private String merchantTxnNo;
    private String amount;
    private String currencyCode;
    private String payType;
    private String customerEmailID;
    private String customerMobileNo;
    private String transactionType;
    private String txnDate;

    private String returnURL;
    private String addlParam1;
    private String addlParam2;
    private String secureHash;
}

package com.example.VTM.ICICI.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequest {
    private String merchantId;
    private String originalTxnNo;
    private String amount;
    private String transactionType;
}

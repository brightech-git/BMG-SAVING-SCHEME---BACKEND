package com.example.VTM.ICICI;


public class PaymentKitConfig {
    private String merchantId;
    private String secretKey;
    private String returnUrl;
    private String refundUrl;
    private String aggregatorId;
    private String statusUrl;
    private String redirectUrl;
    private String payPhiUrl;
    private String initiateSaleUrl;
    private boolean active;  // or Boolean if you expect null possibility

    // getters & setters
    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
    public String getSecretKey() { return secretKey; }
    public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
    public String getReturnUrl() { return returnUrl; }
    public void setReturnUrl(String returnUrl) { this.returnUrl = returnUrl; }
    public String getRefundUrl() { return refundUrl; }
    public void setRefundUrl(String refundUrl) { this.refundUrl = refundUrl; }
    public String getAggregatorId() { return aggregatorId; }
    public void setAggregatorId(String aggregatorId) { this.aggregatorId = aggregatorId; }
    public String getStatusUrl() { return statusUrl; }
    public void setStatusUrl(String statusUrl) { this.statusUrl = statusUrl; }
    public String getRedirectUrl() { return redirectUrl; }
    public void setRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }
    public String getPayPhiUrl() { return payPhiUrl; }
    public void setPayPhiUrl(String payPhiUrl) { this.payPhiUrl = payPhiUrl; }
    public String getInitiateSaleUrl() { return initiateSaleUrl; }
    public void setInitiateSaleUrl(String initiateSaleUrl) { this.initiateSaleUrl = initiateSaleUrl; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}

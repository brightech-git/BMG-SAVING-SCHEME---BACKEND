package com.example.VTM.ICICI;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentKitService {

    private final JdbcTemplate jdbcTemplate;

    public PaymentKitService(@Qualifier("firstJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public PaymentKitConfig getActiveConfig() {
        String sql = "SELECT MerchantId, SecretKey, Return_Url, Refund_Url, AggregatorId, " +
                "Status_Url, Redirect_Url, PayPhi_Url, InitiateSale_Url, Active, Cash_Payment " + // 🆕 added
                "FROM payment_kit WHERE Active = 1";

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            PaymentKitConfig cfg = new PaymentKitConfig();
            cfg.setMerchantId(rs.getString("MerchantId"));
            cfg.setSecretKey(rs.getString("SecretKey"));
            cfg.setReturnUrl(rs.getString("Return_Url"));
            cfg.setRefundUrl(rs.getString("Refund_Url"));
            cfg.setAggregatorId(rs.getString("AggregatorId"));
            cfg.setStatusUrl(rs.getString("Status_Url"));
            cfg.setRedirectUrl(rs.getString("Redirect_Url"));
            cfg.setPayPhiUrl(rs.getString("PayPhi_Url"));
            cfg.setInitiateSaleUrl(rs.getString("InitiateSale_Url"));
            cfg.setActive(rs.getBoolean("Active"));
            cfg.setCashPayment(rs.getBoolean("Cash_Payment")); // 🆕 set new column
            return cfg;
        });
    }
}

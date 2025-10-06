package com.example.VTM.ICICI;



import com.example.VTM.ICICI.entity.InitiateSaleRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class SecureHashGenerator {

    public static String generateHash(InitiateSaleRequest request, String secretKey) throws Exception {
        String rawString =
                request.getAddlParam1() +
                        request.getAddlParam2() +
                        request.getAmount() +
                        request.getCurrencyCode() +
                        request.getCustomerEmailID() +
                        request.getCustomerMobileNo() +
                        request.getMerchantId() +
                        request.getMerchantTxnNo() +
                        request.getPayType() +
                        request.getReturnURL() +
                        request.getTransactionType() +
                        request.getTxnDate();

        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        byte[] hashBytes = sha256_HMAC.doFinal(rawString.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }

        return hexString.toString();
    }
}

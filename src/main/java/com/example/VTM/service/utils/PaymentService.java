package com.example.VTM.service.utils;

import com.example.VTM.model.Customer;
import com.example.VTM.model.PaymentRequest;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.math.BigDecimal;




@Service
public class PaymentService {

    @Value("${razorpay.key_id}")
    private String keyId;

    @Value("${razorpay.key_secret}")
    private String keySecret;

    private final JdbcTemplate jdbcTemplate;

    public PaymentService(@Qualifier("firstJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> createPaymentLink(PaymentRequest paymentRequest) {
        Map<String, Object> response = new HashMap<>();

        try {
            RazorpayClient razorpay = new RazorpayClient(keyId, keySecret);

            String orderId = paymentRequest.getOrderId() != null && !paymentRequest.getOrderId().isEmpty()
                    ? paymentRequest.getOrderId()
                    : generateUniqueOrderId();

            JSONObject options = new JSONObject();
            BigDecimal amountInPaise = paymentRequest.getAmount().multiply(BigDecimal.valueOf(100));
            options.put("amount", amountInPaise);
            options.put("currency", "INR");
            options.put("reference_id", orderId);
            options.put("description", "AKJ Jewellery Payment");

            Customer customer = paymentRequest.getCustomer();
            if (customer != null) {
                JSONObject customerJson = new JSONObject();
                customerJson.put("name", customer.getName());
                customerJson.put("contact", customer.getContact());
                options.put("customer", customerJson);
            } else {
                response.put("error", "Customer details are missing");
                return response;
            }

            JSONObject notify = new JSONObject();
            notify.put("sms", true);
            notify.put("email", true);
            options.put("notify", notify);

            options.put("callback_url", "https://your-app.com/payment-success");
            options.put("callback_method", "get");

            PaymentLink paymentLink = razorpay.paymentLink.create(options);

            // Store Razorpay payment_link ID (not short URL!)
            String sql = "INSERT INTO AppPayment_record " +
                    "(order_id, name, contact, REGNO, GROUPCODE, amount, payment_link, status, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            jdbcTemplate.update(sql,
                    orderId,
                    customer.getName(),
                    customer.getContact(),
                    customer.getREGNO(),
                    customer.getGROUPCODE(),
                    paymentRequest.getAmount(),
                    paymentLink.get("id"),  // Correct ID for later fetch
                    "CREATED",
                    LocalDateTime.now()
            );

            // Send short_url back to frontend
            response.put("payment_link", paymentLink.get("short_url"));
            response.put("order_id", orderId);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Failed to create payment link: " + e.getMessage());
        }

        return response;
    }

    public Map<String, Object> verifyAndUpdatePaymentStatus(String orderId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Fetch the Razorpay payment link ID from the database
            String getLinkIdSql = "SELECT payment_link FROM AppPayment_record WHERE order_id = ?";
            String razorpayLinkId = jdbcTemplate.queryForObject(getLinkIdSql, String.class, orderId);

            if (razorpayLinkId == null || razorpayLinkId.isEmpty()) {
                response.put("error", "Payment link ID not found for the given Order ID");
                return response;
            }

            // Initialize Razorpay client
            RazorpayClient razorpay = new RazorpayClient(keyId, keySecret);

            // Fetch the payment link details using the Razorpay API
            PaymentLink link = razorpay.paymentLink.fetch(razorpayLinkId);
            String status = link.get("status");

            // Update the payment status based on the Razorpay response
            if ("paid".equalsIgnoreCase(status)) {
                updateStatus(orderId, "SUCCESS");
                response.put("message", "Payment successful. Status updated to SUCCESS.");
            } else if ("failed".equalsIgnoreCase(status)) {
                updateStatus(orderId, "CANCEL");
                response.put("message", "Payment failed. Status updated to CANCEL.");
            } else {
                response.put("message", "Payment not completed. Current Razorpay status: " + status);
            }

            response.put("order_id", orderId);
            response.put("razorpay_status", status);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Error verifying payment: " + e.getMessage());
        }

        return response;
    }

    private void updateStatus(String orderId, String status) {
        String updateSql = "UPDATE AppPayment_record SET status = ? WHERE order_id = ?";
        jdbcTemplate.update(updateSql, status, orderId);
    }

    private String generateUniqueOrderId() {
        return "ORDER-" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10).toUpperCase();
    }
}

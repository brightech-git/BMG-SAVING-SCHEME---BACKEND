package com.example.VTM.ICICI;

import com.example.VTM.ICICI.entity.InitiateSaleRequest;
import com.example.VTM.ICICI.entity.RefundRequest;
import com.example.VTM.ICICI.entity.StatusRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/v1/payment")
@CrossOrigin(origins = {
        "https://bmgjewellers.com",
        "https://admin.bmgjewellers.com",
        "http://localhost:3000",
        "https://qa.phicommerce.com",
        "https://secure-ptg.phicommerce.com"
})
@Slf4j
public class PayphiController {

    private final RestTemplate restTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final PaymentKitService paymentKitService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    public PayphiController(RestTemplate restTemplate,
                            @Qualifier("firstJdbcTemplate") JdbcTemplate jdbcTemplate,
                            PaymentKitService paymentKitService) {
        this.restTemplate = restTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.paymentKitService = paymentKitService;
    }

    /**
     * Initiate online or cash payment
     */
    @PostMapping("/initiate-sale")
    public ResponseEntity<?> initiateSale(@RequestHeader("Authorization") String authHeader,
                                          @RequestBody InitiateSaleRequest request) {
        try {
            PaymentKitConfig config = paymentKitService.getActiveConfig();

            // Extract contact and email from JWT token
            String token = authHeader.replace("Bearer ", "").trim();
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret.getBytes())
                    .parseClaimsJws(token)
                    .getBody();

            String contact = claims.get("contact", String.class);
            String email = claims.get("email", String.class);

            request.setCustomerMobileNo(contact);
            request.setCustomerEmailID(email);

            // Generate order_id if not passed
            if (!StringUtils.hasText(request.getMerchantTxnNo())) {
                request.setMerchantTxnNo("ORD-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase());
            }

            // === Cash Payment Handling ===
            if ("CASH".equalsIgnoreCase(request.getPayType())) {

                if (!StringUtils.hasText(request.getName())) {
                    return ResponseEntity.badRequest().body("Name is required for cash payment");
                }

                String status = "PENDING";

                String insertSql = "INSERT INTO AppPayment_record " +
                        "(order_id, name, contact, REGNO, GROUPCODE, amount, status, payment_mode, created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET())";

                String regNo = request.getAddlParam1();     // frontend must pass
                String groupCode = request.getAddlParam2(); // frontend must pass

                jdbcTemplate.update(insertSql,
                        request.getMerchantTxnNo(),
                        request.getName(),
                        request.getCustomerMobileNo(),
                        regNo,
                        groupCode,
                        request.getAmount(),
                        status,
                        "CASH"
                );

                Map<String, Object> resp = new HashMap<>();
                resp.put("orderId", request.getMerchantTxnNo());
                resp.put("status", status);
                resp.put("message", "Cash payment recorded offline");
                return ResponseEntity.ok(resp);
            }

            // === Online Payment Handling ===
            if (!config.isActive()) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Payment kit configuration is not active for online payments");
            }

            request.setMerchantId(config.getMerchantId());
            request.setReturnURL(config.getReturnUrl());

            if (!StringUtils.hasText(request.getTxnDate())) {
                String txnDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                request.setTxnDate(txnDate);
            }

            // Map human-readable payType to PayPhi code
            switch (request.getPayType().toUpperCase()) {
                case "ONLINE":
                    request.setPayType("0");
                    break;
                case "CARD":
                    request.setPayType("1");
                    break;
                case "UPI":
                    request.setPayType("2");
                    break;
                default:
                    return ResponseEntity.badRequest().body("Invalid payType for online payment");
            }

            // Generate secure hash
            String secureHash = SecureHashGenerator.generateHash(request, config.getSecretKey());
            request.setSecureHash(secureHash);

            // Insert initiate_sale_record in DB
            String insertSql = "INSERT INTO initiate_sale_record " +
                    "(merchant_id, merchant_txn_no, amount, currency_code, pay_type, " +
                    "customer_email_id, customer_mobile_no, transaction_type, txn_date, return_url, " +
                    "addl_param1, addl_param2, secure_hash) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            jdbcTemplate.update(insertSql,
                    request.getMerchantId(),
                    request.getMerchantTxnNo(),
                    request.getAmount(),
                    request.getCurrencyCode(),
                    request.getPayType(),
                    request.getCustomerEmailID(),
                    request.getCustomerMobileNo(),
                    request.getTransactionType(),
                    request.getTxnDate(),
                    request.getReturnURL(),
                    request.getAddlParam1(),
                    request.getAddlParam2(),
                    request.getSecureHash()
            );

            // Call PayPhi API
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<InitiateSaleRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    config.getInitiateSaleUrl(),
                    entity,
                    String.class
            );

            // Save response
            String saveResponseSql = "INSERT INTO initiate_sale_response " +
                    "(merchant_txn_no, status_code, response_body) VALUES (?, ?, ?)";
            jdbcTemplate.update(saveResponseSql,
                    request.getMerchantTxnNo(),
                    response.getStatusCodeValue(),
                    response.getBody()
            );

            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());

        } catch (Exception e) {
            log.error("Error initiating sale", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error initiating sale: " + e.getMessage());
        }
    }






    @PostMapping("/redirect-url")
public ResponseEntity<String> getRedirectUrl(@RequestBody Map<String, String> body) {
    String tranCtx = body.get("tranCtx");

    if (tranCtx == null || tranCtx.isEmpty()) {
        return ResponseEntity.badRequest().body("tranCtx is required");
    }

    try {
        // Fetch the active config from DB
        PaymentKitConfig config = paymentKitService.getActiveConfig();

        if (config == null || !config.isActive()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("No active payment kit configuration found.");
        }

        String baseRedirectUrl = config.getRedirectUrl();

        if (baseRedirectUrl == null || baseRedirectUrl.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Redirect URL not configured in payment kit.");
        }

        // Append tranCtx as query param
        String finalRedirectUrl = baseRedirectUrl + "?tranCtx=" + URLEncoder.encode(tranCtx, StandardCharsets.UTF_8);

        return ResponseEntity.ok(finalRedirectUrl);

    } catch (Exception e) {
        // Log and return internal error
        e.printStackTrace();  // Or use logger
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error retrieving redirect URL: " + e.getMessage());
    }
}

    @PostMapping("/status")
    public ResponseEntity<?> checkPaymentStatus(@RequestBody StatusRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            PaymentKitConfig config = paymentKitService.getActiveConfig();
            if (!config.isActive()) {
                result.put("orderStatus", "CONFIG_INACTIVE");
                result.put("message", "Payment kit configuration is not active");
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(result);
            }

            request.setMerchantId(config.getMerchantId());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<StatusRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    config.getStatusUrl(),
                    HttpMethod.POST,
                    entity,
                    String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(response.getBody());
            result.put("payphiResponse", jsonResponse);

            String txnStatus = jsonResponse.path("txnStatus").asText();
            String merchantTxnNo = jsonResponse.path("merchantTxnNo").asText();

            String selectSql = "SELECT * FROM AppPayment_record WHERE order_id = ? ";
            List<Map<String, Object>> orders = jdbcTemplate.queryForList(selectSql, merchantTxnNo);

            if ("SUC".equalsIgnoreCase(txnStatus)) {
                if (!orders.isEmpty()) {
                    String updateSql = "UPDATE AppPayment_record SET status = ?, updated_at = SYSDATETIMEOFFSET() WHERE order_id = ?";
                    jdbcTemplate.update(updateSql, "PAID", merchantTxnNo);
                    result.put("orderStatus", "PAID");
                    result.put("message", "Payment successful. Order status updated");
                } else {
                    result.put("orderStatus", "NOT_FOUND");
                    result.put("message", "Order not found or not in PENDING state");
                }
            } else if ("REJ".equalsIgnoreCase(txnStatus)) {
                if (!orders.isEmpty()) {
                    String updateSql = "UPDATE AppPayment_record SET status = ?, updated_at = SYSDATETIMEOFFSET() WHERE order_id = ?";
                    jdbcTemplate.update(updateSql, "FAILURE", merchantTxnNo);
                    result.put("orderStatus", "FAILURE");
                    result.put("message", "Payment failed. Order status updated to FAILURE");
                } else {
                    result.put("orderStatus", "NOT_FOUND");
                    result.put("message", "Order not found or not in PENDING state");
                }
            } else {
                result.put("orderStatus", "PENDING");
                result.put("message", "Payment not successful. Status: " + txnStatus);
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            result.put("orderStatus", "ERROR");
            result.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }


    @PostMapping("/complete-cash-payment")
    public ResponseEntity<?> completeCashPayment(@RequestBody Map<String, String> body) {
        try {
            String orderId = body.get("orderId");
            if (!StringUtils.hasText(orderId)) {
                return ResponseEntity.badRequest().body("orderId is required");
            }

            // Check if order exists and is pending
            String selectSql = "SELECT * FROM AppPayment_record WHERE order_id = ? AND status = 'PENDING'";
            List<Map<String, Object>> orders = jdbcTemplate.queryForList(selectSql, orderId);

            if (orders.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Cash payment order not found or already completed");
            }

            // Update status to PAID
            String updateSql = "UPDATE AppPayment_record SET status = 'PAID', updated_at = SYSDATETIMEOFFSET() WHERE order_id = ?";
            jdbcTemplate.update(updateSql, orderId);

            Map<String, Object> resp = new HashMap<>();
            resp.put("orderId", orderId);
            resp.put("status", "PAID");
            resp.put("message", "Cash payment marked as completed");

            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            log.error("Error completing cash payment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error completing cash payment: " + e.getMessage());
        }
    }


    @PostMapping("/refund")
    public ResponseEntity<?> refundTransaction(@RequestBody RefundRequest request) {
        try {
            PaymentKitConfig config = paymentKitService.getActiveConfig();
            if (!config.isActive()) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Payment kit configuration is not active");
            }

            // Generate refund txn no
            String refundTxnNo = "REF-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();

            String dataToHash = request.getAmount()
                    + config.getMerchantId()
                    + refundTxnNo
                    + request.getOriginalTxnNo()
                    + request.getTransactionType();

            String secureHash = RefundSecureHashGenerator.generateRefundHash(dataToHash, config.getSecretKey());

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("merchantID", config.getMerchantId());
            formData.add("merchantTxnNo", refundTxnNo);
            formData.add("originalTxnNo", request.getOriginalTxnNo());
            formData.add("amount", request.getAmount());
            formData.add("transactionType", request.getTransactionType());
            formData.add("secureHash", secureHash);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    config.getRefundUrl(),
                    entity,
                    String.class);

            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            log.error("Refund failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Refund Failed: " + e.getMessage());
        }
    }



    }
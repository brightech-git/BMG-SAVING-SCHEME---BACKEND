package com.example.VTM.controller;

import com.example.VTM.model.PaymentRequest;
import com.example.VTM.service.utils.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


//@RestController
//@RequestMapping("/api/payment")
//public class PaymentController {
//
//    private final PaymentService paymentService;
//
//    private String jwtSecret;
//
//    public PaymentController(PaymentService paymentService) {
//        this.paymentService = paymentService;
//    }
//
//    @PostMapping("/create-payment-link")
//    public Map<String, Object> createPaymentLink(@RequestBody PaymentRequest paymentRequest) {
//        return paymentService.createPaymentLink(paymentRequest);
//    }
//
//    @GetMapping("/verify")
//    public ResponseEntity<Map<String, Object>> verifyPayment(@RequestParam String orderId) {
//        Map<String, Object> result = paymentService.verifyAndUpdatePaymentStatus(orderId);
//        return ResponseEntity.ok(result);
//    }
//
//    }

@RestController
@RequestMapping("/api/orders")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Place Order (skip Razorpay)
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody PaymentRequest paymentRequest) {
        return ResponseEntity.ok(paymentService.createOrder(paymentRequest));
    }

    // Cancel Order
    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(paymentService.cancelOrder(orderId));
    }
}


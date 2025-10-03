package com.example.VTM.userAdministartion.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class SmsOtpService {

    private static final Logger logger = LoggerFactory.getLogger(SmsOtpService.class);

    @Value("${sms.api.url}")
    private String smsApiUrl;

    @Value("${sms.api.authKey}")
    private String authKey;

    @Value("${sms.api.senderId}")
    private String senderId;

    @Value("${sms.api.templateId}")
    private String templateId;

    public void sendOtpSms(String contactNumber, String generatedOtp, String hashKey) {
        if (contactNumber == null || !contactNumber.matches("\\d{10}")) {
            logger.error("Invalid mobile number: {}", contactNumber);
            throw new IllegalArgumentException("Invalid mobile number: " + contactNumber);
        }

        String fullMobileNumber = "91" + contactNumber;
        logger.info("Preparing to send OTP to: {}", fullMobileNumber);

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            String template = "BMG JEWELLERS PRIVATE LIMITED: Your OTP for login is {#var#}. " +
                    "Please enter this code in the app to continue. " +
                    "This OTP is valid for 10 minutes. " +
                    "Do not share it with anyone. {#var#}";

            System.out.println(template);

            String messageText = template.replaceFirst("\\{#var#}", generatedOtp);
            if (hashKey != null && !hashKey.isBlank()) {
                messageText = messageText.replaceAll("\\{#var#\\}", hashKey);
            } else {
                messageText = messageText.replaceAll("\\{#var#\\}", "");
            }

            System.out.println(hashKey);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("apikey", authKey);
            params.add("senderid", senderId);
            params.add("number", fullMobileNumber);
            params.add("message", messageText);
            params.add("templateid", templateId);
            params.add("route", "TA");
            params.add("type", "text");
            params.add("format", "json");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(smsApiUrl, request, String.class);

            if (response.getBody() != null && response.getBody().contains("\"status\":\"Success\"")) {
                logger.info("SMS sent successfully to {} | OTP: {}", fullMobileNumber, generatedOtp);
            } else {
                logger.error("SMS sending failed to {} | Response: {}", fullMobileNumber, response.getBody());
                throw new RuntimeException("SMS failed: " + response.getBody());
            }

        } catch (Exception e) {
            logger.error("Exception while sending SMS to {} | Error: {}", fullMobileNumber, e.getMessage(), e);
            throw new RuntimeException("Failed to send SMS: " + e.getMessage(), e);
        }
    }
}

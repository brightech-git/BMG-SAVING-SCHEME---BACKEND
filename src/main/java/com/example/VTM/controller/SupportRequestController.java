package com.example.VTM.controller;


import com.example.VTM.entity.SupportRequest;
import com.example.VTM.service.utils.SupportRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/support")

public class SupportRequestController {

    @Autowired
    private SupportRequestRepository repository;

    @PostMapping("/submit")
    public ResponseEntity<?> submitRequest(@RequestBody SupportRequest request) {
        if (request.getEnquiryType() == null || request.getSubject() == null || request.getDescription() == null) {
            return ResponseEntity.badRequest().body("All fields are required");
        }

        Long id = repository.save(request);
        return ResponseEntity.ok("Ticket submitted successfully! ID: " + id);
    }
}

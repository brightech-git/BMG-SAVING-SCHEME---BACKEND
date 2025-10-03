package com.example.VTM.entity;


public class SupportRequest {
    private Long id;
    private String enquiryType;
    private String subject;
    private String description;

    public SupportRequest() {}
    public SupportRequest(String enquiryType, String subject, String description) {
        this.enquiryType = enquiryType;
        this.subject = subject;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEnquiryType() { return enquiryType; }
    public void setEnquiryType(String enquiryType) { this.enquiryType = enquiryType; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

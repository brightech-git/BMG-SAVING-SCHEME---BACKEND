package com.example.VTM.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class Customer {

    @JsonProperty("name")
    private String name;

    @JsonProperty("contact")
    private String contact;

    @JsonProperty("REGNO")
    private String REGNO;

    @JsonProperty("GROUPCODE")
    private String GROUPCODE;


    public Customer(String name, String contact, String REGNO, String GROUPCODE) {
        this.name = name;
        this.contact = contact;
        this.REGNO = REGNO;
        this.GROUPCODE = GROUPCODE;
    }

    public Customer() {
    }
// Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getREGNO() {
        return REGNO;
    }

    public void setREGNO(String REGNO) {
        this.REGNO = REGNO;
    }

    public String getGROUPCODE() {
        return GROUPCODE;
    }

    public void setGROUPCODE(String GROUPCODE) {
        this.GROUPCODE = GROUPCODE;
    }
}

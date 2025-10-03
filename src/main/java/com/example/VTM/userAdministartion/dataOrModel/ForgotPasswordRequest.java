package com.example.VTM.userAdministartion.dataOrModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ForgotPasswordRequest {

    @JsonProperty(value = "contactNumber", required = true)
    private String contactNumber;
    @JsonProperty(value = "hashKey", required = true)
    private String hashKey;

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }
}


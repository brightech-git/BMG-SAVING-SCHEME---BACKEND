package com.example.VTM.userAdministartion.dataOrModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResetPasswordRequest {

    @JsonProperty("contactNumber")
    private String contactNumber;

    @JsonProperty("otp")
    private String otp;

    @JsonProperty("newPassword")
    private String newPassword;

    public ResetPasswordRequest() {
    }

    public ResetPasswordRequest(String contactNumber, String otp, String newPassword) {
        this.contactNumber = contactNumber;
        this.otp = otp;
        this.newPassword = newPassword;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}

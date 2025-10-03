package com.example.VTM.userAdministartion.dataOrModel;



import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLoginData {

    @JsonProperty(value = "contactOrEmailOrUsername")
    private String contactOrEmailOrUsername;

    @JsonProperty(value = "password")
    private String password;

    @JsonProperty(value = "errorMessage")
    private String errorMessage;

    public UserLoginData() {
    }

    public UserLoginData(String contactOrEmailOrUsername, String password) {
        this.contactOrEmailOrUsername = contactOrEmailOrUsername;
        this.password = password;
    }

    public UserLoginData(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getContactOrEmailOrUsername() {
        return contactOrEmailOrUsername;
    }

    public void setContactOrEmailOrUsername(String contactOrEmailOrUsername) {
        this.contactOrEmailOrUsername = contactOrEmailOrUsername;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

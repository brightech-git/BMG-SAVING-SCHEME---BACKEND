package com.example.VTM.userAdministartion.dataOrModel;

public class GoogleLoginRequest {
    private String email;
    private String name;
    private String pictureUrl; // can be ignored in backend

    // Getters & Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPictureUrl() { return pictureUrl; }
    public void setPictureUrl(String pictureUrl) { this.pictureUrl = pictureUrl; }
}


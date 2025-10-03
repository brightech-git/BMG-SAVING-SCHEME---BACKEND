package com.example.VTM.userAdministartion.entityOrDomain;


import jakarta.persistence.*;

@Entity
@Table(name = "scheme_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 30)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "contact_number", nullable = false, unique = true)
    private String contactNumber;

    @Column(name = "status")
    private String status; // NEW FIELD

    @Column(name = "social_media")
    private String socialMedia; // NEW FIELD

    public User() {
    }

    public User(Long id, String username, String email, String password, String contactNumber, String socialMedia) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.contactNumber = contactNumber;
        this.socialMedia = socialMedia;
    }

    public User(String username, String email, String password, String contactNumber, String socialMedia) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.contactNumber = contactNumber;
        this.socialMedia = socialMedia;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSocialMedia() {
        return socialMedia;
    }

    public void setSocialMedia(String socialMedia) {
        this.socialMedia = socialMedia;
    }
}

package com.sys.system.Class;
public class User2 {
    private String email;
    private String phone;
    private String fullName;
    private String imageUrl;

    public User2() {}

    public User2(String email, String phone, String fullName, String imageUrl) {
        this.email = email;
        this.phone = phone;
        this.fullName = fullName;
        this.imageUrl = imageUrl;
    }

    // Getters and setters for email, phone, fullName, and imageUrl

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

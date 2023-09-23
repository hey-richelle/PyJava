package com.sys.system;
public class Admin3 {
    private String id;
    private String email;
    private String fullName;
    private String imageUrl;
    private String phone;

    public Admin3() {
        // Default constructor required for Firebase
    }

    public Admin3(String id, String email, String fullName, String imageUrl, String phone) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.imageUrl = imageUrl;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

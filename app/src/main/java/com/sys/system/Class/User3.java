package com.sys.system.Class;
public class User3 {
    private String fullname;
    private String imageUrl;
    private String imageContent;
    private String email; // New member variable to store email

    public User3() {
        // Empty constructor required for Firebase
    }

    public User3(String fullname, String imageUrl, String imageContent, String email) {
        this.fullname = fullname;
        this.imageUrl = imageUrl;
        this.imageContent = imageContent;
        this.email = email;
    }

    public String getFullName() {
        return fullname;
    }

    public void setFullName(String fullname) {
        this.fullname = fullname;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageContent() {
        return imageContent;
    }

    public void setImageContent(String imageContent) {
        this.imageContent = imageContent;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

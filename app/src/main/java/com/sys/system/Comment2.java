package com.sys.system;
public class Comment2 {
    private String fullName;
    private String messageText;
    private String imageUrl;
    private String profession;
    private String email;

    // Empty constructor (required for Firebase)
    public Comment2() {
    }

    public Comment2(String fullName, String messageText, String imageUrl, String profession, String email) {
        this.fullName = fullName;
        this.messageText = messageText;
        this.imageUrl = imageUrl;
        this.profession = profession;
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

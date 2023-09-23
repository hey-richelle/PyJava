package com.sys.system;
public class Message {
    private String messageId;
    private String name;
    private String profession;
    private String message;
    private String date;
    private String time;
    private String imageUrl;
    private String imageContent;
    private boolean unsent;
    private String email; // New member variable to store email

    public Message() {
        // Empty constructor required for Firebase
    }

    public Message(String messageId, String name, String profession, String message, String date, String time, String imageUrl, String imageContent, boolean unsent, String email) {
        this.messageId = messageId;
        this.name = name;
        this.profession = profession;
        this.message = message;
        this.date = date;
        this.time = time;
        this.imageUrl = imageUrl;
        this.imageContent = imageContent;
        this.unsent = unsent;
        this.email = email;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public boolean isUnsent() {
        return unsent;
    }

    public void setUnsent(boolean unsent) {
        this.unsent = unsent;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isRead() {
        return !unsent;
    }
}

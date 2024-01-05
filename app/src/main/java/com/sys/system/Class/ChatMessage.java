package com.sys.system.Class;


public class ChatMessage {
    private String messageId;
    private String sender;
    private String message;
    private long timestamp;
    private String uid;
    private String fullName;
    private String imageUrl;
    private String email; // Adding email field
    private String imageContent; // Adding imageContent field
    private String date; // Adding date field

    public ChatMessage() {
        // Empty constructor required for Firebase
    }

    public ChatMessage(String messageId, String sender, String message, long timestamp, String uid, String fullName, String imageUrl, String email, String imageContent, String date) {
        this.messageId = messageId;
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
        this.uid = uid;
        this.fullName = fullName;
        this.imageUrl = imageUrl;
        this.email = email;
        this.imageContent = imageContent;
        this.date = date;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageContent() {
        return imageContent;
    }

    public void setImageContent(String imageContent) {
        this.imageContent = imageContent;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

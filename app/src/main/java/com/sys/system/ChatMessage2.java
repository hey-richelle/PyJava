package com.sys.system;

public class ChatMessage2 {
    private String messageId;
    private String sender;
    private String message;
    private long timestamp;
    private String uid;
    private String name;
    private String image;
    private String email; // Adding email field
    private String imageContent; // Adding imageContent field

    public ChatMessage2() {
        // Empty constructor required for Firebase
    }

    public ChatMessage2(String messageId, String sender, String message, long timestamp, String uid, String name, String image, String email, String imageContent) {
        this.messageId = messageId;
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
        this.uid = uid;
        this.name = name;
        this.image = image;
        this.email = email;
        this.imageContent = imageContent;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
}

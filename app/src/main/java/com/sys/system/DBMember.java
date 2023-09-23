package com.sys.system;
public class DBMember {
    private String name;
    private String image;
    private String profession;
    private String imageContent;
    private String email; // New member variable to store email

    public DBMember() {
        // Default constructor required for calls to DataSnapshot.getValue(DBMember.class)
    }

    public DBMember(String name, String image, String profession, String imageContent, String email) {
        this.name = name;
        this.image = image;
        this.profession = profession;
        this.imageContent = imageContent;
        this.email = email;
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

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
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

package com.sys.system;

public class Students {
    private String id;
    private String email;
    private String image;
    private String name;
    private String username;

    public Students() {
        // Default constructor required for Firebase
    }

    public Students(String id, String email, String image, String name, String username) {
        this.id = id;
        this.email = email;
        this.image = image;
        this.name = name;
        this.username = username;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

package com.sys.system.Class;

public class Link {
    private String link;

    public Link() {
        // Default constructor required for Firebase Realtime Database
    }

    public Link(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }
}

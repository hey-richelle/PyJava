package com.sys.system.Class;
public class ReferenceLink {
    private String link;

    public ReferenceLink() {
        // Default constructor required for Firebase Realtime Database
    }

    public ReferenceLink(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }
}

package com.sys.system;
public class ReferenceModel {
    private String link;

    public ReferenceModel() {
        // Default constructor required for Firebase
    }

    public ReferenceModel(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}

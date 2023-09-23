package com.sys.system;

public class FileModel {
    private String id;
    private String title;
    private String url;
    private String date;
    private String time;
    private long size;
    private String uploader;

    public FileModel() {
    }

    public FileModel(String id, String title, String url, String date, String time, long size, String uploader) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.date = date;
        this.time = time;
        this.size = size;
        this.uploader = uploader;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }
}

package com.example.aws.model;

public class Report {
    public String id;
    public String user_id;
    public String username;
    public String message;
    public String image_url;
    public String status;
    public String timestamp;

    public Report() {
    }

    public Report(String user_id, String username, String message, String image_url, String status, String timestamp) {
        this.user_id = user_id;
        this.username = username;
        this.message = message;
        this.image_url = image_url;
        this.status = status;
        this.timestamp = timestamp;
    }
}

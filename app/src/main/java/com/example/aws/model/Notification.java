package com.example.aws.model;

public class Notification {
    private String userId;
    private String message;
    private String timestamp;

    public Notification() {}

    public Notification(String userId, String message, String timestamp) {
        this.userId = userId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }
}

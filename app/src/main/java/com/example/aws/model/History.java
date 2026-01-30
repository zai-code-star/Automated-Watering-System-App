package com.example.aws.model;

public class History {
    public String user_id;
    public String username;
    public String role;
    public String activity;
    public String timestamp;

    public History() {}

    public History(String user_id, String username, String role, String activity, String timestamp) {
        this.user_id = user_id;
        this.username = username;
        this.role = role;
        this.activity = activity;
        this.timestamp = timestamp;
    }
}

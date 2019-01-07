package com.example.vishal.msg_app;

public class User {
    private String number;
    private String userId;



    public User(String number, String userId) {
        this.number = number;
        this.userId = userId;
    }

    public User() {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

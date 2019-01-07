package com.example.vishal.msg_app;

public class MainContact {
    private String name;
    private Long number;
    private String userId;

    public MainContact(String name, Long number,String userId) {
        this.name = name;
        this.number = number;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }
}

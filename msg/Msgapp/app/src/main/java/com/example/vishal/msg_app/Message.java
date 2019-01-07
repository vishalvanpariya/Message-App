package com.example.vishal.msg_app;

public class Message {
    private String msg;
    private String time;
    private boolean flag;

    public Message() {
    }

    public Message(String msg, String time, boolean flag) {
        this.msg = msg;
        this.time = time;
        this.flag = flag;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}

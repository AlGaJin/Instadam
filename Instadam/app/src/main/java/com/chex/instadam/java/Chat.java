package com.chex.instadam.java;

import java.util.List;

public class Chat {
    private int id;
    private int userImg;
    private String tmpMsg;
    private String username;
    private String time;

    public Chat(int id, int userImg, String tmpMsg, String username, String time) {
        this.id = id;
        this.userImg = userImg;
        this.tmpMsg = tmpMsg;
        this.username = username;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public int getUserImg() {
        return userImg;
    }
    public String getTmpMsg(){
        return tmpMsg;
    }

    public String getUsername() {
        return username;
    }

    public String getTime() {
        return time;
    }
}

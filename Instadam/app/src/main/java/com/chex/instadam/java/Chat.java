package com.chex.instadam.java;

import java.util.List;

public class Chat {
    private int id;
    private int userId;
    private int otherUserId;

    public Chat(int id, int userId, int otherUserId){
        this.id = id;
        this.userId = userId;
        this.otherUserId = otherUserId;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getOtherUserId() {
        return otherUserId;
    }
}

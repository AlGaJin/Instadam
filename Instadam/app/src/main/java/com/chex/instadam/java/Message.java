package com.chex.instadam.java;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.GregorianCalendar;

public class Message {
    private int chatId;
    private int userId;
    private String msg;
    private Timestamp sendTime;

    public Message(int chatId, int userId, String msg, Timestamp sendTime) {
        this.chatId = chatId;
        this.userId = userId;
        this.msg = msg;
        this.sendTime = sendTime;
    }

    public int getChatId() {
        return chatId;
    }

    public int getUserId() {
        return userId;
    }

    public String getMsg(){
        return msg;
    }

    public Timestamp getSendTime() {
        return sendTime;
    }
}

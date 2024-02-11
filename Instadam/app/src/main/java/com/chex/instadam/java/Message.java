package com.chex.instadam.java;

import java.util.Date;
import java.util.GregorianCalendar;

public class Message {
    private int chat_id;
    private String msg;
    private String time;

    public Message(int chat_id, String msg, String time) {
        this.chat_id = chat_id;
        this.msg = msg;
        this.time = time;
    }

    public int getChat_id() {
        return chat_id;
    }

    public String getMsg() {
        return msg;
    }

    public String getTime() {
        return time;
    }
}

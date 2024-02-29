package com.chex.instadam.java;

import java.sql.Timestamp;

/**
 * Representa un mensaje, recoge todos los datos necesarios de un mensaje
 */
public class Message {
    private int chatId; //Id del chat al que pertenece el mensaje
    private int userId; //Id del usuario que lo envía
    private String msg; //El mensaje en sí
    private Timestamp sendTime; //Momento en el que se ha enviado el mensaje

    public Message(int chatId, int userId, String msg, Timestamp sendTime) {
        this.chatId = chatId;
        this.userId = userId;
        this.msg = msg;
        this.sendTime = sendTime;
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

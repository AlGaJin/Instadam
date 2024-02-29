package com.chex.instadam.java;

/**
 * Representa un chat, almacena los datos necesarios para identificar un chat
 */
public class Chat {
    private int id; //Id del propio chat en la base de datos
    private int userId; //Id del usuario que ha creado el chat
    private int otherUserId; //Id del usuario al que se ha seleccionado para crear el chat

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

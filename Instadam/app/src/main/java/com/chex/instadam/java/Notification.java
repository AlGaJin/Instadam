package com.chex.instadam.java;

/**
 * Representa una notificación, recogiendo los datos necesarios para identificar una notificación
 */
public class Notification {
    private String notiMsg; //Mensaje de la notificación
    private int otherUserProfilePic; //Id del drawable del usuario

    public Notification(String notiMsg, int otherUserProfilePic){
        this.notiMsg = notiMsg;
        this.otherUserProfilePic = otherUserProfilePic;
    }

    public String getNotiMsg() {
        return notiMsg;
    }

    public int getOtherUserProfilePic() {
        return otherUserProfilePic;
    }
}

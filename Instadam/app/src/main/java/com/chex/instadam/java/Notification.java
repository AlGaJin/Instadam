package com.chex.instadam.java;

public class Notification {
    private String notiMsg;
    private int otherUserProfilePic;

    public Notification(String notiMsg, int otherUserProfilePic){
        this.notiMsg = notiMsg;
        this.otherUserProfilePic = otherUserProfilePic;
    }

    public String getNotiMsg() {
        return notiMsg;
    }

    public void setNotiMsg(String notiMsg) {
        this.notiMsg = notiMsg;
    }

    public int getOtherUserProfilePic() {
        return otherUserProfilePic;
    }

    public void setOtherUserProfilePic(int otherUserProfilePic) {
        this.otherUserProfilePic = otherUserProfilePic;
    }
}

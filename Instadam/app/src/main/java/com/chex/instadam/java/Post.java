package com.chex.instadam.java;

import com.chex.instadam.enums.Type;

public class Post {
    private int profilePic;
    private int postImg;
    private String username;
    private String sciName;
    private String commonName;
    private String desc;
    private String date;
    private Type type;

    public Post(int profilePic,int postImg, String username,String sciName, String commonName, String desc, String date, Type type) {
        this.profilePic = profilePic;
        this.postImg = postImg;
        this.username = username;
        this.sciName = sciName;
        this.commonName = commonName;
        this.desc = desc;
        this.date = date;
        this.type = type;
    }

    public int getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(int profilePic) {
        this.profilePic = profilePic;
    }

    public int getPostImg() {
        return postImg;
    }

    public void setPostImg(int postImg) {
        this.postImg = postImg;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSciName() {
        return sciName;
    }

    public void setSciName(String sciName) {
        this.sciName = sciName;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

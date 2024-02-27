package com.chex.instadam.java;

import com.chex.instadam.enums.PostTypes;

import java.sql.Date;

public class Post {
    private int id;
    private int id_publisher;
    private Date publish_date;
    private String title;
    private String dsc;
    private PostTypes postType;
    private String sciName;
    private String cmnName;
    private String fbPostPath;

    public Post(int id, int id_publisher, Date publish_date, String title, String dsc, PostTypes postType,
                String sciName, String cmnName, String fbPostPath) {
        this.id = id;
        this.id_publisher = id_publisher;
        this.publish_date = publish_date;
        this.title = title;
        this.dsc = dsc;
        this.postType = postType;
        this.sciName = sciName;
        this.cmnName = cmnName;
        this.fbPostPath = fbPostPath;
    }

    public int getId() {
        return id;
    }

    public int getId_publisher() {
        return id_publisher;
    }

    public Date getPublish_date() {
        return publish_date;
    }

    public String getTitle() {
        return title;
    }

    public String getDsc() {
        return dsc;
    }

    public String getSciName() {
        return sciName;
    }

    public String getCmnName() {
        return cmnName;
    }

    public PostTypes getPostType() {
        return postType;
    }

    public void setPostType(PostTypes postType) {
        this.postType = postType;
    }

    public String getFbPostPath() {
        return fbPostPath;
    }

    public void setFbPostPath(String fbPostPath) {
        this.fbPostPath = fbPostPath;
    }
}

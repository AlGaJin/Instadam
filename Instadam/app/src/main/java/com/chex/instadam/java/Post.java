package com.chex.instadam.java;

import com.chex.instadam.enums.PostTypes;

import java.sql.Date;

/**
 * Representa una publicación, recoge todos los datos de una publicación
 */
public class Post {
    private int id; //Id en la base de datos de la publicación
    private int id_publisher; //Id del usuario que lo ha publicado
    private Date publish_date;//Fecha en la que se ha publicado
    private String title;//Título de la publicación
    private String dsc;//Descipción de la publicación
    private PostTypes postType; //El tipo de publicación, referiendo a los tres reinos que se utilizan en la aplicación
    private String sciName; //Nombre científico del ser vivo
    private String cmnName; //Nombre común por el que el usuario conoce al ser vivo
    private String fbPostPath; //Ruta en la que se almacena la imagen en FireBase

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

    public String getFbPostPath() {
        return fbPostPath;
    }
}

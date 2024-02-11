package com.chex.instadam.java;

public class User {
    private String nombreUsuario;
    private String email;
    private String password;
    private int profilePic;

    public User(String nombreUsuario, String email, String password, int profilePic) {
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.password = password;
        this.profilePic = profilePic;
    }
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

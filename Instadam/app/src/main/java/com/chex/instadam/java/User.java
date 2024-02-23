package com.chex.instadam.java;

public class User {
    private int id;
    private String username;
    private String email;
    private String profilePic;
    private String dscp;

    public User(int id, String username, String email, String profilePic, String dscp) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.profilePic = profilePic;
        this.dscp = dscp;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getDscp() {
        return dscp;
    }

    public void setDscp(String dscp) {
        this.dscp = dscp;
    }

    public User clone(){
        return new User(id, username, email, profilePic, dscp);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", profilePic='" + profilePic + '\'' +
                ", dscp='" + dscp + '\'' +
                '}';
    }
}

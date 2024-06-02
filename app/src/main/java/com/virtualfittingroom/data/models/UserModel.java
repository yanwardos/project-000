package com.virtualfittingroom.data.models;


public class UserModel {
    private String name, email, avatar;

    public UserModel(String name, String email, String avatar) {
        this.name = name;
        this.email = email;
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return "https://vfr.yanwardos.my.id/avatar/" + avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}

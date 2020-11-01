package com.pumkit.chin.vo;

public class UserData {

    public String user_name;
    public String my_id;
    public String token;
    public String img_url;

    public UserData(){

    }

    public UserData(String username, String my_id, String token, String img_url) {
        this.user_name = username;
        this.my_id = my_id;
        this.token = token;
        this.img_url = img_url;
    }

    public String getUsername() {
        return user_name;
    }

    public void setUsername(String username) {
        this.user_name = username;
    }

    public String getmy_id() {
        return my_id;
    }

    public void setmy_id(String my_id) {
        this.my_id = my_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
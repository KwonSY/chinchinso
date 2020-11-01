package com.pumkit.chin.vo;

public class ProfileData {
    private String sid;
    private String img_url;
    private int look_cnt;
    private int open;

    public ProfileData() {

    }

    public ProfileData(String sid, String img_url, int look_cnt, int open) {
        this.sid = sid;
        this.img_url = img_url;
        this.look_cnt = look_cnt;
        this.open = open;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public int getLook_cnt() {
        return look_cnt;
    }

    public void setLook_cnt(int look_cnt) {
        this.look_cnt = look_cnt;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }
}
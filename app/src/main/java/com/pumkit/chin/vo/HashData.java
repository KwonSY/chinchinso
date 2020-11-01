package com.pumkit.chin.vo;

public class HashData {
    private String sid;
    private String hash_name;
    private String users_num;
    private int look_cnt;
    private int open;

    public HashData() {

    }

    public HashData(String sid, String hash_name) {
        this.sid = sid;
        this.hash_name = hash_name;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getHash_name() {
        return hash_name;
    }

    public void setHash_name(String hash_name) {
        this.hash_name = hash_name;
    }

    public String getUsers_num() {
        return users_num;
    }

    public void setUsers_num(String users_num) {
        this.users_num = users_num;
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
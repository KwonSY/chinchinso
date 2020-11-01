package com.pumkit.chin.vo;

public class MatchData {
    private String sid;
    private String user_name;
    private String match_img;
    private int cnt_blind;

    public MatchData() {

    }

    public MatchData(String sid, String user_name, int cnt_blind) {
        this.sid = sid;
        this.user_name = user_name;
        this.cnt_blind = cnt_blind;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getMatch_img() {
        return match_img;
    }

    public void setMatch_img(String match_img) {
        this.match_img = match_img;
    }

    public int getCnt_blind() {
        return cnt_blind;
    }

    public void setCnt_blind(int cnt_blind) {
        this.cnt_blind = cnt_blind;
    }
}
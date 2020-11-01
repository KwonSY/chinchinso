package com.pumkit.chin.vo;

public class DatedListData {
    private String date_date;
    private String match_id;
    private String match_name;
    private String match_pic;
    private String blind_id;
    private String blind_name;
    private String blind_pic;
    private String blind_fire;

    public DatedListData(String date_date, String match_id, String match_name, String match_pic, String blind_id, String blind_name, String blind_pic, String blind_fire) {
        this.match_id = match_id;
        this.match_name = match_name;
        this.match_pic = match_pic;

        this.blind_id = blind_id;
        this.blind_name = blind_name;
        this.blind_pic = blind_pic;
        this.blind_fire = blind_fire;
    }

    public String getDate_date() {
        return date_date;
    }

    public void setDate_date(String date_date) {
        this.date_date = date_date;
    }

    public String getMatch_id() {
        return match_id;
    }

    public void setMatch_id(String match_id) {
        this.match_id = match_id;
    }

    public String getMatch_name() {
        return match_name;
    }

    public void setMatch_name(String match_name) {
        this.match_name = match_name;
    }

    public String getMatch_pic() {
        return match_pic;
    }

    public void setMatch_pic(String match_pic) {
        this.match_pic = match_pic;
    }

    public String getBlind_id() {
        return blind_id;
    }

    public void setBlind_id(String blind_id) {
        this.blind_id = blind_id;
    }

    public String getBlind_name() {
        return blind_name;
    }

    public void setBlind_name(String blind_name) {
        this.blind_name = blind_name;
    }

    public String getBlind_pic() {
        return blind_pic;
    }

    public void setBlind_pic(String blind_pic) {
        this.blind_pic = blind_pic;
    }

    public String getBlind_fire() {
        return blind_fire;
    }

    public void setBlind_fire(String blind_fire) {
        this.blind_fire = blind_fire;
    }
}
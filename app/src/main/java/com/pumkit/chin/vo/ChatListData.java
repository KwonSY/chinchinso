package com.pumkit.chin.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.firebase.client.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class ChatListData {
    private String id_message;

    private String my_id;
    private String match_id;
    private String yourId;

    private String yourName;
    private String text;
    public Long timestamp;

    private String pic1;

    public ChatListData() {

    }

    public ChatListData(String my_id, String yourId, String yourName, String text, Map<String, String> time, String pic1) {
        this.my_id = my_id;
        this.yourId = yourId;
        this.yourName = yourName;
        this.text = text;
//        this.time = time;
        HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.pic1 = pic1;
    }

    public String getId_message() {
        return id_message;
    }

    public void setId_message(String id_message) {
        this.id_message = id_message;
    }

    public String getMy_id() {
        return my_id;
    }

    public void setMy_id(String my_id) {
        this.my_id = my_id;
    }

    public String getMatch_id() {
        return match_id;
    }

    public void setMatch_id(String match_id) {
        this.match_id = match_id;
    }

    public String getYourId() {
        return yourId;
    }

    public void setYourId(String yourId) {
        this.yourId = yourId;
    }

    public String getYourName() {
        return yourName;
    }

    public void setYourName(String yourName) {
        this.yourName = yourName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, String> getTimestamp() {
        return ServerValue.TIMESTAMP;
    }

    @JsonIgnore
    public Long getTimestampLong() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPic1() {
        return pic1;
    }

    public void setPic1(String pic1) {
        this.pic1 = pic1;
    }
}
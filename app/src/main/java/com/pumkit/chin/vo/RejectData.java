package com.pumkit.chin.vo;

public class RejectData {

    private String dial, name;

    public RejectData(String dial, String name) {
        this.dial = dial;
        this.name = name;
    }

    public String getDial() {
        return dial;
    }

    public void setDial(String dial) {
        this.dial = dial;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
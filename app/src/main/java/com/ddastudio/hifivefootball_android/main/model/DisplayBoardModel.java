package com.ddastudio.hifivefootball_android.main.model;

import com.google.gson.annotations.SerializedName;

public class DisplayBoardModel {

    @SerializedName("cd") String code;
    @SerializedName("id") int id;
    @SerializedName("info") String info;
    @SerializedName("action") String action;

    public String getCode() {
        return code;
    }

    public int getId() {
        return id;
    }

    public String getInfo() {
        return info;
    }

    public String getAction() {
        return action;
    }
}

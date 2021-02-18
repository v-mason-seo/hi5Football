package com.ddastudio.hifivefootball_android.match_summery.model;

import android.view.Gravity;

import com.google.gson.annotations.SerializedName;

public class SummeryBaseWrapperModel {

    private int mGravity;
    @SerializedName("title") String title;

    public SummeryBaseWrapperModel() {
        this.mGravity = Gravity.START;
    }

    public String getTitle() {
        return title;
    }

    public int getGravity(){
        return mGravity;
    }

    public void setGravity(int gravity) {
        //this.mGravity = Gravity.CENTER_VERTICAL;
        this.mGravity = gravity;
    }
}

package com.ddastudio.hifivefootball_android.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hongmac on 2017. 12. 4..
 */

public class NotificationTargetModel {


    @SerializedName("content_id")
    private int content_id;

    @SerializedName("comment_id")
    private int comment_id;

    @SerializedName("ment")
    private String ment;

    public int getContent_id() {
        return content_id;
    }

    public int getComment_id() {
        return comment_id;
    }

    public String getMent() {
        return ment;
    }
}

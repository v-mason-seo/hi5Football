package com.ddastudio.hifivefootball_android.data.model.footballdata;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hongmac on 2017. 10. 24..
 */

public class ApiCommentModel {

    @SerializedName("id") int id;
    @SerializedName("isgoal") String isGoal;
    @SerializedName("minute") String minute;
    @SerializedName("comment") String comment;
    @SerializedName("important") String important;

    public int getId() {
        return id;
    }

    public String getIsGoal() {
        return isGoal;
    }

    public String getMinute() {
        return minute;
    }

    public String getComment() {
        return comment;
    }

    public String getImportant() {
        return important;
    }
}

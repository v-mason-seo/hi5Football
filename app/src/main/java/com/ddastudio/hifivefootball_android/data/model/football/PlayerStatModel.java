package com.ddastudio.hifivefootball_android.data.model.football;

import com.google.gson.annotations.SerializedName;

public class PlayerStatModel {

    @SerializedName("goals") String goals;
    @SerializedName("ycard") String yellowCards;
    @SerializedName("rcard") String redCards;

    public String getGoals() {
        return goals;
    }

    public String getYellowCards() {
        return yellowCards;
    }

    public String getRedCards() {
        return redCards;
    }
}

package com.ddastudio.hifivefootball_android.data.model.football;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hongmac on 2018. 2. 25..
 */

public class MatchOverviewModel {


    @SerializedName("match") MatchModel match;
    @SerializedName("players") List<PlayerModel> players;

    public List<PlayerModel> getPlayers() {
        return players;
    }

    public MatchModel getMatch() {
        return match;
    }
}

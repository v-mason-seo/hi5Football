package com.ddastudio.hifivefootball_android.data.model.arena;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hongmac on 2017. 11. 14..
 */

public class PlayerRatingRecordModel {

    @SerializedName("match_id") int matchId;
    @SerializedName("match_fa_id") int matchFaId;
    @SerializedName("player_id") int playerId;
    @SerializedName("player_fa_id") int playerFaId;
    @SerializedName("time_seq") int timeSeq;
    @SerializedName("rating_average") double ratingAverage;
    @SerializedName("rating_up") int ratingUp;
    @SerializedName("rating_down") int ratingDown;

    public int getMatchId() {
        return matchId;
    }

    public int getMatchFaId() {
        return matchFaId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getPlayerFaId() {
        return playerFaId;
    }

    public int getTimeSeq() {
        return timeSeq;
    }

    public double getRatingAverage() {
        return ratingAverage;
    }

    public int getRatingUp() {
        return ratingUp;
    }

    public int getRatingDown() {
        return ratingDown;
    }
}

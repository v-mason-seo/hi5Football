package com.ddastudio.hifivefootball_android.data.model.arena;

import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hongmac on 2017. 11. 14..
 */

public class PlayerRatingParticipationUser {

    @SerializedName("match_id") int matchId;
    @SerializedName("match_fa_id") int matchFaId;
    @SerializedName("player_id") int playerId;
    @SerializedName("player_fa_id") int playerFaId;
    @SerializedName("time_seq") int timeSeq;
    @SerializedName("user") UserModel user;

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

    public UserModel getUser() {
        return user;
    }
}

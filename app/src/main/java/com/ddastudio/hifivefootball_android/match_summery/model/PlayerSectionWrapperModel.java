package com.ddastudio.hifivefootball_android.match_summery.model;

import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hongmac on 2017. 12. 23..
 */

public class PlayerSectionWrapperModel extends SummeryBaseWrapperModel {

    @SerializedName("players") List<PlayerModel> players;

    public List<PlayerModel> getPlayers() {
        return players;
    }
}

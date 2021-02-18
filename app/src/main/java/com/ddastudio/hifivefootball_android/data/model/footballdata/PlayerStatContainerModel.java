package com.ddastudio.hifivefootball_android.data.model.footballdata;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by hongmac on 2017. 10. 29..
 */

@Parcel
public class PlayerStatContainerModel {

    @SerializedName("club") List<PlayerStatisticsModel> playerClubStats;
    @SerializedName("national") List<PlayerStatisticsModel> playerNatitionalStats;
    @SerializedName("club_intl") List<PlayerStatisticsModel> playerClubIntlStats;

    public List<PlayerStatisticsModel> getPlayerClubStats() {
        return playerClubStats;
    }

    public List<PlayerStatisticsModel> getPlayerNatitionalStats() {
        return playerNatitionalStats;
    }

    public List<PlayerStatisticsModel> getPlayerClubIntlStats() {
        return playerClubIntlStats;
    }
}

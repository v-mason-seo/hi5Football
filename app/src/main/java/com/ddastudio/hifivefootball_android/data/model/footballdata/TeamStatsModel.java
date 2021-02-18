package com.ddastudio.hifivefootball_android.data.model.footballdata;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hongmac on 2017. 10. 24..
 */

public class TeamStatsModel {

    @SerializedName("fouls") String fouls;
    @SerializedName("saves") String saves;
    @SerializedName("corners") String corners;
    @SerializedName("offsides") String offsides;
    @SerializedName("redcards") String redCards;
    @SerializedName("table_id") int tableId;
    @SerializedName("shots_total") String shotsTotal;
    @SerializedName("yellowcards") String yellowCards;
    @SerializedName("shots_ongoal") String shotsOnGoal;
    @SerializedName("possesiontime") String possesionTime;

    public String getFouls() {
        return fouls;
    }

    public String getSaves() {
        return saves;
    }

    public String getCorners() {
        return corners;
    }

    public String getOffsides() {
        return offsides;
    }

    public String getRedCards() {
        return redCards;
    }

    public int getTableId() {
        return tableId;
    }

    public String getShotsTotal() {
        return shotsTotal;
    }

    public String getYellowCards() {
        return yellowCards;
    }

    public String getShotsOnGoal() {
        return shotsOnGoal;
    }

    public String getPossesionTime() {
        return possesionTime;
    }
}

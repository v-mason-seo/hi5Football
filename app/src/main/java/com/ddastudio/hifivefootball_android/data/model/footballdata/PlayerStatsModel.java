package com.ddastudio.hifivefootball_android.data.model.footballdata;

import android.text.TextUtils;

import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by hongmac on 2017. 10. 26..
 */

@Parcel
public class PlayerStatsModel extends MultipleItem {

    @SerializedName("id") int playerId;
    @SerializedName("num") int num;
    @SerializedName("pos") String pos;
    @SerializedName("playerName") String name;
    @SerializedName("posx") String posx;
    @SerializedName("posy") String posy;
    @SerializedName("g") String goals;
    @SerializedName("saves") String saves;
    @SerializedName("as") String assists;
    @SerializedName("offsides") String offsides;
    @SerializedName("pen_miss") String pen_miss;
    @SerializedName("rc") String redcards;
    @SerializedName("pen_score") String pen_score;
    @SerializedName("fouls_drawn") String fouls_drawn;
    @SerializedName("shots_total") String shots_total;
    @SerializedName("yc") String yellowcards;
    @SerializedName("shots_on_goal") String shots_on_goal;
    @SerializedName("fouls_committed") String fouls_committed;

    @Override
    public int getItemType() {
        return MultipleItem.BOTTOM_STANDING;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getNum() {
        return num;
    }

    public String getPos() {
        return pos;
    }

    public String getName() {
        return TextUtils.isEmpty(name) ? "" : name;
    }

    public String getPosx() {
        return posx;
    }

    public String getPosy() {
        return posy;
    }

    public String getGoals() {
        return TextUtils.isEmpty(goals) ? "" : goals;
    }

    public boolean hasGoals() {

        return TextUtils.isEmpty(goals) || goals.equals("0") ? false : true;
    }

    public String getSaves() {
        return saves;
    }

    public String getAssists() {
        return assists;
    }

    public String getOffsides() {
        return offsides;
    }

    public String getPen_miss() {
        return pen_miss;
    }

    public String getRedcards() {
        return redcards;
    }

    public boolean hasRedCards() {

        return TextUtils.isEmpty(redcards) || redcards.equals("0") ? false : true;
    }

    public String getPen_score() {
        return pen_score;
    }

    public String getFouls_drawn() {
        return fouls_drawn;
    }

    public String getShots_total() {
        return shots_total;
    }

    public String getYellowcards() {
        return yellowcards;
    }

    public boolean hasYellowCards() {

        return TextUtils.isEmpty(yellowcards) || yellowcards.equals("0") ? false : true;
    }

    public String getShots_on_goal() {
        return shots_on_goal;
    }

    public String getFouls_committed() {
        return fouls_committed;
    }
}

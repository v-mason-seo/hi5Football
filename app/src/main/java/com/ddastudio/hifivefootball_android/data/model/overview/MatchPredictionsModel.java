package com.ddastudio.hifivefootball_android.data.model.overview;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hongmac on 2017. 12. 13..
 */

public class MatchPredictionsModel implements MultiItemEntity {

    public int itemType;
    @SerializedName("mid") int matchId;
    @SerializedName("mfaid") int matchFaId;
    @SerializedName("hometid") int homeTeamId;
    @SerializedName("hometname") String homeTeamName;
    @SerializedName("awaytid") int awayTeamId;
    @SerializedName("awaytname") String awayTeamName;
    @SerializedName("status") String status;
    @SerializedName("user_choice") String userChoice;
    @SerializedName("usercnt") int userCount;
    @SerializedName("homecnt") int homeCount;
    @SerializedName("drawcnt") int drawCount;
    @SerializedName("awaycnt") int awayCount;

    public MatchPredictionsModel() {

        itemType = ViewType.MATCH_OVERVIEW_PREDICTIONS;
    }


    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public int getMatchId() {
        return matchId;
    }

    public int getMatchFaId() {
        return matchFaId;
    }

    public int getHomeTeamId() {
        return homeTeamId;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public int getAwayTeamId() {
        return awayTeamId;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public String getStatus() {
        return status == null ? "" : status;
    }

    public String getUserChoice() {
        return userChoice == null ? "" : userChoice;
    }

    public int getUserCount() {
        return userCount;
    }

    public void addUserCount() {

        userCount += 1;

    }

    public void setUserChoice(String userChoice) {
        this.userChoice = userChoice;
    }



    public String getUserCountString() {
        return String.valueOf(userCount);
    }

    public int getHomeCount() {
        return homeCount;
    }

    public void addHomeCount() {

        homeCount += 1;
    }

    public int getDrawCount() {
        return drawCount;
    }

    public void addDrawCount() {
        drawCount += 1;
    }

    public int getAwayCount() {
        return awayCount;
    }

    public void addAwayCount() {

        awayCount += 1;
    }

    public float getHomeRate() {

        if ( userCount != 0 && homeCount != 0) {

            return homeCount * 100.0f / userCount;
        }

        return 0.0f;
    }

    public float getDrawRate() {

        if ( userCount != 0 && drawCount != 0) {

            return drawCount * 100.0f / userCount;
        }

        return 0.0f;
    }

    public float getAwayRate() {

        if ( userCount != 0 && awayCount != 0) {

            return awayCount * 100.0f / userCount;
        }

        return 0.0f;
    }


    public String getHomeRateString() {

        if ( userCount != 0 && homeCount != 0) {

            double value = ( (double)homeCount / (double)userCount ) * 100;
            return String.valueOf(value) + "%";
        }

        return "0%";
    }

    public String getDrawRateString() {

        if ( userCount != 0 && drawCount != 0) {

            //float value = ( drawCount / userCount ) * 100;

            return String.valueOf(drawCount * 100.0f / userCount) + "%";
        }

        return "0%";
    }

    public String getAwayRateString() {

        if ( userCount != 0 && awayCount != 0) {

            float value = ( awayCount / userCount ) * 100;
            //return String.valueOf(value);
            return String.valueOf(awayCount * 100.0f / userCount) + "%";
        }

        return "0%";
    }
}

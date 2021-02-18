package com.ddastudio.hifivefootball_android.data.model.footballdata;

import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hongmac on 2017. 10. 24..
 */

public class StandingModel implements MultiItemEntity {

    String TeamImageBaseUrl = "http://cdn.hifivefootball.com/team/18/";
    String TeamLargeImageBaseUrl = "http://cdn.hifivefootball.com/team/180/";

    int itemType;
    @SerializedName("comp_fa_id") int compFaId;
    @SerializedName("season") String season;
    @SerializedName("round") int round;
    @SerializedName("stage_id") int stageId;
    @SerializedName("comp_group") String compGroup;
    @SerializedName("country") String country;
    @SerializedName("team") TeamModel team;
    @SerializedName("status") String status;
    @SerializedName("recent_form") String recent_form;
    @SerializedName("position") int position;
    @SerializedName("overall_gp") int overallGp;
    @SerializedName("overall_w") int overallW;
    @SerializedName("overall_d") int overallD;
    @SerializedName("overall_l") int overallL;
    @SerializedName("overall_gs") int overallGs;
    @SerializedName("overall_ga") int overallGa;
    @SerializedName("home_gp") int homeGp;
    @SerializedName("home_w") int homeW;
    @SerializedName("home_d") int homeD;
    @SerializedName("home_l") int homeL;
    @SerializedName("home_gs") int homeGs;
    @SerializedName("home_ga") int homeGa;
    @SerializedName("away_gp") int awayGp;
    @SerializedName("away_w") int awayW;
    @SerializedName("away_d") int awayD;
    @SerializedName("away_l") int awayL;
    @SerializedName("away_gs") int awayGs;
    @SerializedName("away_ga") int awayGa;
    @SerializedName("gd") int gd;
    @SerializedName("points") int points;
    @SerializedName("description") String description;
    @SerializedName("updated") String updated;
    boolean selected;

    public StandingModel() {
        itemType = MultipleItem.BOTTOM_STANDING;
        selected = false;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getCompFaId() {
        return compFaId;
    }

    public String getSeason() {
        return season;
    }

    public int getRound() {
        return round;
    }

    public int getStageId() {
        return stageId;
    }

    public String getCompGroup() {
        return TextUtils.isEmpty(compGroup) ? "" : compGroup;
    }

    public String getCountry() {
        return country;
    }

//    public int getTeamImageId() {
//        return teamImageId;
//    }
//
//    public String getTeamImageUrl() {
//        return TeamLargeImageBaseUrl + teamImageId + ".png";
//    }
//
//    public int getTeamId() {
//        return teamId;
//    }
//
//    public int getTeamFaId() {
//        return teamFaId;
//    }
//
//    public String getTeamName() {
//        return teamName;
//    }

    public int getTeamId() {

        if ( team != null ) {
            return team.getTeamId();
        }

        return -1;
    }

    public String getTeamName() {

        if ( team != null ) {
            return team.getTeamName();
        }

        return "";
    }

    public String getTeamEmblemUrl() {

        if ( team != null ) {
            return team.getEmblemUrl();
        }

        return "";
    }

    public String getStatus() {
        return status;
    }

    public String getRecent_form() {
        return recent_form;
    }

    public int getPosition() {
        return position;
    }

    public int getOverallGp() {
        return overallGp;
    }

    public int getOverallW() {
        return overallW;
    }

    public int getOverallD() {
        return overallD;
    }

    public int getOverallL() {
        return overallL;
    }

    public int getOverallGs() {
        return overallGs;
    }

    public int getOverallGa() {
        return overallGa;
    }

    public int getHomeGp() {
        return homeGp;
    }

    public int getHomeW() {
        return homeW;
    }

    public int getHomeD() {
        return homeD;
    }

    public int getHomeL() {
        return homeL;
    }

    public int getHomeGs() {
        return homeGs;
    }

    public int getHomeGa() {
        return homeGa;
    }

    public int getAwayGp() {
        return awayGp;
    }

    public int getAwayW() {
        return awayW;
    }

    public int getAwayD() {
        return awayD;
    }

    public int getAwayL() {
        return awayL;
    }

    public int getAwayGs() {
        return awayGs;
    }

    public int getAwayGa() {
        return awayGa;
    }

    public int getGd() {
        return gd;
    }

    public int getPoints() {
        return points;
    }

    public String getDescription() {
        return description;
    }

    public String getUpdated() {
        return updated;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

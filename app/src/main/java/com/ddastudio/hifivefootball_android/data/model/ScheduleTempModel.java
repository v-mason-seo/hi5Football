package com.ddastudio.hifivefootball_android.data.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by hongmac on 2017. 10. 13..
 */

@Parcel
public class ScheduleTempModel extends MultipleItem {


    String ImageBaseUrl = "http://cdn.hifivefootball.com/team/180/";
    String LargeImageBaseUrl = "http://cdn.hifivefootball.com/team/180/";

    @SerializedName("id") int id;
    @SerializedName("competition_id") int competitionId;
    @SerializedName("competition_img_id") int competitionImageId;
    @SerializedName("season") String season;
    @SerializedName("league_name") String leagueName;
    @SerializedName("match_date") String matchDate;
    @SerializedName("status") String status;
    @SerializedName("matchday") int matchday;
    @SerializedName("hometeam_id") int homeTeamId;
    @SerializedName("hometeam_img_id") int homeTeamImageId;
    @SerializedName("hometeam_name") String homeTeamName;
    @SerializedName("hometeam_goals") int homeTeamGoals;
    @SerializedName("awayteam_id") int awayTeamId;
    @SerializedName("awayteam_img_id") int awayTeamImageId;
    @SerializedName("awayteam_name") String awayTeamName;
    @SerializedName("awayteam_goals") int awayTeamGoals;

    @Override
    public int getItemType() {
        return MultipleItem.ARENA_SCHEDULE;
    }

    public int getId() {
        return id;
    }

    public int getCompetitionId() {
        return competitionId;
    }

    public int getCompetitionImageId() {
        return competitionImageId;
    }

    public String getSeason() {
        return season;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public String getStatus() {
        return status;
    }

    public int getMatchday() {
        return matchday;
    }

    public int getHomeTeamId() {
        return homeTeamId;
    }

    public int getHomeTeamImageId() {
        return homeTeamImageId;
    }

    public String getHometeamImageUrl() {

        return ImageBaseUrl + homeTeamImageId + ".png";
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public int getHomeTeamGoals() {
        return homeTeamGoals;
    }

    public int getAwayTeamId() {
        return awayTeamId;
    }

    public int getAwayTeamImageId() {
        return awayTeamImageId;
    }

    public String getAwayTeamImageUrl() {

        return ImageBaseUrl + awayTeamImageId + ".png";
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public int getAwayTeamGoals() {
        return awayTeamGoals;
    }


//    @Override
//    public boolean equals(Object obj) {
//
//
//        return competitionId == ((ScheduleTempModel)obj).competitionId;
//    }
//
//    @Override
//    public int hashCode() {
//        return leagueName.hashCode();
//    }
}

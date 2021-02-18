package com.ddastudio.hifivefootball_android.data.model.footballdata;

import android.support.annotation.Nullable;
import android.util.Log;

import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by hongmac on 2017. 10. 29..
 */

@Parcel
public class PlayerStatisticsModel extends MultipleItem{

    String CompetitionImageBaseUrl = "http://cdn.hifivefootball.com/competitions/180/";

    @SerializedName("id") int id;
    @SerializedName("playerName") String name;
    @SerializedName("goals") String goals;
    @SerializedName("league") String league;
    @SerializedName("season") String season;
    @SerializedName("lineups") String lineups;
    @SerializedName("minutes") String minutes;
    @SerializedName("redcards") String redcards;
    @SerializedName("league_id") int leagueId;
    @SerializedName("yellowred") String yellowred;
    @SerializedName("appearances") String appearances;
    @SerializedName("yellowcards") String yellowcards;
    @SerializedName("substitute_in") String substitute_in;
    @SerializedName("substitute_out") String substitute_out;
    @SerializedName("substitute_on_bench") String substituteOnBench;

    @Override
    public int getItemType() {
        return MultipleItem.PLAYER_STATS;
    }

    public String getTitle() {

        return league + " " + season + " " + name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGoals() {
        return goals;
    }

    public String getLeague() {
        return league;
    }

    public String getSeason() {
        return season;
    }

    public String getLineups() {
        return lineups;
    }

    public String getMinutes() {
        return minutes;
    }

    public String getRedcards() {
        return redcards;
    }

    public int getLeagueId() {
        return leagueId;
    }

    public String getCompetitionImageUrl() {
        String url =  CompetitionImageBaseUrl + leagueId + ".png";
        return url;
    }

    public String getYellowred() {
        return yellowred;
    }

    public String getAppearances() {
        return appearances;
    }

    public String getYellowcards() {
        return yellowcards;
    }

    public String getSubstitute_in() {
        return substitute_in;
    }

    public String getSubstitute_out() {
        return substitute_out;
    }

    public String getSubstituteOnBench() {
        return substituteOnBench;
    }

}

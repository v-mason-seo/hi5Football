package com.ddastudio.hifivefootball_android.data.model.footballdata;

import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hongmac on 2017. 10. 24..
 */

public class MatchDetailModel extends MatchModel {

    @SerializedName("venue") String venue;
    @SerializedName("venue_id") int venue_id;
    @SerializedName("venue_city") String venue_city;
    @SerializedName("status") String status;
    @SerializedName("timer") String timer;
    @SerializedName("ht_score") String ht_score;
    @SerializedName("ft_score") String ft_score;
    @SerializedName("et_score") String et_score;
    @SerializedName("penalty_local") int penalty_local;
    @SerializedName("penalty_visitor") int penalty_visitor;
    @SerializedName("localteam_stats") List<TeamStatsModel> localTeamStatsList;
    @SerializedName("visitorteam_stats") List<TeamStatsModel> visitorTeamStatsList;
    @SerializedName("api_comments") List<ApiCommentModel> apiCommentList;


    public String getVenue() {
        return venue;
    }

    public int getVenue_id() {
        return venue_id;
    }

    public String getVenue_city() {
        return venue_city;
    }

    public String getStatus() {
        return status;
    }

    public String getTimer() {
        return timer;
    }

    public String getHt_score() {
        return ht_score;
    }

    public String getFt_score() {
        return ft_score;
    }

    public String getEt_score() {
        return et_score;
    }

    public int getPenalty_local() {
        return penalty_local;
    }

    public int getPenalty_visitor() {
        return penalty_visitor;
    }

    public List<TeamStatsModel> getLocalTeamStatsList() {
        return localTeamStatsList;
    }

    public List<TeamStatsModel> getVisitorTeamStatsList() {
        return visitorTeamStatsList;
    }

    public List<ApiCommentModel> getApiCommentList() {
        return apiCommentList;
    }
}

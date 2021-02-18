package com.ddastudio.hifivefootball_android.match_summery.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hongmac on 2017. 12. 27..
 */

public class TeamSectionModel implements MultiItemEntity {

    final String CompetitionImageBaseUrl = "http://cdn.hifivefootball.com/competitions/18/";
    final String CompetitionLargeImageBaseUrl = "http://cdn.hifivefootball.com/competitions/180/";
    final String TeamSmallImageBaseUrl = "http://cdn.hifivefootball.com/team/18/";
    final String TeamImageBaseUrl = "http://cdn.hifivefootball.com/team/45/";
    final String TeamLargeImageBaseUrl = "http://cdn.hifivefootball.com/team/180/";

    public int itemType;

    @SerializedName("id") int teamId;
    @SerializedName("name") String teamName;
    @SerializedName("emblem_id") int teamFmId;

    public TeamSectionModel() {
        itemType = ViewType.MATCH_TEAM_SECTION;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public int getTeamFmId() {
        return teamFmId;
    }

    public String getEmblemUrl() {
        return TeamImageBaseUrl + teamFmId + ".png";
    }

    public String getLargeEmblemUrl() {
        return TeamLargeImageBaseUrl + teamFmId + ".png";
    }

    public String getSmallEmblemUrl() {
        return TeamSmallImageBaseUrl + teamFmId + ".png";
    }
}

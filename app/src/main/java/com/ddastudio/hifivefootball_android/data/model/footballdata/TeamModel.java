package com.ddastudio.hifivefootball_android.data.model.footballdata;

import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

import timber.log.Timber;

/**
 * Created by hongmac on 2017. 10. 30..
 */

@Parcel
public class TeamModel extends MultipleItem {

    //-----------------------------------
    // 이미지 베이스 URL
    //-----------------------------------
    String TeamImageSmallBaseUrl = "http://cdn.hifivefootball.com/team/18/";
    String TeamImageBaseUrl = "http://cdn.hifivefootball.com/team/45/";
    String TeamLargeImageBaseUrl = "http://cdn.hifivefootball.com/team/180/";

    @SerializedName("id") int teamId;
    @SerializedName("fa_id") int teamFaId;
    @SerializedName("emblem_id") int emblemId;
    @SerializedName("name") String teamName;
    @SerializedName("country") String country;
    @SerializedName("founded") String founded;
    @SerializedName("leagues") String leagues;
    @SerializedName("venue_name") String venue_name;
    @SerializedName("venue_surface") String venue_surface;
    @SerializedName("venue_city") String venue_city;
    @SerializedName("coach_name") String coach_name;
    @SerializedName("coach_id") int coach_id;
    @SerializedName("hits") int hits;
    @SerializedName("statistics") List<TeamStatisticsModel> statistics;
    @SerializedName("tag") List<String> tagList;
    @SerializedName("updated") String updated;
    boolean isSelected = false;

    public TeamModel() {
        itemType = TEAM_BASIC_INFO;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getEmblemUrl() {
        return TeamImageBaseUrl + emblemId + ".png";
    }

    public String getLargeEmblemUrl() {
        return TeamLargeImageBaseUrl + emblemId + ".png";
    }

    public String getSmallEmblemUrl() {
        return TeamImageSmallBaseUrl + emblemId + ".png";
    }

    public int getTeamId() {
        return teamId;
    }

    public int getTeamFaId() {
        return teamFaId;
    }

    public int getEmblemId() {
        return emblemId;
    }

    public String getTeamName() {
        return teamName == null ? "" : teamName;
    }

    public String getCountry() {
        return country;
    }

    public String getFounded() {
        return founded;
    }

    public String getLeagues() {
        return leagues;
    }

    public String getVenue_name() {
        return venue_name;
    }

    public String getVenue_surface() {
        return venue_surface;
    }

    public String getVenue_city() {
        return venue_city;
    }

    public String getCoach_name() {
        return coach_name;
    }

    public int getCoach_id() {
        return coach_id;
    }

    public int getHits() {
        return hits;
    }

    public String getHitString() {
        return "HIT : " + String.valueOf(hits);
    }

    public List<TeamStatisticsModel> getStatistics() {
        return statistics;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public String getUpdated() {
        return updated;
    }

    @Override
    public String toString() {
        return teamName;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        TeamModel t = (TeamModel) obj;

        return this.getTeamId() == t.getTeamId()
                && this.getTeamName().equals(t.getTeamName())
                && this.getEmblemUrl().equals(t.getEmblemUrl())
                && this.getHits() == t.getHits()
                && this.getUpdated().equals(t.getUpdated());

    }
}

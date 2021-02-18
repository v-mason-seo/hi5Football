package com.ddastudio.hifivefootball_android.data.model.football;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;
import com.ddastudio.hifivefootball_android.utils.DateUtils;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.Date;
import java.util.regex.Pattern;


/**
 * Created by hongmac on 2017. 10. 24..
 */

@Parcel(analyze = {MatchModel.class, TeamModel.class, CompModel.class})
public class MatchModel implements MultiItemEntity, Comparable<MatchModel>{

    String CompetitionImageBaseUrl = "http://cdn.hifivefootball.com/competitions/18/";
    String CompetitionLargeImageBaseUrl = "http://cdn.hifivefootball.com/competitions/180/";
    String TeamImageBaseUrl = "http://cdn.hifivefootball.com/team/18/";
    String TeamLargeImageBaseUrl = "http://cdn.hifivefootball.com/team/180/";

    int itemType;

    @SerializedName("comp") CompModel competition;
    @SerializedName("match_id") int matchId;
    @SerializedName("match_fa_id") int matchFaId;
    @SerializedName("match_date") Date matchDate;
    @SerializedName("season") String season;
    @SerializedName("week") String week;
    @SerializedName("timer") String timer;
    @SerializedName("status") String status;
    @SerializedName("venue") String venue;
    @SerializedName("home_team") TeamModel homeTeam;
    @SerializedName("home_score") String homeTeamScore;
    @SerializedName("away_team") TeamModel awayTeam;
    @SerializedName("away_score") String awayTeamScore;
    @SerializedName("user_cnt") int userCount;
    @SerializedName("content_cnt") int contentCount;
    @SerializedName("rating_cnt") int ratingCount;
    @SerializedName("pre_match_date") String preMatchDate;
    @SerializedName("next_match_date") String nextMatchDate;

    /*------------------------------------------------------------------------------*/

    public MatchModel() {
        itemType = MultipleItem.ARENA_SCHEDULE;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    /*------------------------------------------------------------------------------*/

    public CompModel getCompetition() {
        return competition;
    }

    public int getCompetitionId() {

        if ( competition != null )
            return competition.getCompetitionId();

        return 0;
    }

    public int getCompetitionImageId() {
        if ( competition != null ) {
            return competition.getCompetitionImageId();
        }

        return 0;
    }

    public String getCompetitionName() {
        if ( competition != null ) {
            return competition.getCompetitionName();
        }

        return "";
    }

    public String getCompetitionSmallImage() {
        if ( competition != null ) {
            return competition.getCompetitionSmallImageUrl();
        }

        return "";
    }

    public String getCompetitionImage() {
        if ( competition != null ) {
            return competition.getCompetitionImageUrl();
        }

        return "";
    }

    public String getCompRegion() {
        if ( competition != null ) {
            return competition.getCompetitionRegion();
        }

        return "";
    }

    public int getMatchId() {
        return matchId;
    }

    public int getMatchFaId() {
        return matchFaId;
    }

    public Date getMatchRawDate() {
        return matchDate;
    }

    public String getMatchDate() {

        return DateUtils.convertDateToString(matchDate, "yyyy-MM-dd");
    }

    public String getMatchDate2() {

        return DateUtils.convertDateToString(matchDate, "MM/dd(E)");
    }

    public String getMatchDate3() {

        return DateUtils.convertDateToString(matchDate, "MM.dd");
    }

    public String getMatchDate4() {

        return DateUtils.convertDateToString(matchDate, "yy/MM/dd(E)");
    }

    public String getMatchDateTime() {

        try {
            return DateUtils.convertDateToString(matchDate, "yy/MM/dd (E)  HH:mm");
        } catch (Exception e) {
            return "";
        }

    }

    public String getMatchDateString() {

        if ( matchDate == null ) {
            return "";
        }

        return DateUtils.getPrettTime(matchDate);
    }

    public String getTime() {
        return DateUtils.convertDateToString(matchDate, "HH:mm");
    }

    public String getSeason() {
        return season == null ? "" : season;
    }


    public String getWeek() {

        if ( TextUtils.isEmpty(week) || week.equals("0") ) {
            return "";
        }
        return week + "주차";
    }

    public String getTimer() {
        return timer;
    }

    private boolean matched(String regex, String inputTxt) {
        inputTxt = TextUtils.isEmpty(status) ? "" : inputTxt;
        return Pattern.matches(regex, inputTxt);
    }

    public String getStatus() {

        // HH:mm 형식이면 match_date 에서 시간을 뽑아서 보여준다.
        boolean isTime = matched("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$", status);

        if ( isTime ) {
            return DateUtils.convertDateToString(matchDate, "HH:mm");
        } else if ( TextUtils.isEmpty(status)) {
            return DateUtils.convertDateToString(matchDate, "HH:mm");
        } else {
            return status;
        }

        //return isTime ? DateUtils.convertDateToString(matchDate, "HH:mm") : status;
    }

    public int getHomeTeamId() {

        if ( homeTeam != null ) {
            return homeTeam.getTeamId();
        }

        return -1;
    }

    public String getHomeTeamName() {

        if ( homeTeam != null ) {
            return homeTeam.getTeamName();
        }

        return "";
    }

    public String getHomeTeamEmblemUrl() {

        if ( homeTeam != null ) {
            return homeTeam.getEmblemUrl();
        }

        return "";
    }

    public String getHomeTeamLargeEmblemUrl() {

        if ( homeTeam != null ) {
            return homeTeam.getLargeEmblemUrl();
        }

        return "";
    }

    public String getHomeTeamSmallEmblemUrl() {

        if ( homeTeam != null ) {
            return homeTeam.getSmallEmblemUrl();
        }

        return "";
    }

    public int getAwayTeamId() {

        if ( awayTeam != null ) {
            return awayTeam.getTeamId();
        }

        return -1;
    }

    public String getAwayTeamName() {

        if ( awayTeam != null ) {
            return awayTeam.getTeamName();
        }

        return "";
    }

    public String getAwayTeamEmblemUrl() {

        if ( awayTeam != null ) {
            return awayTeam.getEmblemUrl();
        }

        return "";
    }

    public String getAwayTeamLargeEmblemUrl() {

        if ( awayTeam != null ) {
            return awayTeam.getLargeEmblemUrl();
        }

        return "";
    }

    public String getAwayTeamSmallEmblemUrl() {

        if ( awayTeam != null ) {
            return awayTeam.getSmallEmblemUrl();
        }

        return "";
    }

    public String getHomeTeamScore() {
        return homeTeamScore;
    }

    public String getHomeTeamScoreString() {

        return homeTeamScore == null ? "-" : String.valueOf(homeTeamScore);
    }

    public String getAwayTeamScoreString() {

        return awayTeamScore == null ? "-" : String.valueOf(awayTeamScore);
    }

    public String getAwayTeamScore() {
        //return awayTeamScore;
        return awayTeamScore;
    }

    public String getVenue() {
        return venue == null ? "" : venue;
    }

    public int getUserCount() {
        return userCount;
    }

    public String getUserCountString() {
        return String.valueOf(userCount);
    }

    public int getContentCount() {
        return contentCount;
    }

    public String getContentCountString() {
        return "매치글 : " + contentCount;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public String getRatingCountString() {
        return "선수평가 : " + ratingCount;
    }

    public String getPreMatchDate() {
        return preMatchDate;
    }

    public String getNextMatchDate() {
        return nextMatchDate;
    }



    /*---------------------------------------------------------------------*/

    @Override
    public int compareTo(@NonNull MatchModel matchModel) {

        int ret0 = Integer.compare(getCompetitionId(), matchModel.getCompetitionId());

        if ( ret0 == 0 ) {
            int ret1 = matchDate.compareTo(matchModel.matchDate);

            if ( ret1 == 0 ) {
                int ret2 =  status.compareTo(matchModel.getStatus());

                return ret2;
            }

            return ret1;
        }

        return ret0;
    }

    @Override
    public String toString() {

        return String.format("Match id : %d\n" +
                "Match football api id : %d\n" +
                "Home team name : %s\n" +
                "Away team name : %s", matchId, matchFaId, homeTeam.getTeamName(), awayTeam.getTeamName());
    }
}

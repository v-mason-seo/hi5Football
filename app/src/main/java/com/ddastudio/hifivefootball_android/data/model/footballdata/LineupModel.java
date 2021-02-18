package com.ddastudio.hifivefootball_android.data.model.footballdata;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import timber.log.Timber;

/**
 * Created by hongmac on 2017. 10. 26..
 */

@Parcel
public class LineupModel extends MultipleItem implements Comparable<LineupModel> {

    String PlayerImageBaseUrl = "http://cdn.hifivefootball.com/player/48/";
    String PlayerLargeImageBaseUrl = "http://cdn.hifivefootball.com/player/96";

    //String PlayerLargeImageBaseUrl = "http://cdn.hifivefootball.com/player/";
    //String SoccerWikiBaseUrl = "http://cdn.soccerwiki.org/images/player/";

    @SerializedName("match_id") int matchId;
    @SerializedName("match_fa_id") int matchFaId;
    @SerializedName("team") TeamModel team;
    @SerializedName("id") int playerId;
    @SerializedName("player_fa_id") int playerFaId;
    @SerializedName("soccerwiki_id") int playerImageid;
    @SerializedName("no") String playerNumber;
    @SerializedName("name") String playerName;
    @SerializedName("pos") String pos;
    //@SerializedName("startingyn") String startingyn;
    @SerializedName("substitutions") String substitutions;
    @SerializedName("subs_minute") String subsMinute;
    @SerializedName("player_stats") PlayerStatsModel playerStats;
    @SerializedName("rating") float playerRating;
    @SerializedName("goals") String goals;
    @SerializedName("ycard") String yellowCards;
    @SerializedName("rcard") String redCards;
    @SerializedName("user_rating_count") int userRatingCount;
    @SerializedName("user_comment_count") int userCommentCount;
    @SerializedName("updated") String updated;
    int background;

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public int getPlayerId() {
        return playerId;
    }

    @Override
    public int getItemType() {
        return MultipleItem.MATCH_LINEUP;
    }

    public int getMatchId() {
        return matchId;
    }

    public int getMatchFaId() {
        return matchFaId;
    }

//    public int getTeamId() {
//        return teamId;
//    }

    public int getTeamId() {

        if ( team != null ) {
            return team.getTeamId();
        }

        return 0;
    }

    public String getTeamName() {

        if ( team != null ) {
            return team.getTeamName();
        }

        return "";
    }

    public String getEmblemUrl() {

        if ( team != null ) {
            return team.getEmblemUrl();
        }

        return "";
    }

    public String getSmallEmblemUrl() {

        if ( team != null ) {
            return team.getSmallEmblemUrl();
        }

        return "";
    }

    public int getPlayerFaId() {
        return playerFaId;
    }

    public String getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPos() {
        return pos == null ? "" : pos.toUpperCase();
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

//    public String getStartingyn() {
//        return startingyn == null ? "" : startingyn;
//    }

    public String getSubstitutions() {
        return TextUtils.isEmpty(substitutions) ? "" : substitutions;
    }

    public String getSubsMinute() {
        return TextUtils.isEmpty(subsMinute) ? "" : subsMinute;
    }

    public PlayerStatsModel getPlayerStats() {
        return playerStats;
    }

    public String getUpdated() {
        return updated;
    }

    public String getPlayerLargeImageUrl() {

        return PlayerLargeImageBaseUrl + playerId + ".png";
    }

    public String getPlayerImageUrl() {

        return PlayerImageBaseUrl + playerId + ".png";
    }

    public String getGoals() {
        return goals;
    }

    public boolean hasGoals() {

        return TextUtils.isEmpty(goals) || goals.equals("0") ? false : true;
    }

    public String getYellowCards() {
        return yellowCards;
    }

    public boolean hasYellowCards() {

        return TextUtils.isEmpty(yellowCards) || yellowCards.equals("0") ? false : true;
    }

    public String getRedCards() {
        return redCards;
    }

    public boolean hasRedCards() {

        return TextUtils.isEmpty(redCards) || redCards.equals("0") ? false : true;
    }

    public float getPlayerRating() {
        return playerRating;
    }

    public String getPlayerRatingString() {

        return String.format("%.1f", playerRating);
    }

    public int getUserRatingCount() {
        return userRatingCount;
    }

    public String getUserRatingCountString() {
        return String.valueOf(userRatingCount);
    }

    public int getUserCommentCount() {
        return userCommentCount;
    }

    public String getUserCommentCountString() {
        return String.valueOf(userCommentCount);
    }

    public Integer getOrder() {
        switch (getPos()) {
            // [Row - 1]-------------------------------
            case "LS":
                return 1;
            case "F":
            case "ST":
                return 2;
            case "RS":
                return 3;
            // [Row - 1]-------------------------------
            case "LF":
                return 5;
            case "CF-L":
            case "LCF":
                return 6;
            case "CF":
                return 7;
            case "CF-R":
            case "RCF":
                return 8;
            case "RF":
                return 9;
            // [Row - 2]-------------------------------
            case "AM-L":
                return 10;
            case "AM":
                return 12;
            case "AM-R":
                return 14;
            // [Row - 3]-------------------------------
            case "LM":
                return 15;
            case "LCM":
            case "CM-L":
                return 16;
            case "CM":
                return 17;
            case "RCM":
            case "CM-R":
                return 18;
            case "RM":
                return 19;
            // [Row - 4]-------------------------------
            case "LWB":
                return 20;
            case "DM-L":
            case "LDM":
                return 21;
            case "DM":
            case "SW":
                return 22;
            case "DM-R":
            case "RDM":
                return 23;
            case "RWB":
                return 24;
            // [Row - 5]-------------------------------
            case "LB":
                return 25;
            case "CD-L":
                return 26;
            case "CD":
                return 27;
            case "CD-R":
                return 28;
            case "RB":
                return 29;
            // [Row - X]-------------------------------
            case "G":
            case "GK":
                return 32;
            default:
                return 100;
        }
    }



    /*------------------------------------------------------------------*/

    @Override
    public int compareTo(@NonNull LineupModel o) {
        return getOrder().compareTo(o.getOrder());
    }
}

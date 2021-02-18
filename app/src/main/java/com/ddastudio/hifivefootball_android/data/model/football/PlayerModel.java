package com.ddastudio.hifivefootball_android.data.model.football;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.footballdata.LineupModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.PlayerStatContainerModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.PlayerStatsModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;
import com.ddastudio.hifivefootball_android.utils.DateUtils;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by hongmac on 2017. 10. 29..
 */

@Parcel
public class PlayerModel implements MultiItemEntity, Comparable<PlayerModel>{

    //-----------------------------------
    // 이미지 베이스 URL
    //-----------------------------------
    //String SoccerWikiBaseUrl = "http://cdn.soccerwiki.org/images/player/";
    String PlayerImageBaseUrl = "http://cdn.hifivefootball.com/player/48/";
    String PlayerLargeImageBaseUrl = "http://cdn.hifivefootball.com/player/96";

    int itemType;

    @SerializedName("id") int playerId;
    @SerializedName("player_fa_id") int playerFaId;
    @SerializedName("player_img_id") int playerImageid;
    @SerializedName("soccerwiki_id") int soccerwiki_id;
    @SerializedName("name") String playerName;
    @SerializedName("player_common_name") String playerCommonName;
    @SerializedName("player_firstname") String playerFirstName;
    @SerializedName("player_lastname") String playerLastName;
    @SerializedName("no") String backNumber;
    @SerializedName("pos") String pos;
    @SerializedName("position") String position;
    @SerializedName("substitutions") String substitutions;
    @SerializedName("subs_minute") String subsMinute;
    @SerializedName("hits") int hits;
    @SerializedName("stat") PlayerStatsModel stats;
    @SerializedName("rating") float rating;
    @SerializedName("rating_u") float userRating;
    @SerializedName("comment") String playerComment;
    @SerializedName("user_comment_count") int commentCount;
    @SerializedName("user_rating_count") int ratingCount;
    @SerializedName("team") TeamModel team;
    @SerializedName("national_team") TeamModel nationalTeam;
    @SerializedName("nationality") String nationality;
    @SerializedName("birthdate") Date birthdate;
    @SerializedName("birthcountry") String birthcountry;
    @SerializedName("birthplace") String birthplace;
    @SerializedName("height") String height;
    @SerializedName("weight") String weight;
    @SerializedName("tag") List<String> tagList;
    @SerializedName("updated") String updated;
    boolean isSelected = false;
    int background;

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 생성자
     */
    public PlayerModel() {
        itemType = ViewType.PLAYER;
    }

    @Override
    public int getItemType() {

        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    /*---------------------------------------------------------------------------------------------*/

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }



    public int getPlayerId() {
        return playerId;
    }

    public int getPlayerFaId() {
        return playerFaId;
    }

    public String getPlayerCommonName() {

        if (TextUtils.isEmpty(playerCommonName)) return "";
        return playerCommonName;
    }

    public float getRating() {
        return rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public String getRatingCountString() {
        return String.valueOf(ratingCount);
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void addRatingCount() {
        ratingCount += ratingCount+1;
    }

    public String getRatingString() {
        return String.valueOf(rating);
    }

    public float getUserRating() {
        return userRating;
    }

    public String getUserRatingString() {

        return String.valueOf(userRating);
    }

    public String getPlayerComment() {
        return TextUtils.isEmpty(playerComment) ? "" : playerComment;
    }

    public void setPlayerComment(String playerComment) {
        this.playerComment = playerComment;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPlayerFirstName() {
        return playerFirstName;
    }

    public String getPlayerLastName() {
        return playerLastName;
    }

    public String getSubstitutions() {
        return TextUtils.isEmpty(substitutions) ? "" : substitutions;
    }

    public boolean hasSubstitutions() {

        return !TextUtils.isEmpty(substitutions) && (getSubstitutions().equals("on") || getSubstitutions().equals("off"));
    }

    public String getSubsMinute() {
        return TextUtils.isEmpty(subsMinute) ? "" : subsMinute+"'";
    }

    public int getHits() {
        return hits;
    }

    public String getHitString() {

        return String.valueOf(hits);
    }

    public PlayerStatsModel getStats() {
        return stats;
    }

    public String getGoals() {
        return stats != null ? stats.getGoals() : "";
    }

    public boolean hasGoals() {

        if ( stats == null )
            return false;

        return TextUtils.isEmpty(stats.getGoals()) || stats.getGoals().equals("0") ? false : true;
    }

    public String getYellowCards() {
        return stats != null ? stats.getYellowcards() : "";
    }

    public boolean hasYellowCards() {

        if ( stats == null )
            return false;

        return TextUtils.isEmpty(stats.getYellowcards()) || stats.getYellowcards().equals("0") ? false : true;
    }

    public String getRedCards() {
        return stats != null ? stats.getRedcards() : "";
    }

    public boolean hasRedCards() {

        if ( stats == null )
            return false;

        return TextUtils.isEmpty(stats.getRedcards()) || stats.getRedcards().equals("0") ? false : true;
    }

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

    public String getBackNumber() {
        return backNumber;
    }

    public String getNationality() {
        if (TextUtils.isEmpty(nationality)) return "";
        return nationality;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public String getBirthDateString() {

        if ( birthdate != null ) {
            return DateUtils.convertDateToString(birthdate, "yyyy-MM-dd");
        }

        return "";
    }

    public String getBirthcountry() {
        return birthcountry;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public String getPos() {
        if (TextUtils.isEmpty(pos)) return "";
        return pos.toUpperCase();
    }

    public String getPosition() {
        return position;
    }

    public String getHeight() {
        if (TextUtils.isEmpty(height)) return "";
        return height;
    }

    public String getWeight() {
        if (TextUtils.isEmpty(weight)) return "";
        return weight;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public String getCommentCountString() {
        return String.valueOf(commentCount);
    }

    public void addCommentCount() {
        commentCount += commentCount + 1;
    }

//    public PlayerStatContainerModel getPlayerStatistics() {
//        return playerStatistics;
//    }

    public List<String> getTagList() {
        return tagList;
    }

    public String getUpdated() {
        return updated;
    }

    @Override
    public String toString() {
        return playerName;
    }

    public String getPlayerLargeImageUrl() {

        return PlayerImageBaseUrl + playerId + ".png";
    }

    /*------------------------------------------------------------------*/

    public void setBackground(int background) {
        this.background = background;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        PlayerModel p = (PlayerModel) obj;

        return this.getPlayerId() == p.getPlayerId()
                && this.getPlayerName().equals(p.getPlayerName())
                && this.getPos().equals(p.getPos())
                && this.getSubstitutions().equals(p.getSubstitutions())
                && this.getSubsMinute().equals(p.getSubsMinute())
                && this.getRating() == p.getRating()
                && this.getRatingCount() == p.getRatingCount()
                && this.getPlayerComment() == p.getPlayerComment()
                && this.getUpdated().equals(p.getUpdated());
    }

    @Override
    public int compareTo(@NonNull PlayerModel o) {

        int ret1 = getOrder().compareTo(o.getOrder());
        return ret1;
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
            case "LCD":
                return 26;
            case "CD":
                return 27;
            case "CD-R":
            case "RCD":
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
}

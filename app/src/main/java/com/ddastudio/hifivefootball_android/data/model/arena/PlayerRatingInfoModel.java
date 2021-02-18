package com.ddastudio.hifivefootball_android.data.model.arena;

import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerStatModel;
import com.ddastudio.hifivefootball_android.utils.DateUtils;
import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by hongmac on 2017. 11. 13..
 */

public class PlayerRatingInfoModel implements MultiItemEntity {

    String TeamImageBaseUrl = "http://cdn.hifivesoccer.com/team/18/";
    String TeamLargeImageBaseUrl = "http://cdn.hifivesoccer.com/team/180/";

    int itemType;
    @SerializedName("content_id") int contentId;
    @SerializedName("match_id") int matchId;
    @SerializedName("player") PlayerModel player;
    @SerializedName("comment") String playerComment;
    @SerializedName("rating") float playerRating;
    @SerializedName("time_seq") String timeSeq;
    @SerializedName("hifive") int commentHifive;
    @SerializedName("substitutions") String substitutions;
    @SerializedName("subs_minute") String subsMinute;
    @SerializedName("avg_rating") float playerAvgRating;
    @SerializedName("user_rating_count") int userRatingCount;
    @SerializedName("user_comment_count") int userCommentCount;
    @SerializedName("updated") Date updated;
    @SerializedName("user") UserModel user;

    public PlayerRatingInfoModel() {
        this.itemType = ViewType.PLAYER_RATING_INFO;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public Integer getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    int userUpNDonw = 0;
    List<PlayerRatingRecordModel> playerRatingRecordList;

    public int getMatchId() {
        return matchId;
    }

    public PlayerModel getPlayer() {
        return player;
    }

    public int getPlayerId() {
        if ( player != null ) {
            return player.getPlayerId();
        }

        return 0;
    }

    public String getPlayerName() {

        if ( player != null ) {
            return player.getPlayerName();
        }

        return "";
    }

    public String getPlayerComment() {
        return TextUtils.isEmpty(playerComment) ? "" : playerComment;
    }

    public void setPlayerComment(String playerComment) {
        this.playerComment = playerComment;
    }

    public float getPlayerRating() {
        return playerRating;
    }

    public String getPlayerRatingString() {
        return String.valueOf(playerRating);
    }

    public float getPlayerAvgRating() {
        return playerAvgRating;
    }

    public String getPlayerAvgRatingString() {

        // 평균값이 0이면 유저가 세팅한 값으로 보여둔다.
        // 유저가 점수를 주고 바로 데이터를 업데이트 칠때 평균을 계산할 수 없기 때문에 평균이 0점이면 유저 점수가 평균이 된다.
        if ( playerAvgRating == 0.0 ) {

            return getPlayerRatingString();
        }

        return String.valueOf(playerAvgRating);
    }

    public String getPlayerAvgRatingAndCountString() {
        return "사용자 평점" + String.valueOf(playerAvgRating) + " (" + userRatingCount + ")";
    }

    public void setPlayerRating(float playerRating) {
        this.playerRating = playerRating;
    }

    public String getTimeSeq() {
        return timeSeq;
    }

    public void setTimeSeq(String timeSeq) {
        this.timeSeq = timeSeq;
    }

    public int getUserUpNDonw() {
        return userUpNDonw;
    }

    public void setUserUpNDonw(int userUpNDonw) {
        this.userUpNDonw = userUpNDonw;
    }

    public int getCommentHifive() {
        return commentHifive;
    }

    public void addHifiveCount(int count) {

        commentHifive = commentHifive + count;
    }

    public String getCommentHifiveString() {
        return String.valueOf(commentHifive);
    }

    public int getUserRatingCount() {
        return userRatingCount;
    }

    public void addUserRatingCount() {
        userRatingCount = userRatingCount + 1;
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

    public String getUpdated() {
        if ( updated == null ) {
            updated = Calendar.getInstance().getTime();
        }

        return DateUtils.getPrettTime(updated);
    }

    public String getSubstitutions() {

        if ( player == null ) return "";

        return player.getSubstitutions();
    }

    public String getSubsMinute() {

        if ( player == null ) return "";
        return player.getSubsMinute();
    }

    public boolean hasGoals() {

        if ( player == null )
            return false;

        return player.hasGoals();
    }

    public boolean hasYellowCards() {

        if ( player == null )
            return false;

        return player.hasYellowCards();
    }

    public boolean hasRedCards() {

        if ( player == null )
            return false;

        return player.hasRedCards();
    }

    public boolean hasSubstitutions() {

        if ( player == null )
            return false;

        return player.hasSubstitutions();
    }

    public String getNickName() {

        return user == null ? "" : user.getNickname();
    }

    public String getUserName() {
        return user == null ? "" : user.getUsername();
    }

}

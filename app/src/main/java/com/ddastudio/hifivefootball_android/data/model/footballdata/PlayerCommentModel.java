package com.ddastudio.hifivefootball_android.data.model.footballdata;

import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.utils.DateUtils;
import com.google.gson.annotations.SerializedName;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by hongmac on 2018. 1. 8..
 */

public class PlayerCommentModel implements MultiItemEntity {

    String PlayerLargeImageBaseUrl = "http://cdn.hifivefootball.com/player/";

    public int itemType;
    @SerializedName("pc_id") int playerCommentId;
    @SerializedName("match_id") int matchId;
    @SerializedName("player_id") int playerId;
    @SerializedName("player_img_id") int playerImageid;
    @SerializedName("player_name") String playerName;
    @SerializedName("comment") String comment;
    @SerializedName("hifive") int hifiveCount;
    @SerializedName("ingame") int ingame;
    @SerializedName("rating") float playerRating;
    @SerializedName("user") UserModel user;
    @SerializedName("created") Date created;

    public PlayerCommentModel() {
        itemType = ViewType.PLAYER_COMMENT;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    /*--------------------------------------------------------------*/

    public int getPlayerCommentId() {
        return playerCommentId;
    }

    public int getMatchId() {
        return matchId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getComment() {
        return comment;
    }

    public void addHifiveCount(int count) {

        hifiveCount = hifiveCount + count;
    }

    public int getHifiveCount() {
        return hifiveCount;
    }

    public String getHifiveCountString() {
        return String.valueOf(hifiveCount);
    }

    public int getIngame() {
        return ingame;
    }

    public float getPlayerRating() {
        return playerRating;
    }

    public String getPlayerRatingString() {

        return String.format("%.1f", playerRating);
    }

    public UserModel getUser() {
        return user;
    }

    public String getCreated() {

        if ( created == null ) {
            created = Calendar.getInstance().getTime();
        }

        return DateUtils.getPrettTime(created);
    }

    /*--------------------------------------------------------------*/

    public String getNickNameAndUserName() {

        return user == null ? "" : String.format("%s(%s)", user.getNickname(), user.getUsername());
    }

    public String getNickName() {

        return user == null ? "" : user.getNickname();
    }

    public String getPlayerLargeImageUrl() {

        return PlayerLargeImageBaseUrl + playerImageid + ".png";
    }
}

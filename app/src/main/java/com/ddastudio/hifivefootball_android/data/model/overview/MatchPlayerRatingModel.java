package com.ddastudio.hifivefootball_android.data.model.overview;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;

/**
 * Created by hongmac on 2017. 12. 13..
 */

public class MatchPlayerRatingModel implements MultiItemEntity {

    public int itemType;
    PlayerModel bestPlayer;
    PlayerModel worstPlayer;

    public MatchPlayerRatingModel() {
        itemType = ViewType.MATCH_OVERVIEW_PLAYER_RATING;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    /*---------------------------------------------------------------------*/

    public PlayerModel getBestPlayer() {
        return bestPlayer;
    }

    public void setBestPlayer(PlayerModel bestPlayer) {
        this.bestPlayer = bestPlayer;
    }

    public String getBestPlayerName() {
        if ( bestPlayer != null ) {
            return bestPlayer.getPlayerName();
        }

        return "";
    }

    public String getBestPlayerImageUrl() {
        if ( bestPlayer != null ) {
            return bestPlayer.getPlayerLargeImageUrl();
        }

        return "";
    }

    public Float getBestPlayerRating() {
        if ( bestPlayer != null ) {
            return bestPlayer.getRating();
        }

        return 0f;
    }

    public String getBestPlayerRatingString() {
        if ( bestPlayer != null ) {
            return bestPlayer.getRatingString();
        }

        return "MVP";
    }

    /*-------------------------------------------------------------------*/

    public PlayerModel getWorstPlayer() {
        return worstPlayer;
    }

    public void setWorstPlayer(PlayerModel worstPlayer) {
        this.worstPlayer = worstPlayer;
    }

    public String getWorstPlayerName() {
        if ( worstPlayer != null ) {
            return worstPlayer.getPlayerName();
        }

        return "";
    }

    public String getWorstPlayerImageUrl() {
        if ( worstPlayer != null ) {
            return worstPlayer.getPlayerLargeImageUrl();
        }

        return "";
    }

    public Float getWorstPlayerRating() {
        if ( worstPlayer != null ) {
            return worstPlayer.getRating();
        }

        return 0f;
    }

    public String getWorstPlayerRatingString() {
        if ( worstPlayer != null ) {
            return worstPlayer.getRatingString();
        }

        return "WORST";
    }
}

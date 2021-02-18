package com.ddastudio.hifivefootball_android.match_summery.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;
import com.google.gson.annotations.SerializedName;

import timber.log.Timber;

/**
 * Created by hongmac on 2017. 12. 23..
 */

public class PlayerSectionModel implements MultiItemEntity {

    String CompetitionImageBaseUrl = "http://cdn.hifivefootball.com/competitions/18/";
    String CompetitionLargeImageBaseUrl = "http://cdn.hifivefootball.com/competitions/180/";
    String TeamImageBaseUrl = "http://cdn.hifivefootball.com/team/18/";
    String TeamLargeImageBaseUrl = "http://cdn.hifivefootball.com/team/180/";

    String PlayerImageBaseUrl = "http://cdn.hifivefootball.com/player/48/";
    String PlayerImageLargeUrl = "http://cdn.hifivefootball.com/player/96/";

    public int itemType;

    @SerializedName("id") int playerId;
    @SerializedName("name") String nameKor;
    @SerializedName("hits") int hits;
    @SerializedName("soccerwiki_id") int soccerwiki_id;
    @SerializedName("team")
    TeamModel team;

    public PlayerSectionModel() {
        itemType = ViewType.MATCH_PLAYER_SECTION;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getNameKor() {
        return nameKor;
    }

    public int getHits() {
        return hits;
    }

    public String getHitString() {

        return String.valueOf(hits);
    }

    //    public int getEmblemId() {
//        return emblemId;
//    }
//
//    public String getEmblemUrl() {
//        return TeamImageBaseUrl + emblemId + ".png";
//    }

    public boolean hasPlayerAvatar() {
//        if ( soccerwiki_id != 0 )
//            return true;

        return true;
    }

    public String getPlayerAvatar() {

        return PlayerImageBaseUrl + playerId + ".png";
    }

    public String getPlayerLargeAvatar() {

        return PlayerImageLargeUrl + playerId + ".png";
    }

    public String getTeamName() {

        if ( team != null ) {
            return team.getTeamName();
        }

        return "";
    }

    public String getTeamEmblemUrl() {

        if ( team != null ) {
            return team.getEmblemUrl();
        }

        return "";
    }
}

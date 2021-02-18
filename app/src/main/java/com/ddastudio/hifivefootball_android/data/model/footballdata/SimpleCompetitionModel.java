package com.ddastudio.hifivefootball_android.data.model.footballdata;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.common.ViewType;

/**
 * Created by hongmac on 2018. 1. 12..
 */

public class SimpleCompetitionModel implements MultiItemEntity {

    String CompetitionImageBaseUrl = "http://cdn.hifivefootball.com/competitions/180/";
    String CompetitionSmallImageBaseUrl = "http://cdn.hifivefootball.com/competitions/45/";

    int itemType;
    String competitionName;
    int imageId;

    public SimpleCompetitionModel(String competitionName, int imageId) {
        this.competitionName = competitionName;
        this.imageId = imageId;
        itemType = ViewType.SIMPLE_COMPETITION_DATE;
    }
    @Override
    public int getItemType() {
        return itemType;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public String getCompetitionImageUrl() {

        return CompetitionImageBaseUrl + imageId + ".png";
    }

    public String getCompetitionSmallImageUrl() {

        return CompetitionSmallImageBaseUrl + imageId + ".png";
    }
}

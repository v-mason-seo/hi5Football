package com.ddastudio.hifivefootball_android.data.model.football;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.common.ViewType;

/**
 * Created by hongmac on 2018. 1. 3..
 */

public class MatchDateModel implements MultiItemEntity {

    int itemType;
    String title;

    public MatchDateModel(String title) {
        itemType = ViewType.SIMPLE_MATCH_DATE;
        this.title = title;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getTitle() {
        return title;
    }
}

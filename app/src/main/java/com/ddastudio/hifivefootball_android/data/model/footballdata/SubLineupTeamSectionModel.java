package com.ddastudio.hifivefootball_android.data.model.footballdata;

import android.support.annotation.ColorInt;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.ViewType;

/**
 * Created by hongmac on 2018. 2. 7..
 */

public class SubLineupTeamSectionModel implements MultiItemEntity {

    int itemType;
    String title;
    @ColorInt int color;

    public SubLineupTeamSectionModel(String title) {
        itemType = ViewType.SUB_LINEUP_TEAM_SECTION;
        this.title = title;
        this.color = R.color.md_lime_200;
    }

    public SubLineupTeamSectionModel(String title, @ColorInt int color) {
        itemType = ViewType.SUB_LINEUP_TEAM_SECTION;
        this.title = title;
        this.color = color;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}

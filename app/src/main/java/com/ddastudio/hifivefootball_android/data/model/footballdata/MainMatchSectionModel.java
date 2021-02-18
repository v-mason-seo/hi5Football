package com.ddastudio.hifivefootball_android.data.model.footballdata;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.common.ViewType;

/**
 * Created by hongmac on 2017. 12. 27..
 */

public class MainMatchSectionModel implements MultiItemEntity {

    int itemType;
    String title;

    public MainMatchSectionModel(String title) {
        itemType = ViewType.MAIN_MATCH_SECTION;
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

    public void setTitle(String title) {
        this.title = title;
    }
}

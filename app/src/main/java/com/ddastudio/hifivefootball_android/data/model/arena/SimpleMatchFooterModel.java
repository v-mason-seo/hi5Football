package com.ddastudio.hifivefootball_android.data.model.arena;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.common.ViewType;

/**
 * Created by hongmac on 2017. 12. 28..
 */

public class SimpleMatchFooterModel implements MultiItemEntity {

    int itemType;

    public SimpleMatchFooterModel() {
        itemType = ViewType.SIMPLE_MATCH_FOOTER;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }
}

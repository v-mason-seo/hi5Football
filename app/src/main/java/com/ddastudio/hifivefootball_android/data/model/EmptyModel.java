package com.ddastudio.hifivefootball_android.data.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.common.ViewType;

/**
 * Created by hongmac on 2018. 1. 18..
 */

public class EmptyModel implements MultiItemEntity {

    int itemType;
    public EmptyModel() {
        this.itemType = ViewType.EMPTY_TYPE;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}

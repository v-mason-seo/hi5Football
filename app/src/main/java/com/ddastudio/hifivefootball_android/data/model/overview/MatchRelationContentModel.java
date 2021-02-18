package com.ddastudio.hifivefootball_android.data.model.overview;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.common.ViewType;

/**
 * Created by hongmac on 2017. 12. 17..
 */

public class MatchRelationContentModel implements MultiItemEntity {

    public int itemType;

    public MatchRelationContentModel() {
        itemType = ViewType.MATCH_OVERVIEW_RELATION_CONTENT;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}

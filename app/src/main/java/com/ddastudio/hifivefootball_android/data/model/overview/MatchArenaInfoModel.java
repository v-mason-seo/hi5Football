package com.ddastudio.hifivefootball_android.data.model.overview;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.common.ViewType;

/**
 * Created by hongmac on 2018. 2. 4..
 */

public class MatchArenaInfoModel implements MultiItemEntity {

    public int itemType;

    public MatchArenaInfoModel() {
        itemType = ViewType.MATCH_OVERVIEW_ARENA_INFO;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}

package com.ddastudio.hifivefootball_android.data.model.arena_chat;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.common.ViewType;

/**
 * Created by hongmac on 2018. 3. 25..
 */

public class ArenaImageModel implements MultiItemEntity{

    int itemType;
    String url;

    public ArenaImageModel(String url) {
        this.url = url;
        this.itemType = ViewType.ARENA_IMAGE;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

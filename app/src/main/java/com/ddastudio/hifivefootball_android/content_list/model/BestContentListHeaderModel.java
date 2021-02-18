package com.ddastudio.hifivefootball_android.content_list.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.common.EntityType;
import com.ddastudio.hifivefootball_android.common.ViewType;

/**
 * Created by hongmac on 2017. 12. 7..
 * 베스트 글 날짜 정보
 */

public class BestContentListHeaderModel implements MultiItemEntity {

    int itemType;
    String header;

    public BestContentListHeaderModel(String header) {
        this.header = header;
        this.itemType = ViewType.CONTENT_BEST_DATE_HEADER;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

}

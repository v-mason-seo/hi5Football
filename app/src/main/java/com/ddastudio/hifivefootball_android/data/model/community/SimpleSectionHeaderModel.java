package com.ddastudio.hifivefootball_android.data.model.community;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.common.ViewType;

/**
 * Created by hongmac on 2018. 3. 2..
 */

public class SimpleSectionHeaderModel implements MultiItemEntity{

    int itemType;
    String title;
    boolean bVisibleMoreButton;

    public SimpleSectionHeaderModel(String title) {
        this.itemType = ViewType.SIMPLE_SECTION_HEADER;
        this.title = title;
        this.bVisibleMoreButton = false;
    }

    public SimpleSectionHeaderModel(String title, boolean isVisibleMoreButton) {
        this.itemType = ViewType.SIMPLE_SECTION_HEADER;
        this.title = title;
        this.bVisibleMoreButton = isVisibleMoreButton;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    /*-------------------------------------------------------*/

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isbVisibleMoreButton() {
        return bVisibleMoreButton;
    }

    public void setbVisibleMoreButton(boolean bVisibleMoreButton) {
        this.bVisibleMoreButton = bVisibleMoreButton;
    }
}

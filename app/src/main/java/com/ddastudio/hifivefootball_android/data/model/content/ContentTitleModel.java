package com.ddastudio.hifivefootball_android.data.model.content;

import com.ddastudio.hifivefootball_android.data.model.MultipleItem;

/**
 * Created by hongmac on 2017. 9. 14..
 */

public class ContentTitleModel extends MultipleItem {

    String title;

    public ContentTitleModel() {
        this.title = "";
    }

    public ContentTitleModel(String title) {
        this.title = title;
    }

    @Override
    public int getItemType() {
        return MultipleItem.CONTENT_TITLE;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

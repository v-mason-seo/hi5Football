package com.ddastudio.hifivefootball_android.data.model.community;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;

public class EditorTitleModel implements MultiItemEntity {

    int itemType;
    String title;

    public EditorTitleModel() {
        this.title = "";
        this.itemType = MultipleItem.EDITOR_TITLE;
    }

    public EditorTitleModel(String title) {
        this.title = title;
        this.itemType = MultipleItem.EDITOR_TITLE;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    @Override
    public int getItemType() {
        return itemType;
    }
}

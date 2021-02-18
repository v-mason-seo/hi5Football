package com.ddastudio.hifivefootball_android.data.model.content;

import com.ddastudio.hifivefootball_android.data.model.MultipleItem;

/**
 * Created by hongmac on 2017. 9. 14..
 */

public class ContentViewInfoModel extends MultipleItem {

    String info;

    public ContentViewInfoModel(int itemType, String info) {
        super(itemType);
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}

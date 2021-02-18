package com.ddastudio.hifivefootball_android.data.model.footballdata;

import com.ddastudio.hifivefootball_android.data.model.MultipleItem;

/**
 * Created by hongmac on 2017. 10. 27..
 */

public class LineupHeaderModel extends MultipleItem {

    String title;

    public LineupHeaderModel(String title) {
        this.title = title;
    }

    @Override
    public int getItemType() {
        return MultipleItem.MATCH_LINEUP_HEADER;
    }

    public String getTitle() {
        return title;
    }
}

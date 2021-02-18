package com.ddastudio.hifivefootball_android.data.model.footballdata;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;

/**
 * Created by hongmac on 2017. 11. 3..
 */

public class CompetitionMatchHeaderModel implements MultiItemEntity {

    String fixtureDate;

    public CompetitionMatchHeaderModel(String fixtureDate) {
        this.fixtureDate = fixtureDate;
    }

    public String getFixtureDate() {
        return fixtureDate;
    }

    public void setFixtureDate(String fixtureDate) {
        this.fixtureDate = fixtureDate;
    }

    @Override
    public int getItemType() {
        return MultipleItem.ARENA_MATCH_HEADER;
    }
}

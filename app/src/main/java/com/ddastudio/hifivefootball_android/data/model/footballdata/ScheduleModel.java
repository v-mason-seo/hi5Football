package com.ddastudio.hifivefootball_android.data.model.footballdata;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hongmac on 2017. 11. 1..
 */

public class ScheduleModel extends AbstractExpandableItem<MultiItemEntity> implements MultiItemEntity {

    @SerializedName("match_date") String matchDate;
    @SerializedName("pre_match_date") String preMatchDate;
    @SerializedName("next_match_date") String nextMatchDate;

    public String getMatchDate() {
        return matchDate;
    }

    public String getPreMatchDate() {
        return preMatchDate;
    }

    public String getNextMatchDate() {
        return nextMatchDate;
    }

    @Override
    public int getItemType() {
        return MultipleItem.ARENA_DATE;
    }

    @Override
    public int getLevel() {
        return 0;
    }
}

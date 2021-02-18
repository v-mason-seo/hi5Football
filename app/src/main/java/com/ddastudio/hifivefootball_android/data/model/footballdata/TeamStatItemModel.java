package com.ddastudio.hifivefootball_android.data.model.footballdata;

import com.ddastudio.hifivefootball_android.data.model.MultipleItem;

/**
 * Created by hongmac on 2017. 10. 30..
 */

public class TeamStatItemModel extends MultipleItem {

    String key;
    String value;
    String group;

    public TeamStatItemModel(String key, String value) {
        this.key = key;
        this.value = value;
        this.group = "A";
    }

    public TeamStatItemModel(String key, String value, String group) {
        this.key = key;
        this.value = value;
        this.group = group;
    }

    @Override
    public int getItemType() {
        return TEAM_STATS;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getGroup() {
        return group;
    }
}

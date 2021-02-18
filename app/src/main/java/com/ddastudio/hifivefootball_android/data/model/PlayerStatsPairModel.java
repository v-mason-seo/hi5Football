package com.ddastudio.hifivefootball_android.data.model;

import android.text.TextUtils;
import android.util.Pair;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by hongmac on 2017. 10. 26..
 */

public class PlayerStatsPairModel implements MultiItemEntity {

    String name;
    String value;
    int res;


    public PlayerStatsPairModel(String first, String second, int res) {
        this.name = first;
        this.value = second;
        this.res = res;
    }

    public String getName() {
        return name;
    }

    public String getValue() {

        return TextUtils.isEmpty(value) ? "-" : value;
    }

    public int getRes() {
        return res;
    }

    @Override
    public int getItemType() {
        return MultipleItem.BOTTOM_MATCH_PLAYER_STATS;
    }
}

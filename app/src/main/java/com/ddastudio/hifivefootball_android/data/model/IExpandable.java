package com.ddastudio.hifivefootball_android.data.model;

import java.util.List;

/**
 * Created by hongmac on 2017. 10. 17..
 */

public interface IExpandable<T> {

    boolean isExpanded();
    void setExpanded(boolean expanded);
    List<T> getSubItems();

    /**
     * Get the level of this item. The level start from 0.
     * If you don't care about the level, just return a negative.
     */
    int getLevel();
}

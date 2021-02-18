package com.ddastudio.hifivefootball_android.data.model.footballdata;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.chad.library.adapter.base.entity.SectionEntity;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.utils.DateUtils;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by hongmac on 2017. 12. 27..
 */

public class SimpleMatchHeaderModel implements MultiItemEntity {

    int itemType;
    String sectionHeader;

    public SimpleMatchHeaderModel(String sectionHeader) {
        itemType = ViewType.MATCH_SECTION_HEADER;
        this.sectionHeader = sectionHeader;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getSectionHeader() {

        Date today = Calendar.getInstance().getTime();

        if ( sectionHeader.equals(DateUtils.convertDateToString(today, "yyyy-MM-dd", -1))) {
            return "어제";
        } else if ( sectionHeader.equals(DateUtils.convertDateToString(today, "yyyy-MM-dd", 0))) {
            return "오늘";
        } else if ( sectionHeader.equals(DateUtils.convertDateToString(today, "yyyy-MM-dd", 1))) {
            return "내일";
        }

        return sectionHeader;
    }

    public void setSectionHeader(String sectionHeader) {
        this.sectionHeader = sectionHeader;
    }
}

package com.ddastudio.hifivefootball_android.nunting_bset.model;

import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.utils.DateUtils;

import java.util.Date;

import static com.ddastudio.hifivefootball_android.common.NuntingViewType.POSTS_HEADER;

public class PostsHeaderModel implements MultiItemEntity {

    int itemType;
    String header;

    public PostsHeaderModel(String header) {
        this.itemType = POSTS_HEADER;
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String tvHeader) {
        this.header = tvHeader;
    }

    public String getHeaderFormat() {

        if (TextUtils.isEmpty(header)) {
            return "";
        }

        try {
            Date headerDate = DateUtils.toDate(header, "yyyyMMddHH");
            return DateUtils.convertDateToString(headerDate, "yyyy.MM.dd  a hh 시");
        } catch (Exception ex) {
            return "";
        }

    }

    @Override
    public int getItemType() {
        return itemType;
    }
}

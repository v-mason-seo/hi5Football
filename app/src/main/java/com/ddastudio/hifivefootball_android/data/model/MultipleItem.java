package com.ddastudio.hifivefootball_android.data.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by hongmac on 2017. 9. 14..
 */

public class MultipleItem implements MultiItemEntity {

    // 본문
    public static final int CONTENT_TITLE = 998;
    public static final int CONTENT_BODY = 999;
    //public static final int CONTENT_HIFIVE = 0;
    //public static final int CONTENT_TAG = 1;
    //public static final int CONTENT_WRITER_INFO = 2;
    public static final int CONTENT_COMMENT_INFO = 3;
    public static final int CONTENT_COMMENT_LIST = 4;
    public static final int CONTENT_RELATION = 5;

    // 대댓글
    public static final int COMMENT_OUT = 0;
    public static final int COMMENT_IN = 1;
    public static final int COMMENT_INPUT = 2;

    // 사용자가 쓴글 댓글 ...
    public static final int USER_CONTENT = 0;
    public static final int USER_COMMENT = 1;

    public final static int ISSUE_TYPE_OPEN = 1000;
    public final static int ISSUE_TYPE_CLOSE = 1100;


    public final static int EDITOR_TEXT = 5000;
    public final static int EDITOR_IMAGE = 5001;
    public final static int EDITOR_LINK = 5002;
    public final static int EDITOR_VIDEO = 5005;
    public final static int EDITOR_TITLE = 5006;
    public final static int EDITOR_TAG = 5016;

    public final static int ARENA_COMPETITON = 2000;
    public final static int ARENA_SCHEDULE = 2001;
    public final static int ARENA_SCHEDULE_FOOTER = 2002;
    public final static int ARENA_MATCH_HEADER = 2003;
    public final static int ARENA_DATE = 2010;
    public final static int ARENA_LAST_SCHEDULE = 2031;

    public final static int BOTTOM_STANDING_HEADER = 2999;
    public final static int BOTTOM_STANDING = 3000;
    public final static int BOTTOM_MATCH_PLAYER_STATS = 3001;
    public final static int BOTTOM_TEAM_LIST = 3002;
    public final static int BOTTOM_PLAYER_LIST = 3003;

    public final static int MATCH_EVENTS_LOCAL_TEAM = 4000;
    public final static int MATCH_EVENTS_VISITOR_TEAM = 4100;

    public final static int MATCH_LINEUP_HEADER = 4500;
    public final static int MATCH_LINEUP = 4501;

    public final static int PLAYER_BASIC_INFO = 5000;
    public final static int PLAYER_STATS = 5100;

    public final static int TEAM_BASIC_INFO = 5500;
    public final static int TEAM_STATS = 5600;

    public final static int ADMIN_COMPETITION = 6000;

    public final static int USER_TYPE = 55731;

    public int itemType;

    public MultipleItem() {

    }

    public MultipleItem(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }
}

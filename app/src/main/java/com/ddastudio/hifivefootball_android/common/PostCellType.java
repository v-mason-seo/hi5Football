package com.ddastudio.hifivefootball_android.common;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hongmac on 2018. 2. 27..
 */

public enum PostCellType {

    BASE(0),

    MATCH_CHAT(1),

    MATCH_REPORT(2),

    PREVIEW(3),

    SMALL_PREVIEW(4),

    PLAYER_TALK(5),

    TEAM_TALK(6)
    ;

    final int value;

    PostCellType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static PostCellType toEnum(int val) {
        for (PostCellType cellType : PostCellType.values()) {
            if ( cellType.value() == val ) {
                return cellType;
            }
        }

        // 찾지 못했다면 기본값으로 리턴.
        return BASE;
    }
}

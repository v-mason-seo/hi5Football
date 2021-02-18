package com.ddastudio.hifivefootball_android.common;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hongmac on 2018. 2. 27..
 */

public enum  PostBodyType {

    HTML(0),

    PLAIN(1),

    MARKDOWN(2),

    LINK(3);


    final int value;

    PostBodyType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static PostBodyType toEnum(int val) {
        for (PostBodyType bodyType : PostBodyType.values()) {
            if ( bodyType.value() == val ) {
                return bodyType;
            }
        }

        // 찾지 못했다면 기본값으로 리턴.
        return HTML;
    }
}

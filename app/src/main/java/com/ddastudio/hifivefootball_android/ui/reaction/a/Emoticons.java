package com.ddastudio.hifivefootball_android.ui.reaction.a;

import com.ddastudio.hifivefootball_android.common.PostBoardType;

/**
 * Created by hongmac on 2018. 2. 8..
 */

public enum Emoticons {
    S0(0),
    S1(1),
    S2(2),
    S3(3),
    S4(4),
    S5(5),
    S6(6),
    S7(7),
    S8(8),
    S9(9),
    S10(10),
    S11(11);

    final int value;

    Emoticons(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static Emoticons toEnum(int val) {
        for (Emoticons boardType : Emoticons.values()) {
            if ( boardType.value() == val ) {
                return boardType;
            }
        }

        // 찾지 못했다면 기본값으로 리턴.
        return S1;
    }
}

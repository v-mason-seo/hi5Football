package com.ddastudio.hifivefootball_android.common;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by hongmac on 2018. 2. 27..
 */

public enum  PostBoardType {

    ALL_BOARD(-1, "전체") {
        @Override
        public String toString() {
            return "전체게시판";
        }
    },

    INSTRUCTION(1, "사용법") {
        @Override
        public String toString() {
            return "사용법";
        }
    },

    NOTIFICATION_A(0, "공지") {
        @Override
        public String toString() {
            return "공지A";
        }
    },

    NOTIFICATION_B(10, "공지") {
        @Override
        public String toString() {
            return "공지B";
        }
    },

    FOOTBALL(200, "축구") {
        @Override
        public String toString() {
            return "축구게시판";
        }
    },

    PLAYER_SCOUT(240, "선수스카웃") {
        @Override
        public String toString() {
            return "선수스카웃";
        }
    },

    NEWS(300, "뉴스") {
        @Override
        public String toString() {
            return "축구 소식";
        }
    },

    COLUMN(350, "컬럼") {
        @Override
        public String toString() {
            return "컬럼";
        }
    },

    VIDEO(360, "축동") {
        @Override
        public String toString() {
            return "축구동영상";
        }
    },

    FREE(400, "자유") {
        @Override
        public String toString() {
            return "자유게시판";
        }
    },

    HUMOR(420, "유머") {
        @Override
        public String toString() {
            return "유머게시판";
        }
    },

    CLUB(500, "클럽") {
        @Override
        public String toString() {
            return "클럽";
        }
    },

    PLAYER(600, "플레이어") {
        @Override
        public String toString() {
            return "플레이어";
        }
    },

    MATCH_REPORT(610, "평가") {
        @Override
        public String toString() {
            return "매치 리포트";
        }
    },

    ISSUE(700, "건/개") {
        @Override
        public String toString() {
            return "건의/개선";
        }
    },

    REPORT(1000, "신고") {
        @Override
        public String toString() {
            return "신고";
        }
    };

    final int value;
    final String shortName;

    PostBoardType(int value, String shortName) {
        this.value = value;
        this.shortName = shortName;
    }

    public int value() {
        return value;
    }

    public String getShortName() {
        return shortName;
    }

    public static PostBoardType toEnum(int val) {
        for (PostBoardType boardType : PostBoardType.values()) {
            if ( boardType.value() == val ) {
                return boardType;
            }
        }

        // 찾지 못했다면 기본값으로 리턴.
        return FOOTBALL;
    }

    /**
     * 글쓰기 가능한 게시판 목록
     * @return
     */
    public static PostBoardType[] getCreatableBoard() {

        //
        // 2018.05.07 자유게시판 사용하지 않음
        //
        PostBoardType[] boards = new PostBoardType[]{FOOTBALL, FREE, ISSUE};

        return boards;
    }
}

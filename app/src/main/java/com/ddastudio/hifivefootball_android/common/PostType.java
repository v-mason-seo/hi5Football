package com.ddastudio.hifivefootball_android.common;

/**
 * Created by hongmac on 2018. 2. 27..
 */

public enum PostType {

    /**
     * 전체 게시판
     */
    BOARD_ALL(0),

    /**
     * 각각 게시판
     */
    BOARD_SINGLE(1),

    /**
     * 전체 하이파이브 베스트
     */
    BEST_HIFIVE_ALL(2),

    /**
     * 개별 하이파이브 베스트
     */
    BEST_HIFIVE_SINGLE(3),

    /**
     * 전체 댓글 베스트
     */
    BEST_COMMENT_ALL(4),

    /**
     * 개별 댓글 베스트
     */
    BEST_COMMENT_SINGLE(5),

    /**
     * 매치 관련글
     */
    MATCH_RELATION(6),

    /**
     * 팀 관련글
     */
    TEAM_RELATION(7),

    /**
     * 플레이어 관련글
     */
    PLAYER_RELATION(8);

    final int value;

    PostType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static PostType toEnum(int val) {
        for (PostType postType : PostType.values()) {
            if ( postType.value() == val ) {
                return postType;
            }
        }

        // 찾지 못했다면 기본값으로 리턴.
        return BOARD_ALL;
    }
}

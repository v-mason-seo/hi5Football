package com.ddastudio.hifivefootball_android.common;

/**
 * 입력 또는 수정 글쓰기 모드
 *
 */
public enum  PostEditorType {

    //
    // 입력 ( 기본값 )
    //
    INSERT(0),
    //
    // 수정
    //
    EDIT(1)
    ;

    final int value;

    PostEditorType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static PostEditorType toEnum(int val) {
        for(PostEditorType editorType : PostEditorType.values()) {
            if ( editorType.value == val) {
                return editorType;
            }
        }

        // 찾지 못했다면 기본값으로 리턴.
        return INSERT;
    }

}

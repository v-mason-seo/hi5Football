package com.ddastudio.hifivefootball_android.data.event;

import android.widget.EditText;

import com.ddastudio.hifivefootball_android.common.PostBodyType;
import com.ddastudio.hifivefootball_android.data.model.OpenGraphModel;

/**
 * Created by hongmac on 2017. 9. 9..
 */

public class EditorEvent {

    private EditorEvent() {

    }

    /**
     * 게시글을 작성할 게시판 선택 이벤트
     */
    public static class SelectBoardEvent {

        final int boardId;

        public SelectBoardEvent(int boardId) {
            this.boardId = boardId;
        }

        public int getBoardId() {
            return boardId;
        }
    }

    /**
     * 에디터 액션 버튼 클릭 이벤트 ( 볼드, 이탤릭, 이미지, 링크, 글자크기 등 )
     */
    public static class ActionButtonClickEvent {

        final int viewId;
        final String value;
        final PostBodyType editorType;
        OpenGraphModel ogValue; // Link Preview 데이터

        public ActionButtonClickEvent(int viewId, String value, PostBodyType editorType) {
            this.viewId = viewId;
            this.value = value;
            this.editorType = editorType;
        }

        public ActionButtonClickEvent(int viewId, String value, PostBodyType editorType, OpenGraphModel og) {
            this.viewId = viewId;
            this.value = value;
            this.editorType = editorType;
            this.ogValue = og;
        }

        public int getViewId() {
            return viewId;
        }

        public String getValue() {
            return value;
        }

        public PostBodyType getEditorType() {
            return editorType;
        }

        public OpenGraphModel getOgValue() {
            return ogValue;
        }
    }

    /**
     * 글쓰기 이벤트
     */
    public static class PostContentEvent {

        PostBodyType editorType;

        public PostContentEvent(PostBodyType editorType) {
            this.editorType = editorType;
        }

        public PostBodyType getEditorType() {
            return editorType;
        }

    }

    public static class PostContentStatusEvent {

        public static int STATUS_COMPLETE = 0;
        public static int STATUS_ERROR = 1;
        int selectedBoardId;
        int result;

        public PostContentStatusEvent(int result, int boardId) {
            this.result = result;
            this.selectedBoardId = boardId;
        }

        public int getResult() {
            return result;
        }

        public int getSelectedBoardId() {
            return selectedBoardId;
        }
    }

    /**
     * 텍스트 수정 완료 이벤트
     */
    public static class EditTextEvent {

        boolean complete;
        int position;
        String text;

        public EditTextEvent(boolean result, int position, String text) {
            this.complete = result;
            this.position = position;
            this.text = text;
        }

        public boolean isComplete() {
            return complete;
        }

        public int getPosition() {
            return position;
        }

        public String getText() {
            return text;
        }
    }

}

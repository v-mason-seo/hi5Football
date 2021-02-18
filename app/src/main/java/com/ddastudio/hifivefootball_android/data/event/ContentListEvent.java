package com.ddastudio.hifivefootball_android.data.event;

import com.ddastudio.hifivefootball_android.board.model.BoardMasterModel;
import com.ddastudio.hifivefootball_android.common.PostBoardType;

import org.parceler.Parcel;

/**
 * Created by hongmac on 2017. 9. 11..
 */

public class ContentListEvent {

    /**
     * 게시판 리스트에서 게시판을 선택한다.
     *  - ContentContainerFragment 뷰페이저 포지션을 변경한다.
     *  - BoardListFragment -> ContentContainerFragment 로 보내는 이벤트
     */
    public static class SelectedBoardEvent {

        // 1. 선택된 보드 데이터
        BoardMasterModel mBoard;
        // 2. 베스트글 여부
        boolean isBest;

        public SelectedBoardEvent(BoardMasterModel board) {
            this.mBoard = board;
            this.isBest = false;
        }

        public SelectedBoardEvent(BoardMasterModel board, boolean isBest) {
            this.mBoard = board;
            this.isBest = isBest;
        }

        public BoardMasterModel getBoard() {
            return mBoard;
        }

        public boolean isBest() {
            return isBest;
        }
    }

    /**
     * ContentContainerFragment -> ContentListFragment 로 보내는 이벤트
     *  - SelectedBoardEvent 이벤트로 ContentContainerFragment 뷰페이저
     *    위치 이동이 완료되고 이벤트를 발생시칸다.
     *  - 게시글 리스트 데이터를 새로고침한다.
     */
    public static class CompletedBoardEvent {

        // 1. 선택된 보드 데이터
        BoardMasterModel mBoard;
        // 2. 베스트글 여부
        boolean isBest;

        public CompletedBoardEvent(BoardMasterModel board) {
            this.mBoard = board;
            this.isBest = false;
        }

        public CompletedBoardEvent(BoardMasterModel board, boolean isBest) {
            this.mBoard = board;
            this.isBest = isBest;
        }

        public BoardMasterModel getBoard() {
            return mBoard;
        }

        public boolean isBest() {
            return isBest;
        }
    }

    /**
     * 게시판 리스트, 게시글 리스트 뷰페이전의 위치가 변경되면 발생하는 이벤트
     *  - 메인액티비티에서 툴바 타이틀 변경하기 위한 용도로 사용함.
     */
    public static class ContentContainerPageSelected {

        int position;

        public ContentContainerPageSelected(int position) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }
    }

    public static class RefreshListEvent {

        PostBoardType boardType;

        public RefreshListEvent(PostBoardType boardType) {
            this.boardType = boardType;
        }

        public PostBoardType getBoardid() {
            return boardType;
        }
    }

    public static class CurrentPosition {

        int currentPosition;

        public CurrentPosition(int position) {
            this.currentPosition = position;
        }

        public int getCurrentPosition() {
            return currentPosition;
        }
    }


    @Parcel
    public static class UpdateContentEvent {

        public final static int FAIL = 0;
        public final static int SUCCESS = 1;

        int contentId;
        int result;
        int position;
        boolean delete;
        boolean like;
        boolean scrap;
        int likeCount;
        int scrapCount;
        int commentCount;

        public UpdateContentEvent() {}

        public UpdateContentEvent(int contentId, int result, int position, boolean delete, boolean like, int likeCount, boolean scrap, int scrapCount, int commentCount) {
            this.contentId = contentId;
            this.result = result;
            this.position = position;
            this.delete = delete;
            this.like = like;
            this.likeCount = likeCount;
            this.scrap = scrap;
            this.scrapCount = scrapCount;
            this.commentCount = commentCount;
        }

        public int getContentId() {
            return contentId;
        }

        public int getResult() {
            return result;
        }

        public int getPosition() {
            return position;
        }

        public boolean isDelete() {
            return delete;
        }

        public boolean isLike() {
            return like;
        }

        public int getLikeCount() {
            return likeCount;
        }

        public boolean isScrap() {
            return scrap;
        }

        public int getScrapCount() {
            return scrapCount;
        }

        public void setContentId(int contentId) {
            this.contentId = contentId;
        }

        public void setResult(int result) {
            this.result = result;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public void setDelete(boolean delete) {
            this.delete = delete;
        }

        public void setLike(boolean like) {
            this.like = like;
        }

        public void setLikeCount(int likeCount) {
            this.likeCount = likeCount;
        }

        public void setScrap(boolean scrap) {
            this.scrap = scrap;
        }

        public void setScrapCount(int scrapCount) {
            this.scrapCount = scrapCount;
        }

        public int getCommentCount() {
            return commentCount;
        }

        public void setCommentCount(int commentCount) {
            this.commentCount = commentCount;
        }
    }
}

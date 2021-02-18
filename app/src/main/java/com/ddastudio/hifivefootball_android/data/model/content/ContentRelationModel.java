package com.ddastudio.hifivefootball_android.data.model.content;

import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.UserActionModel;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.utils.DateUtils;
import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by hongmac on 2017. 11. 28..
 */

public class ContentRelationModel extends MultipleItem {

    public static final int CONTENT_LIST = 0;
    public static final int BEST_CONTENT_LIST = 1;

    @SerializedName("content_id") int content_id;
    @SerializedName("title") String title;
    @SerializedName("arena_id") int arena_id;
    @SerializedName("deleted") int deleted;
    @SerializedName("reported") int reported;
    @SerializedName("comments") int comments;
    @SerializedName("scraps") int scraps;
    @SerializedName("preview") String preview;
    @SerializedName("likers") int likers;
    @SerializedName("board_id") int boardId;
    @SerializedName("created") Date created;
    @SerializedName("updated") Date updated;
    @SerializedName("user_action")
    UserActionModel userAction;
    @SerializedName("user")
    UserModel user;

    /*--------------------------------------------------------------*/

    public ContentRelationModel() { }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBoardId(int boardId) {
        this.boardId = boardId;
    }

    /**
     * 게시글 id
     *
     * @return
     */
    public int getContentId() {
        return content_id;
    }

    /**
     * 타이틀
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * 아레나 id
     *
     * @return
     */
    public int getArenaId() {
        return arena_id;
    }

    /**
     * 아레나 id 입력
     *
     * @param arena_id
     */
    public void setArenaId(int arena_id) {
        this.arena_id = arena_id;
    }

    public int getDeleted() {
        return deleted;
    }

    public int getReported() {
        return reported;
    }

    public int getScraps() {
        return scraps;
    }

    public int getComments() {
        return comments;
    }

    public String getPreview() {
        return preview;
    }

    public int getBoardId() {
        return boardId;
    }

    public String getBoardName() {

        switch (boardId) {
            case 200:
                return "해외";
            case 300:
                return "국내";
            case 400:
                return "일반";
            case 500:
                return "클럽";
            case 600:
                return "플레이어";
            case 700:
                return "건의/개선";
            default:
                return "";

        }
    }

    public int getLikers() {
        return likers;
    }

    /*public String getTags() {
        return tags;
    }*/

    public UserActionModel getUserAction() {
        return userAction;
    }

    public boolean isLiked() {

        if ( userAction != null ) {
            return userAction.isLike();
        }

        return false;
    }

    public boolean isScraped() {
        if ( userAction != null )
            return userAction.isScrap();

        return false;
    }

    public void setScraps(int scraps) {
        this.scraps = scraps;
    }

    public void setScraped(boolean isScraped) {

        if ( userAction != null )
            userAction.setScrap(isScraped == true ? 1 : 0);
    }

    public void setLiked(boolean isLiked) {

        if ( userAction != null ) {
            userAction.setLike(isLiked == true ? 1 : 0);
        }
    }

    public void setLikers(int likers) {
        this.likers = likers;
    }

    public void setDeleted(boolean isDeleted) {

        deleted = isDeleted == true ? 1 : 0;
    }

    public boolean isDeleted() {

        return deleted == 0 ? false : true;
    }

    /**
     * 생성시간
     *
     * @return
     */
    public String getCreatedString() {
        if ( created == null ) {
            created = Calendar.getInstance().getTime();
        }

        return DateUtils.getPrettTime(created);
    }

    public Date getCreated() {
        return created;
    }

    /**
     * 수정시간
     *
     * @return
     */
    public String getUpdatedString() {
        if ( updated == null ) {
            updated = Calendar.getInstance().getTime();
        }

        return DateUtils.getPrettTime(updated);
    }

    public Date getUpdated() {
        return updated;
    }

    /**
     * 사용자 정보
     *
     * @return
     */
    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public void setUserAction(UserActionModel userAction) {
        this.userAction = userAction;
    }

    /*--------------------------------------------------------------*/

    @Override
    public int getItemType() {
        return MultipleItem.CONTENT_RELATION;
    }

    @Override
    public boolean equals(Object inObject) {

        if (inObject instanceof ContentHeaderModel) {
            ContentHeaderModel inItem = (ContentHeaderModel) inObject;
            return this.content_id == inItem.getContentId();
        }
        return false;
    }
}

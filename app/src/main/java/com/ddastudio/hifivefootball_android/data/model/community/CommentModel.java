package com.ddastudio.hifivefootball_android.data.model.community;

import android.support.annotation.NonNull;

import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.UserActionModel;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.utils.DateUtils;
import com.google.gson.annotations.SerializedName;

import org.ocpsoft.prettytime.PrettyTime;
import org.parceler.Parcel;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by hongmac on 2017. 9. 6..
 */

@Parcel(analyze = {CommentModel.class})
public class CommentModel extends MultipleItem implements Comparable<CommentModel>
        /*extends AbstractFlexibleItem<CommentModel.CommentViewHolder>
        implements MultiItemEntit*/ {
    boolean visited = false;
    List<CommentModel> childeNode;

    public void addChild(List<CommentModel> nodes) {

        childeNode = nodes;
    }

    public List<CommentModel> getChildeNode() {
        return childeNode;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    @SerializedName("comment_id") int commentId;
    @SerializedName("parent_id") int parentId;
    @SerializedName("group_id") int groupId;
    @SerializedName("depth") int depth;
    @SerializedName("content_id") int contentId;
    @SerializedName("title") String title;
    @SerializedName("content") String content;
    //@SerializedName("liked") int liked;
    @SerializedName("likers") int likers;
    @SerializedName("deleted") int deleted;
    @SerializedName("reported") int reported;
    @SerializedName("created") Date created;
    @SerializedName("updated") Date updated;
    @SerializedName("user_action")
    UserActionModel userAction;
    @SerializedName("user")
    UserModel user;


    public CommentModel() {
        super(MultipleItem.CONTENT_COMMENT_LIST);
    }

    /*--------------------------------------------------------------*/


    public boolean isEdit() {

        if ( created.equals(updated)) {
            return false;
        }

        return true;
    }

    public int getCommentId() {
        return commentId;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getParentId() {
        return parentId;
    }

    public int getDepth() {
        return depth;
    }

    public int getContentId() {
        return contentId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {

        if ( isDeleted()) {
            return "삭제된 댓글입니다";
        }

        return content;
    }

    public boolean isLiked() {

        if ( userAction != null)
            return userAction.isLike();

        return false;
    }

    public void setLiked(int liked) {

        if ( userAction != null )
            userAction.setLike(liked);
    }

    public int getLikers() {
        return likers;
    }

    public void addLikers() {
        this.likers = this.likers + 1;
    }

    public void minLikers() {
        this.likers = this.likers - 1;
    }

    public void setLikers(int likers) {
        this.likers = likers;
    }

    public boolean isDeleted() {
        return deleted == 0 ? false : true;
    }

    public int getReported() {
        return reported;
    }

    public void setReported(int reported) {
        this.reported = reported;
    }

    public String getCreated() {
        if ( created == null ) {
            created = Calendar.getInstance().getTime();
        }

        return DateUtils.getPrettTime(created);
    }

    public String getUpdated() {
        if ( updated == null ) {
            updated = Calendar.getInstance().getTime();
        }

        return DateUtils.getPrettTime(updated);
    }

    public UserModel getUser() {
        return user;
    }

    /*--------------------------------------------------------------*/

    @Override
    public boolean equals(Object inObject) {
        if (inObject instanceof CommentModel) {
            CommentModel inItem = (CommentModel) inObject;
            return this.commentId == inItem.getCommentId();
        }
        return false;
    }

    @Override
    public int compareTo(@NonNull CommentModel commentModel) {

        int v = Integer.compare(depth, commentModel.depth);

        if ( v !=0 ) return v;

        v = Integer.compare(commentId, commentModel.commentId);

        if ( v !=0 ) return v;

        return Integer.compare(commentId, commentModel.commentId);
    }
}

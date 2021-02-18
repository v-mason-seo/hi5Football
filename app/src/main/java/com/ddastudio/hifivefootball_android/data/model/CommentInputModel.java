package com.ddastudio.hifivefootball_android.data.model;

/**
 * Created by hongmac on 2017. 9. 16..
 */

public class CommentInputModel extends MultipleItem {

    int parentCommentId;
    int parentGroupId;
    int depth;
    String content;


    public CommentInputModel(int commentId, int groupId, int depth, String content) {
        this.parentCommentId = commentId;
        this.parentGroupId = groupId == 0 ? commentId : groupId;
        this.depth = depth;
        this.content = content;
    }


    public int getParentCommentId() {
        return parentCommentId;
    }

    public int getParentGroupId() {
        return parentGroupId;
    }

    public int getDepth() {
        return depth;
    }

    public String getContent() {
        return content;
    }

    @Override
    public int getItemType() {
        //return super.getItemType();
        return MultipleItem.COMMENT_INPUT;
    }

    @Override
    public void setItemType(int itemType) {
        super.setItemType(itemType);
    }
}

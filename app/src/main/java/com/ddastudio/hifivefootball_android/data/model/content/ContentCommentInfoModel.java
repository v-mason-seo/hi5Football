package com.ddastudio.hifivefootball_android.data.model.content;

import com.ddastudio.hifivefootball_android.data.model.MultipleItem;

/**
 * Created by hongmac on 2017. 11. 7..
 */

public class ContentCommentInfoModel extends MultipleItem {

    int commentCount;

    public ContentCommentInfoModel(int commentCount) {
        this.commentCount = commentCount;
    }

    @Override
    public int getItemType() {
        return MultipleItem.CONTENT_COMMENT_INFO;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public String getCommentCountString() {

        return "댓글  " + commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}

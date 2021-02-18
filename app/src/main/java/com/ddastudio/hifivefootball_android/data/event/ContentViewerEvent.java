package com.ddastudio.hifivefootball_android.data.event;

import com.ddastudio.hifivefootball_android.data.model.CommentInputModel;

/**
 * Created by hongmac on 2017. 9. 16..
 */

public class ContentViewerEvent {

    public static class PostCommentEvent {

        int scrollPosition;
        CommentInputModel commentInputModel;

        public PostCommentEvent(CommentInputModel commentModel, int scrollPosition) {
            this.commentInputModel = commentModel;
            this.scrollPosition = scrollPosition;
        }

        public CommentInputModel getCommentModel() {
            return commentInputModel;
        }

        public int getScrollPosition() {
            return scrollPosition;
        }
    }
}

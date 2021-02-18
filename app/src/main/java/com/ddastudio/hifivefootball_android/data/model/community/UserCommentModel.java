package com.ddastudio.hifivefootball_android.data.model.community;

import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.data.model.community.CommentModel;
import com.google.gson.annotations.SerializedName;

import org.ocpsoft.prettytime.PrettyTime;
import org.parceler.Parcel;

import java.util.Date;

/**
 * Created by hongmac on 2017. 9. 22..
 */

@Parcel
public class UserCommentModel extends CommentModel {

    @SerializedName("content_user")
    UserModel contentUser;
    @SerializedName("content_created")
    Date contentCreated;
    @SerializedName("content_comments") int contentComments;
    @SerializedName("content_likers") int contentLikers;
    @SerializedName("content_scraps") int contentScraps;

    public String getContentUserName() {
        if ( contentUser != null )
            return contentUser.getUsername();

        return "";
    }

    public String getContentUserAvatarUrl() {
        if ( contentUser != null )
            return contentUser.getAvatarUrl();

        return "";
    }

    public String getContentUserProfile() {
        if (contentUser != null)
            return contentUser.getProfile();

        return "";
    }

    public int getContentComments() {
        return contentComments;
    }

    public int getContentLikers() {
        return contentLikers;
    }

    public int getContentScraps() {
        return contentScraps;
    }
}

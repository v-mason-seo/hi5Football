package com.ddastudio.hifivefootball_android.data.model;

import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.utils.DateUtils;
import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by hongmac on 2017. 9. 26..
 */

public class IssueModel extends MultipleItem {

    @SerializedName("issue_id") int issueId;
    @SerializedName("closed") Date closed;
    @SerializedName("progress") int progress;
    @SerializedName("platform") int platform;
    @SerializedName("tags") List<String> tags;
    @SerializedName("contents") ContentHeaderModel content;
    @SerializedName("user") UserModel user;
    @SerializedName("user_action") UserActionModel userAction;

    public IssueModel() {
        super(MultipleItem.ISSUE_TYPE_OPEN);
    }

    public ContentHeaderModel getContent() {

        if ( content != null ) {
            content.setUser(user);
            content.setUserAction(userAction);
        }

        return content;
    }

    public int getIssueId() {
        return issueId;
    }

    public String getClosedString() {
        if ( closed == null ) {
            closed = Calendar.getInstance().getTime();
        }

        return DateUtils.getPrettTime(closed);
    }

    public int getProgress() {
        return progress;
    }

    public String getUserName() {

        if ( user != null ) {
            return user.getUsername();
        }

        return "";
    }

    public String getNickNameNUserName() {

        if ( user != null ) {
            return user.getNickNameAndUserName();
        }

        return "";
    }

    public int getPlatform() {
        return platform;
    }

    public String getPlatformName() {

        switch (platform) {
            case 0:
                return "공통";
            case 1:
                return "Android";
            case 2:
                return "iOS";
            case 3:
                return "Web";
            default:
                return "";
        }
    }

    public String getAvatarUrl() {

        if ( user != null )
            return user.getAvatarUrl();

        return "";
    }

    public String getTitle() {

        if ( content != null )
            return content.getTitle();

        return "";
    }

    public List<String> getTags() {
        return tags;
    }

    public int getCommentCount() {
        if ( content != null )
            return content.getComments();

        return 0;
    }

    public int getLikeCount() {
        if ( content != null )
            return content.getLikers();

        return 0;
    }

    public int getScrapCount() {
        if ( content != null )
            return content.getScraps();

        return 0;
    }

    public String getCreated() {

        if ( content != null ) {
            return content.getCreatedString();
        }

        return "";
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

    public String getIssueInfo() {

        if ( closed != null ) {
            return "closed " + getClosedString() + "  |  " + getCommentString();
        }

        return "opended " + getCreated() + " by " + getNickNameNUserName() + ", " + getCommentString();

    }

    public String getCommentString() {
        if ( content != null ) {
            return "댓글 : " + content.getCommentString();
        }

        return "";
    }
}

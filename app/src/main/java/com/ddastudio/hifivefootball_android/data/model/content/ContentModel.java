package com.ddastudio.hifivefootball_android.data.model.content;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.PostBodyType;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.StreamViewerModel;
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
 * Created by hongmac on 2017. 9. 8..
 */

@Parcel
public class ContentModel extends MultipleItem {

    @SerializedName("content_id") int content_id;
    @SerializedName("title") String title;
    @SerializedName("arena_id") int arena_id;
    @SerializedName("board_id") int boardId;
    @SerializedName("bodytype") PostBodyType bodytype;
    @SerializedName("celltype") int cellType;
    //@SerializedName("bodytype") int bodytype;
    @SerializedName("content")
    @NonNull
    String content;
    @SerializedName("comments") int comments;
    @SerializedName("likers") int likers;
    @SerializedName("scraps") int scraps;
    //@SerializedName("tags") String tags;
    @SerializedName("tags") List<String> tags;
    @SerializedName("imgs") List<StreamViewerModel> imgs;
    @SerializedName("ip") String ip;
    @SerializedName("updated") Date updated;
    @SerializedName("created") Date created;
    @SerializedName("allow_comment") int allow_comment;
    //@SerializedName("liked") int liked;
    //@SerializedName("scraped") int scraped;
    @SerializedName("deleted") int deleted;
    @SerializedName("reported") int reported;
    @SerializedName("user_action")
    UserActionModel userAction;
    @SerializedName("user")
    UserModel user;

    public ContentModel() {
        super(MultipleItem.CONTENT_BODY);
    }

    public String getTitle() {
        return title;
    }

    public int getContentId() {
        return content_id;
    }

    public int getBoardId() {
        return boardId;
    }

    public void setBoardId(int boardId) {
        this.boardId = boardId;
    }

    public String getContent() {
        return content == null ? "" : content;
    }

    public List<StreamViewerModel> getImgs() {
        return imgs;
    }

    public String getIp() {
        return ip;
    }

    public String getUpdated() {

        if ( updated == null ) {
            updated = Calendar.getInstance().getTime();
        }

        return DateUtils.getPrettTime(updated);
    }

    public String getCreated() {
        if ( created == null ) {
            created = Calendar.getInstance().getTime();
        }

        return DateUtils.getPrettTime(created);
    }

    public boolean isAllowComment() {
        return allow_comment == 0 ? false : true;
    }

//    public int getBodytype() {
//        return bodytype;
//    }
//
//    public void setBodytype(int bodytype) {
//        this.bodytype = bodytype;
//    }


    public PostBodyType getBodytype() {
        return bodytype;
    }

    public void setBodytype(PostBodyType bodytype) {
        this.bodytype = bodytype;
    }

    public int getCellType() {
        return cellType;
    }

    public void setCellType(int cellType) {
        this.cellType = cellType;
    }

    public boolean isScraped() {
        if ( userAction != null )
            return userAction.isScrap();

        return false;
    }

    public void setScraped(int scraped) {
        if ( userAction != null )
            userAction.setScrap(scraped);
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getLikers() {
        return likers;
    }

    public void addLikes() {
        likers = likers + 1;
    }

    public void minLikers() {

        if ( likers > 0 ) {
            likers = likers - 1;
        }
    }

    public int getScraps() {
        return scraps;
    }

    public void addScrap() {
        scraps = scraps + 1;
    }

    public void minScrap() {

        if ( scraps > 0 ) {
            scraps = scraps - 1;
        }
    }

//    public String getTags() {
//
//        if ( tags == null )
//            return "";
//
//        return tags;
//    }


    public List<String> getTags() {
        return tags;
    }

    public boolean isLiked() {
        if ( userAction != null )
            return userAction.isLike();

        return false;
    }

    public void setContent_id(int content_id) {
        this.content_id = content_id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public void setLiked(int liked) {
        if ( userAction != null )
            userAction.setLike(liked);
    }

    public boolean isDeleted() {
        return deleted  == 0 ? false : true;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public boolean isReported() {
        return reported == 0 ? false : true;
    }

    public void setReported(int reported) {
        this.reported = reported;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public UserModel getUser() {
        return user;
    }

    public String getUserName() {

        return user != null ? user.getUsername() : "";
    }

    /*---------------------------------------------------------------------------------------------*/

    public String getWrapperHtml(Context context) {

        String htmlCode;

        Float textSize_f;
        Float lineSpacing_f;
        String webview_backgound_color;
        String webView_text_color;
        String webView_link_color;

        textSize_f = 16.0f;
        lineSpacing_f = 1.5f;
        webView_text_color= toHtmlColor(context, R.color.md_black_1000);
        webView_link_color = toHtmlColor(context, R.color.colorPrimary);
        webview_backgound_color = toHtmlColor(context, R.color.md_grey_50);

        htmlCode = context.getString(R.string.html
                , content
                , webView_text_color
                , webView_link_color
                , textSize_f
                , 8.5f
                , lineSpacing_f
                , webview_backgound_color);

        return htmlCode;
    }

    private String toHtmlColor(Context context, int colorid) {

        String FORMAT_HTML_COLOR = "%06X";
        return String.format(FORMAT_HTML_COLOR, 0xFFFFFF &
                ContextCompat.getColor(context, colorid));
    }
}

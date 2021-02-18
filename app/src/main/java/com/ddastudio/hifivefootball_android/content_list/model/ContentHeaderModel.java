package com.ddastudio.hifivefootball_android.content_list.model;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.PostBoardType;
import com.ddastudio.hifivefootball_android.common.PostCellType;
import com.ddastudio.hifivefootball_android.common.PostType;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.model.StreamViewerModel;
import com.ddastudio.hifivefootball_android.data.model.UserActionModel;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;
import com.ddastudio.hifivefootball_android.utils.DateUtils;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by hongmac on 2017. 9. 4..
 */


//@Parcel(analyze = {ContentHeaderModel.class, MatchModel.class})
@Parcel
public class ContentHeaderModel implements MultiItemEntity {

    Integer itemType;
    PostType postType;

    @SerializedName("content_id") int content_id;
    @SerializedName("title") String title;
    @SerializedName("content") String content;
    @SerializedName("arena_id") int arena_id;
    @SerializedName("match") MatchModel match;
    @SerializedName("player") PlayerModel player;
    @SerializedName("team") TeamModel team;
    @SerializedName("deleted") int deleted;
    @SerializedName("rating") float rating;
    @SerializedName("time_seq") String timeSeq;
    @SerializedName("reported") int reported;
    @SerializedName("comments") int comments;
    @SerializedName("scraps") int scraps;
    @SerializedName("preview") String preview;
    @SerializedName("likers") int likers;
    @SerializedName("board_id") int boardId;
    @SerializedName("bodytype") int bodytype;
    @SerializedName("celltype") int cellType;
    @SerializedName("link") String link;
    @SerializedName("tags") List<String> tags;
    @SerializedName("imgs") List<StreamViewerModel> imgs;
    @SerializedName("created") Date created;
    @SerializedName("updated") Date updated;
    @SerializedName("user_action") UserActionModel userAction;
    @SerializedName("user") UserModel user;

    /*--------------------------------------------------------------*/

    public ContentHeaderModel() {
        //itemType = ViewType.CONTENT_GENERAL;
        postType = PostType.BOARD_ALL;
    }

    @Override
    public int getItemType() {

        // itemType을 강제로 지정했다면 아래 로직을 무시한다.
        if ( itemType != null ) {
            return itemType;
        }

        /*----------------------------------------*/

        // 전체게시판
        if ( postType == PostType.BOARD_ALL) {

            switch (PostCellType.toEnum(cellType)) {
                case BASE:
                    itemType = ViewType.CONTENT_GENERAL;
                    break;
                case MATCH_CHAT:
                    itemType =  ViewType.CONTENT_ARENA;
                    break;
                case MATCH_REPORT:
                    itemType = ViewType.CONTENT_MATCH_REPORT_SMALL;
                    break;
                case PREVIEW:
                    //itemType = ViewType.CONTENT_PREVIEW;
                    // 전체게시판에서는 PREVIEW 셀타입은 SMALL_PREVIEW 으로 변경함.
                    itemType = ViewType.CONTENT_SMALL_PREVIEW;
                    break;
                case SMALL_PREVIEW:
                    itemType = ViewType.CONTENT_SMALL_PREVIEW;
                    break;
                case TEAM_TALK:
                    itemType = ViewType.CONTENT_TEAM_TALK;
                    break;
                case PLAYER_TALK:
                    itemType = ViewType.CONTENT_PLAYER_TALK;
                    break;

            }

        // 게시판을 직접 선택
        } else if ( postType == PostType.BOARD_SINGLE ) {

            switch (PostCellType.toEnum(cellType)) {
                case BASE:
                    itemType = ViewType.CONTENT_GENERAL;
                    break;
                case MATCH_CHAT:
                    //itemType = ViewType.CONTENT_ARENA;
                    itemType = ViewType.CONTENT_GENERAL;
                    break;
                case MATCH_REPORT:
                    itemType = ViewType.CONTENT_MATCH_REPORT;
                    break;
                case PREVIEW:
                    itemType = ViewType.CONTENT_PREVIEW;
                    break;
                case SMALL_PREVIEW:
                    itemType = ViewType.CONTENT_SMALL_PREVIEW;
                    break;
                case TEAM_TALK:
                    itemType = ViewType.CONTENT_TEAM_TALK;
                    break;
                case PLAYER_TALK:
                    itemType = ViewType.CONTENT_PLAYER_TALK;
                    break;
            }
        } else if ( postType == PostType.PLAYER_RELATION ) {
            switch (PostCellType.toEnum(cellType)) {
                case SMALL_PREVIEW:
                    itemType = ViewType.CONTENT_SMALL_PREVIEW;
                    break;
            }
        } else if ( postType == PostType.TEAM_RELATION ) {
            switch (PostCellType.toEnum(cellType)) {
                case SMALL_PREVIEW:
                    itemType = ViewType.CONTENT_SMALL_PREVIEW;
                    break;
            }
        }

        /*----------------------------------------*/

        if ( itemType == null ) {
            itemType = ViewType.CONTENT_GENERAL;
        }


        // 임시로직
        itemType = ViewType.CONTENT_GENERAL;

        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public PostType getPostType() {
        return postType;
    }

    public void setPostType(PostType postType) {
        this.postType = postType;
    }

    /*--------------------------------------------------------------*/

    /**
     * 게시글 id
     *
     * @return
     */
    public int getContentId() {
        return content_id;
    }

    public void setContentId(int content_id) {
        this.content_id = content_id;
    }

    /**
     * 타이틀
     *
     * @return
     */
    public String getTitle() {
        return title == null ? "" : title;
    }

    public void setContent_id(int content_id) {
        this.content_id = content_id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getContent() {
        return TextUtils.isEmpty(content) ? "" : content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 아레나 id
     *
     * @return
     */
    public int getArenaId() {
        //return arena_id;

        if ( match != null ) {

            return match.getMatchId();
        }

        return 0;
    }

    public MatchModel getMatch() {
        return match;
    }

    public String getCompetitionImage() {

        if ( getMatch() != null ) {
            return getMatch().getCompetitionImage();
        }

        return "X";
    }

    public String getCompetitionName() {
        if ( getMatch() != null ) {
            return getMatch().getCompetitionName();
        }

        return "";
    }

    public String getWeek() {

        if ( getMatch() != null ) {
            return getMatch().getWeek();
        }

        return "";
    }

    public PlayerModel getPlayer() {
        return player;
    }

    public int getPlayerId() {

        if ( player != null ) {
            return player.getPlayerId();
        }

        return 0;
    }

    public String getPlayerName() {

        if ( player != null ) {
            return player.getPlayerName();
        }

        return "";
    }

    public String getPlayerAvatar() {
        if ( player != null ) {
            return player.getPlayerLargeImageUrl();
        }

        return "";
    }

    public TeamModel getTeam() {
        return team;
    }

    public String getTeamEmblemUrl() {

        if ( team != null ) {
            return team.getEmblemUrl();
        }

        return "";
    }

    public int getHomeTeamId() {

        if ( match != null ) {

            return match.getHomeTeamId();
        }

        return -1;
    }

    public String getHomeTeamName() {

        if ( match != null ) {
            return match.getHomeTeamName();
        }

        return "";
    }

    public String getHomeTeamEmblemUrl() {

        if ( match != null ) {
            return match.getHomeTeamEmblemUrl();
        }

        return "";
    }

    public String getHomeTeamSmallEmblemUrl() {

        if ( match != null ) {
            return match.getHomeTeamEmblemUrl();
        }

        return "";
    }

    public int getAwayTeamId() {

        if ( match != null ) {
            return match.getAwayTeamId();
        }

        return -1;
    }

    public String getAwayTeamName() {

        if ( match != null ) {
            return match.getAwayTeamName();
        }

        return "";
    }

    public String getAwayTeamEmblemUrl() {

        if ( match != null ) {
            return match.getAwayTeamEmblemUrl();
        }

        return "";
    }

    public String getAwayTeamLargeEmblemUrl() {

        if ( match != null ) {
            return match.getAwayTeamLargeEmblemUrl();
        }

        return "";
    }

    public String getAwayTeamSmallEmblemUrl() {

        if ( match != null ) {
            return match.getAwayTeamSmallEmblemUrl();
        }

        return "";
    }

    /**
     * 아레나 id 입력
     *
     * @param arena_id
     */
    public void setArenaId(int arena_id) {
        this.arena_id = arena_id;
    }

    public float getRating() {
        return rating;
    }

    public String getPlayerRatingString() {

        return String.format("%.1f", rating);
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getTimeSeq() {
        return timeSeq;
    }

    public void setTimeSeq(String timeSeq) {
        this.timeSeq = timeSeq;
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

    public void addScrap() {
        scraps = scraps + 1;
    }

    public void minScrap() {

        if ( scraps > 0 ) {
            scraps = scraps - 1;
        }
    }

    public int getComments() {
        return comments;
    }

    public String getCommentString() {

        return String.valueOf(comments);
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public String getPreview() {
        return preview;
    }

    public int getBoardId() {
        return boardId;
    }

    /**
     * 0: HTML     EntityType.EDITOR_TYPE_HTML
     * 1: Plain    EntityType.EDITOR_TYPE_PLAIN
     * 2: MarkDown EntityType.EDITOR_TYPE_MARKDOWN
     * @return
     */
    public int getBodytype() {
        return bodytype;
    }

    public void setBodytype(int bodytype) {
        this.bodytype = bodytype;
    }

    public String getBoardName() {

        return PostBoardType.toEnum(boardId).getShortName();
        //App.getHifiveSettingsManager().getBoardName()
        //return App.getHifiveSettingsManager().getBoardShortName(boardId);

//        switch (boardId) {
//            case 200:
//                return "해외";
//            case 300:
//                return "국내";
//            case 400:
//                return "일반";
//            case 500:
//                return "클럽";
//            case 600:
//                return "플레이어";
//            case 700:
//                return "건의/개선";
//            default:
//                return App.getHifiveSettingsManager().getBoardShortName(boardId);
//                //return "";
//
//        }
    }

    public int getCellType() {
        return cellType;
    }

    public PostCellType getCellTypeEnum() {
        return PostCellType.toEnum(cellType);
    }

    public List<StreamViewerModel> getImgs() {
        return imgs;
    }

    public String getLink() {
        return link;
    }

    public int getLikers() {
        return likers;
    }

    public String getLikerString() {
        return String.valueOf(likers);
    }

    /*public String getTags() {
        return tags;
    }*/

    public List<String> getTags() {
        return tags;
    }

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
    public String getScrapString() {
        return String.valueOf(scraps);
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

    public void setLiked(int liked) {
        if ( userAction != null )
            userAction.setLike(liked);
    }

    public void addLikes() {
        likers = likers + 1;
    }

    public void minLikers() {

        if ( likers > 0 ) {
            likers = likers - 1;
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

    public String getNickNameAndUserName() {

        return user == null ? "" : String.format("%s(%s)", user.getNickname(), user.getUsername());
    }

    public String getNickName() {

        return user == null ? "" : user.getNickname();
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public void setUserAction(UserActionModel userAction) {
        this.userAction = userAction;
    }

    public String getUserName() {

        return user != null ? user.getUsername() : "";
    }

    /*--------------------------------------------------------------*/

    @Override
    public boolean equals(Object inObject) {

        if (inObject instanceof ContentHeaderModel) {
            ContentHeaderModel inItem = (ContentHeaderModel) inObject;
            return this.content_id == inItem.getContentId();
        }
        return false;
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
package com.ddastudio.hifivefootball_android.content_list;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.content_list.widget.BadgedImageView;
import com.ddastudio.hifivefootball_android.data.model.StreamViewerModel;
import com.ddastudio.hifivefootball_android.content_list.model.BestContentListHeaderModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.ui.utils.ColorUtils;
import com.ddastudio.hifivefootball_android.ui.utils.picasso.PicassoCircleTransformation;
import com.ddastudio.hifivefootball_android.ui.utils.picasso.PicassoImageGetter;
import com.ddastudio.hifivefootball_android.utils.ColorGenerator;
import com.ddastudio.hifivefootball_android.utils.CommonUtils;
import com.ddastudio.hifivefootball_android.utils.FileUtil;
import com.google.android.flexbox.FlexboxLayout;
import com.squareup.picasso.Picasso;

import java.util.List;

import cn.nekocode.badge.BadgeDrawable;
import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import timber.log.Timber;

/**
 * Created by hongmac on 2017. 9. 21..
 */

public class ContentListRvAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    ChipCloudConfig config;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ContentListRvAdapter(List<MultiItemEntity> data) {
        super(data);

        // 1. 게시글 디폴트 - 자유게시판, 축구게시판 일반글
        //addItemType(ViewType.CONTENT_GENERAL, R.layout.row_content_list_item2);
        addItemType(ViewType.CONTENT_GENERAL, R.layout.row_content_default_card_item);

        // 2. 팀 글
        //addItemType(ViewType.CONTENT_TEAM_TALK, R.layout.row_content_team_talk_item);
        //addItemType(ViewType.CONTENT_TEAM_TALK, R.layout.row_content_team_card_item);
        addItemType(ViewType.CONTENT_TEAM_TALK, R.layout.row_content_default_card_item);

        // 3. 플레이어 글
        //addItemType(ViewType.CONTENT_PLAYER_TALK, R.layout.row_content_player_talk_item);
        //addItemType(ViewType.CONTENT_PLAYER_TALK, R.layout.row_content_player_card_item);
        addItemType(ViewType.CONTENT_PLAYER_TALK, R.layout.row_content_default_card_item);

        // 4.매치 글
        //addItemType(ViewType.CONTENT_ARENA, R.layout.row_content_list_item2);
        //addItemType(ViewType.CONTENT_ARENA, R.layout.row_content_match_card_item);
        addItemType(ViewType.CONTENT_ARENA, R.layout.row_content_default_card_item);

        // 5. 뉴스
        addItemType(ViewType.CONTENT_PREVIEW, R.layout.row_content_default_card_item);
        addItemType(ViewType.CONTENT_SMALL_PREVIEW, R.layout.row_content_default_card_item);

        // 6. 매치리포트
        addItemType(ViewType.CONTENT_MATCH_REPORT, R.layout.row_content_match_card_item);
        addItemType(ViewType.CONTENT_MATCH_REPORT_SMALL, R.layout.row_content_match_card_item);

        //addItemType(ViewType.CONTENT_MATCH_REPORT, R.layout.row_content_match_report);
        //addItemType(ViewType.CONTENT_MATCH_REPORT_SMALL, R.layout.row_content_match_report);
        //addItemType(ViewType.CONTENT_MATCH_REPORT_SMALL, R.layout.row_content_player_rating_list_item);

        //addItemType(ViewType.CONTENT_ARENA, R.layout.row_content_arena_chat_list_item);
//        addItemType(ViewType.CONTENT_MATCH_REPORT, R.layout.row_content_match_report);
//        addItemType(ViewType.CONTENT_MATCH_REPORT_SMALL, R.layout.row_content_player_rating_list_item);
        addItemType(ViewType.CONTENT_SMALL_PREVIEW2, R.layout.row_content_small_preview_item2);



        addItemType(ViewType.CONTENT_BEST_DATE_HEADER, R.layout.row_best_content_date_row);

        // 2018.04.10 게시글과 비슷한 형태로 변경한다.
        addItemType(ViewType.CONTENT_BEST_HIFIVE, R.layout.row_best_content_list_item);
        addItemType(ViewType.CONTENT_BEST_COMMENT, R.layout.row_best_content_list_item);
        // 2018.04.10 아래버전은 옛날방식이다. 새로운 레이아웃으로 변경한다..
//        addItemType(ViewType.CONTENT_BEST_HIFIVE, R.layout.row_best_contens_header_item);
//        addItemType(ViewType.CONTENT_BEST_COMMENT, R.layout.row_best_contens_header_item);

        config = new ChipCloudConfig()
                .selectMode(ChipCloud.SelectMode.none)
                .checkedChipColor(Color.parseColor("#ddaa00"))
                .checkedTextColor(Color.parseColor("#ffffff"))
                .uncheckedChipColor(Color.parseColor("#efefef"))
                .uncheckedTextColor(Color.parseColor("#666666"))
                .useInsetPadding(true)
        ;

    }

    private SpannableString getBoardNameBadge(String boardName) {

        final BadgeDrawable drawableBoardTag =
                new BadgeDrawable.Builder()
                        .type(BadgeDrawable.TYPE_ONLY_ONE_TEXT)
                        .badgeColor(ColorGenerator.MATERIAL.getColor(boardName))
                        .textSize(CommonUtils.sp2px(mContext, 9))
                        .text1(boardName)
                        .build();

        SpannableString spannableString = new SpannableString(drawableBoardTag.toSpannable());

        return spannableString;
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        switch (helper.getItemViewType()) {

            // 일반게시글
            case ViewType.CONTENT_GENERAL:
                bindDefaultCardContent(helper, (ContentHeaderModel)item);
                //bindGeneralContent(helper, (ContentHeaderModel)item);
                break;

            // 아레나 글
            case ViewType.CONTENT_ARENA:
                bindDefaultCardContent(helper, (ContentHeaderModel)item);
                //bindMatchTalkCardContent(helper, (ContentHeaderModel)item);
                //bindArenaContent(helper, (ContentHeaderModel)item);
                break;

            // 플레이어 글
            case ViewType.CONTENT_PLAYER_TALK:
                bindDefaultCardContent(helper, (ContentHeaderModel)item);
                //bindPlayerTalkCardContent(helper, (ContentHeaderModel)item);
                //bindPlayerTalkContent(helper, (ContentHeaderModel)item);
                break;

            // 팀 글
            case ViewType.CONTENT_TEAM_TALK:
                bindDefaultCardContent(helper, (ContentHeaderModel)item);
                //bindTeamTalkCardContent(helper, (ContentHeaderModel)item);
                //bindTeamTalkContent(helper, (ContentHeaderModel)item);
                break;

            // 매치리포트 - 1
            case ViewType.CONTENT_MATCH_REPORT:
                bindMatchTalkCardContent(helper, (ContentHeaderModel)item);
                //bindMatchReport(helper, (ContentHeaderModel)item);
                break;

            // 매치리포트 - 2
            case ViewType.CONTENT_MATCH_REPORT_SMALL:
                bindMatchTalkCardContent(helper, (ContentHeaderModel)item);
                //bindMatchReport(helper, (ContentHeaderModel)item);
                //bindPlayerRatingContent(helper, (ContentHeaderModel)item);
                break;

            // 뉴스
            case ViewType.CONTENT_SMALL_PREVIEW:
                //bindFootballNews(helper, (ContentHeaderModel)item);
                bindDefaultCardContent(helper, (ContentHeaderModel)item);
                break;

            //
            case ViewType.CONTENT_SMALL_PREVIEW2:
                bindFootballNews2(helper, (ContentHeaderModel)item);
                break;

            //

            case ViewType.CONTENT_BEST_DATE_HEADER:
                bindBestDateHeader(helper, (BestContentListHeaderModel)item);
                break;
            case ViewType.CONTENT_BEST_HIFIVE:
                bindBestContent3(helper, (ContentHeaderModel)item);
                break;
            case ViewType.CONTENT_BEST_COMMENT:
                // 임시로 주석처리 - 베스트 글 집계를 임시적으로 간단하게 처리한다..
                //bindBestContent(helper, (BestContentsHeaderModel)item);
                //bindBestContent(helper, (ContentHeaderModel)item);
                bindBestContent2(helper, (ContentHeaderModel)item);
                break;

        }
    }


    /*------------------------------------------------------------------------*/

    private boolean displayImageByPicasso(String url, ImageView target, int placeholder_id) {

        if ( TextUtils.isEmpty(url)) {
            target.setImageResource(R.drawable.ic_face);
            return false;
        }

        Picasso.get()
                .load(url)
                .placeholder(placeholder_id)
                .transform(new PicassoCircleTransformation())
                .fit()
                .centerInside()
                .into(target);

        return true;
    }

    private void bindDefaultCardContent(BaseViewHolder helper, ContentHeaderModel content) {

        helper.addOnClickListener(R.id.iv_comment)
                .addOnClickListener(R.id.iv_hifive)
                .addOnClickListener(R.id.iv_scraped)
                .addOnClickListener(R.id.iv_avatar)
                .addOnClickListener(R.id.include_player)
                .addOnClickListener(R.id.include_team);

        helper.setText(R.id.tv_nickname, content.getNickNameAndUserName())
                .setText(R.id.tv_updated, content.getUpdatedString())
                .setText(R.id.tv_title, content.getTitle())
                .setText(R.id.tv_hifive_count, content.getLikerString())
                .setText(R.id.tv_comment_count, content.getCommentString())
                .setText(R.id.tv_preview, content.getPreview())
                .setGone(R.id.tv_preview, !TextUtils.isEmpty(content.getPreview()))
                .setGone(R.id.include_match, content.getArenaId() != 0)
                .setGone(R.id.include_team, content.getTeam() != null)
                .setGone(R.id.include_player, content.getPlayer() != null)
                .setGone(R.id.v_divider, !(content.getArenaId() !=0 || content.getTeam() != null || content.getPlayer() != null))
                .setImageResource(R.id.iv_scraped, content.isScraped() ? R.drawable.ic_scrap_vector : R.drawable.ic_scrap_border)
        ;

        displayImageByPicasso(content.getUser().getAvatarUrl(),
                helper.getView(R.id.iv_avatar),
                R.drawable.ic_face);

        if ( content.getImgs() != null
                && content.getImgs().size() > 0
                /*&& content.getImgs().get(0).getStreamType() == StreamViewerModel.IMAGE*/ ) {

            // 확장자 조건은 필요없은 코드이나 예전 데이터가 잘 못 들어가 있으므로 추가함.
            // 1. 데이터 정리 후 아래 코드는 삭제하자.
            // 2. 글리이드 버전업 하면 mp4도 이미지가 나오니 글라이드 버전업 후 아래 코드 삭제하자.
            String extension = FileUtil.getExtension(content.getImgs().get(0).getStreamUrl());

            // MP4, JPG 이미지 타입 뱃지 달기
            if ( !TextUtils.isEmpty(extension)) {
                BadgedImageView ivBadgedView = helper.getView(R.id.iv_image);
                ivBadgedView.setBadge(extension.replace(".", "").toUpperCase());
            }

            helper.setGone(R.id.iv_image, true);
            GlideApp.with(mContext)
                    .load(content.getImgs().get(0).getStreamUrl())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_empty_dracula)
                    .into((ImageView)helper.getView(R.id.iv_image));

//            if ( !extension.equals(".mp4")) {
//                helper.setGone(R.id.iv_image, true);
//                GlideApp.with(mContext)
//                        .load(content.getImgs().get(0).getStreamUrl())
//                        .centerCrop()
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .placeholder(R.drawable.ic_empty_dracula)
//                        .into((ImageView)helper.getView(R.id.iv_image));
//            } else {
//                helper.setGone(R.id.iv_image, false);
//            }

        } else {
            helper.setGone(R.id.iv_image, false);
        }

        //
        // 2. 매치정보
        //
        if ( content.getMatch() != null ) {

            helper.setText(R.id.tv_competition_name, content.getCompetitionName() + " - " + content.getWeek())
                    .setText(R.id.tv_match_date, content.getMatch().getMatchDate4())
                    .setText(R.id.tv_home_team_name, content.getMatch().getHomeTeamName())
                    .setText(R.id.tv_home_team_score, content.getMatch().getHomeTeamScoreString())
                    .setText(R.id.tv_away_team_name, content.getMatch().getAwayTeamName())
                    .setText(R.id.tv_away_team_score, content.getMatch().getAwayTeamScoreString());

            // 컴피티션
            displayImageByPicasso(content.getCompetitionImage(),
                    helper.getView(R.id.iv_competition),
                    R.drawable.ic_circle_grey_vector);

            // 홈팀 엠블럼
            Picasso.get()
                    .load(content.getHomeTeamEmblemUrl())
                    .placeholder(R.drawable.ic_empty_emblem_vector_1)
                    .fit()
                    .centerInside()
                    .into((ImageView)helper.getView(R.id.iv_home_emblem));

            // 어웨이팀 엠블럼
            Picasso.get()
                    .load(content.getAwayTeamEmblemUrl())
                    .placeholder(R.drawable.ic_empty_emblem_vector_1)
                    .fit()
                    .centerInside()
                    .into((ImageView)helper.getView(R.id.iv_away_emblem));
        }

        //
        // 2. 선수 정보
        //
        if ( content.getPlayer() != null ) {
            helper.setText(R.id.tv_player_name, content.getPlayerName())
                    .setText(R.id.tv_player_team_name, content.getPlayer().getTeamName());

            displayImageByPicasso(content.getPlayerAvatar(),
                    helper.getView(R.id.iv_player_avatar),
                    R.drawable.ic_person_grey_vector);
        }

        //
        // 2. 팀 정보
        //
        if ( content.getTeam() != null ) {
            helper.setText(R.id.tv_team_name, content.getTeam().getTeamName());

            displayImageByPicasso(content.getTeamEmblemUrl(),
                    helper.getView(R.id.iv_emblem),
                    R.drawable.ic_empty_emblem_vector_1);
        }

    }

    /**
     * 게시글 데이터 바인딩
     * @param helper
     * @param content
     */
    private void bindGeneralContent(BaseViewHolder helper, ContentHeaderModel content) {

        helper.addOnClickListener(R.id.iv_liked)
                .addOnClickListener(R.id.like_count)
                .addOnClickListener(R.id.iv_scraped)
                .addOnClickListener(R.id.comment_icon)
                .addOnClickListener(R.id.comment_count)
                .addOnClickListener(R.id.comment_box)
                .addOnClickListener(R.id.fl_profile_box)
        ;

        helper.setText(R.id.contents_title, content.getTitle())
                .setText(R.id.comment_count, String.valueOf(content.getComments()))
                .setText(R.id.tv_detail_info, getContentInfoSpannable(content))
        ;

        GlideApp.with(mContext)
                .load(content.getUser().getAvatarUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_face)
                .into((ImageView)helper.getView(R.id.avatar));
    }

    /**
     * 유저, 작성일자, 하이파이브, 스크랩 정보를 스패너블 현태로 리턴한다.
     * @param content
     * @return
     */
    private Spannable getContentInfoSpannable(ContentHeaderModel content) {

        int startIndex;
        int endIndex;
        SpannableStringBuilder ssb = new SpannableStringBuilder();

        // 1. 닉네임
        startIndex = ssb.length();
        ssb.append(content.getNickName());
        endIndex = ssb.length();
        ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.md_blue_grey_500)), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 2. 작성일자
        startIndex = ssb.length();
        ssb.append("  ").append(content.getUpdatedString());
        endIndex = ssb.length();
        ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.md_grey_500)), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 3. 하이파이브
        if ( content.getLikers() > 0) {
            ssb.append("  ").append(String.valueOf(content.getLikers())).append(" 하이파이브");
        }

        // 4. 스크랩
        if ( content.isScraped() ) {
            ssb.append("  " + "스크랩됨");
        }

        return ssb;
    }

    /*------------------------------------------------------------------------*/

    private void bindMatchTalkCardContent(BaseViewHolder helper, ContentHeaderModel content) {

        helper.addOnClickListener(R.id.iv_comment)
                .addOnClickListener(R.id.iv_hifive)
                .addOnClickListener(R.id.iv_scraped)
                .addOnClickListener(R.id.iv_avatar)
                .addOnClickListener(R.id.include_match)
                .addOnClickListener(R.id.iv_home_emblem)
                .addOnClickListener(R.id.iv_away_emblem)
        ;

        //
        // 1. 게시글 정보
        //
        helper.setText(R.id.tv_nickname, content.getNickNameAndUserName())
                .setText(R.id.tv_updated, content.getUpdatedString())
                .setText(R.id.tv_title, content.getTitle())
                .setText(R.id.tv_hifive_count, content.getLikerString())
                .setText(R.id.tv_comment_count, content.getCommentString())
                .setText(R.id.tv_preview, content.getPreview())
                .setGone(R.id.tv_preview, !TextUtils.isEmpty(content.getPreview()));

        Picasso.get()
                .load(content.getUser().getAvatarUrl())
                .placeholder(R.drawable.ic_face)
                .transform(new PicassoCircleTransformation())
                .fit()
                .centerInside()
                .into((ImageView)helper.getView(R.id.iv_avatar));

        if ( content.getImgs() != null && content.getImgs().size() > 0) {
            Picasso.get()
                    .load(content.getImgs().get(0).getStreamUrl())
                    .placeholder(R.drawable.ic_empty_dracula)
                    .fit()
                    .centerInside()
                    .into((ImageView)helper.getView(R.id.iv_image));
        } else {
            helper.setGone(R.id.iv_image, false);
        }


        //
        // 2. 매치정보
        //
        if ( content.getMatch() != null ) {

            helper.setText(R.id.tv_competition_name, content.getCompetitionName() + " - " + content.getWeek())
                    .setText(R.id.tv_match_date, content.getMatch().getMatchDate4())
                    .setText(R.id.tv_home_team_name, content.getMatch().getHomeTeamName())
                    .setText(R.id.tv_home_team_score, content.getMatch().getHomeTeamScoreString())
                    .setText(R.id.tv_away_team_name, content.getMatch().getAwayTeamName())
                    .setText(R.id.tv_away_team_score, content.getMatch().getAwayTeamScoreString());

            // 컴피티션
            displayImageByPicasso(content.getCompetitionImage(),
                    helper.getView(R.id.iv_competition),
                    R.drawable.ic_circle_grey_vector);
//            Picasso.get()
//                    .load(content.getCompetitionImage())
//                    .placeholder(R.drawable.ic_circle_grey_vector)
//                    .fit()
//                    .centerInside()
//                    .into((ImageView)helper.getView(R.id.iv_competition));

            // 홈팀 엠블럼
            Picasso.get()
                    .load(content.getHomeTeamEmblemUrl())
                    .placeholder(R.drawable.ic_empty_emblem_vector_1)
                    .fit()
                    .centerInside()
                    .into((ImageView)helper.getView(R.id.iv_home_emblem));

            // 어웨이팀 엠블럼
            Picasso.get()
                    .load(content.getAwayTeamEmblemUrl())
                    .placeholder(R.drawable.ic_empty_emblem_vector_1)
                    .fit()
                    .centerInside()
                    .into((ImageView)helper.getView(R.id.iv_away_emblem));
        } else {
            helper.setGone(R.id.include_match, content.getMatch() != null);
        }

    }

    /**
     * 아레나 게스글 바인딩
     * @param helper
     * @param content
     */
    private void bindArenaContent(BaseViewHolder helper, ContentHeaderModel content) {

        helper.addOnClickListener(R.id.comment_box)
                .addOnClickListener(R.id.fl_profile_box)
        ;

        helper.setText(R.id.contents_title, getArenaTitleSpannable(helper.getView(R.id.contents_title), content))
                .setText(R.id.comment_count, String.valueOf(content.getComments()))
                .setText(R.id.tv_detail_info, getContentInfoSpannable(content))
        ;

        GlideApp.with(mContext)
                .load(content.getUser().getAvatarUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_face)
                .into((ImageView)helper.getView(R.id.avatar));

    }

    /**
     * 아레나 타이틀 스패너블을 리턴한다.
     *  - [ 홈팀 VS 어웨이팀 타이틀 ]
     * @param tvTitle
     * @param content
     * @return
     */
    private Spannable getArenaTitleSpannable(TextView tvTitle, ContentHeaderModel content) {

        int start, end;
        Spannable html;
        SpannableStringBuilder ssbTitle = new SpannableStringBuilder();
        PicassoImageGetter imageGetter = new PicassoImageGetter(tvTitle);

        String homeImgSrc = mContext.getResources().getString(R.string.img_src, content.getHomeTeamSmallEmblemUrl());
        String awayImgSrc = mContext.getResources().getString(R.string.img_src, content.getAwayTeamSmallEmblemUrl());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

            // 홈팀 이미지
            html = (Spannable) Html.fromHtml(homeImgSrc, Html.FROM_HTML_MODE_LEGACY, imageGetter, null);
            ssbTitle.append(html);

            // VS 텍스트 삽입
            start = ssbTitle.length();
            ssbTitle.append(" VS ");
            end = ssbTitle.length();
            ssbTitle.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.md_blue_grey_300)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssbTitle.setSpan(new RelativeSizeSpan(0.8f), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // 어웨이팀 이미지
            html = (Spannable) Html.fromHtml(awayImgSrc, Html.FROM_HTML_MODE_LEGACY, imageGetter, null);
            ssbTitle.append(html);

        } else {
            // 홈팀 이미지
            html = (Spannable) Html.fromHtml(homeImgSrc, imageGetter, null);
            ssbTitle.append(html);

            // VS 텍스트 삽입
            start = ssbTitle.length();
            ssbTitle.append(" VS ");
            end = ssbTitle.length();
            ssbTitle.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.md_blue_grey_300)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssbTitle.setSpan(new RelativeSizeSpan(0.8f), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // 어웨이팀 이미지
            html = (Spannable) Html.fromHtml(awayImgSrc, imageGetter, null);
            ssbTitle.append(html);
        }

        // 공백 추가
        ssbTitle.append("  ");

        // 게스글 타이틀 입력
        ssbTitle.append(content.getTitle());

        return ssbTitle;
    }

    /*------------------------------------------------------------------------*/


    private void bindPlayerTalkCardContent(BaseViewHolder helper, ContentHeaderModel content) {

        helper.addOnClickListener(R.id.iv_comment)
                .addOnClickListener(R.id.iv_hifive)
                .addOnClickListener(R.id.iv_scraped)
                .addOnClickListener(R.id.include_player);

        //
        // 1. 게시글 정보
        //
        helper.setText(R.id.tv_nickname, content.getNickNameAndUserName())
                .setText(R.id.tv_updated, content.getUpdatedString())
                .setText(R.id.tv_title, content.getTitle())
                .setText(R.id.tv_hifive_count, content.getLikerString())
                .setText(R.id.tv_comment_count, content.getCommentString())
                .setGone(R.id.tv_preview, !TextUtils.isEmpty(content.getPreview()))
                ;

        Picasso.get()
                .load(content.getUser().getAvatarUrl())
                .placeholder(R.drawable.ic_face)
                .transform(new PicassoCircleTransformation())
                .fit()
                .centerInside()
                .into((ImageView)helper.getView(R.id.iv_avatar));

        if ( content.getImgs() != null && content.getImgs().size() > 0) {
            Picasso.get()
                    .load(content.getImgs().get(0).getStreamUrl())
                    .placeholder(R.drawable.ic_empty_dracula)
                    .fit()
                    .centerCrop()
                    .into((ImageView)helper.getView(R.id.iv_image));
        } else {
            helper.setGone(R.id.iv_image, false);
        }

        //
        // 2. 선수 정보
        //
        if ( content.getPlayer() != null ) {
            helper.setText(R.id.tv_player_name, content.getPlayerName())
                    .setText(R.id.tv_team_name, content.getPlayer().getTeamName())
                    .setText(R.id.tv_preview, content.getPreview());

            displayImageByPicasso(content.getPlayerAvatar(),
                    helper.getView(R.id.iv_player_avatar),
                    R.drawable.ic_person_grey_vector);
        } else {

            helper.setGone(R.id.include_player, content.getPlayer() != null);
        }
    }

    /**
     * 플레이어 게시글 바인딩
     */
    private void bindPlayerTalkContent(BaseViewHolder helper, ContentHeaderModel content) {

        helper.addOnClickListener(R.id.iv_liked)
                .addOnClickListener(R.id.like_count)
                .addOnClickListener(R.id.iv_scraped)
                .addOnClickListener(R.id.comment_icon)
                .addOnClickListener(R.id.comment_count)
                .addOnClickListener(R.id.comment_box)
                .addOnClickListener(R.id.fl_profile_box)
        ;

        helper.setText(R.id.contents_title, getPlayerTalkTitleSpannable(helper.getView(R.id.contents_title), content))
                .setText(R.id.tv_detail_info, getContentInfoSpannable(content))
                .setText(R.id.comment_count, String.valueOf(content.getComments()))
        ;

        GlideApp.with(mContext)
                .load(content.getUser().getAvatarUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_face)
                .into((ImageView)helper.getView(R.id.avatar));

    }

    /**
     * 플레이어 게시글의 타이틀 스패너블을 리턴한다.
     */
    private Spannable getPlayerTalkTitleSpannable(TextView tvTitle, ContentHeaderModel content) {

        int start, end;
        Spannable html;
        SpannableStringBuilder ssbTitle = new SpannableStringBuilder();
        PicassoImageGetter imageGetter = new PicassoImageGetter(tvTitle);

        String playerAvatarImgSrc = mContext.getResources().getString(R.string.img_src, content.getPlayerAvatar());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            html = (Spannable) Html.fromHtml(playerAvatarImgSrc, Html.FROM_HTML_MODE_LEGACY, imageGetter, null);
        } else {
            html = (Spannable) Html.fromHtml(playerAvatarImgSrc, imageGetter, null);
        }

        // 이미지 추가
        ssbTitle.append(html);

        // 선수이름 추가
        start = ssbTitle.length();
        ssbTitle.append(" " + content.getPlayerName() + "   ");
        end = ssbTitle.length();
        ssbTitle.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.md_blue_grey_300)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssbTitle.setSpan(new RelativeSizeSpan(0.8f), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 게시글 제목 추가
        ssbTitle.append(content.getTitle());

        return ssbTitle;
    }


    /*------------------------------------------------------------------------*/

    private void bindTeamTalkCardContent(BaseViewHolder helper, ContentHeaderModel content) {

        helper.addOnClickListener(R.id.iv_comment)
                .addOnClickListener(R.id.iv_hifive)
                .addOnClickListener(R.id.iv_scraped)
                .addOnClickListener(R.id.iv_avatar)
                .addOnClickListener(R.id.include_team);

        //
        // 1. 게시글 정보
        //
        helper.setText(R.id.tv_nickname, content.getNickNameAndUserName())
                .setText(R.id.tv_updated, content.getUpdatedString())
                .setText(R.id.tv_title, content.getTitle())
                .setText(R.id.tv_hifive_count, content.getLikerString())
                .setText(R.id.tv_comment_count, content.getCommentString())
                .setText(R.id.tv_preview, content.getPreview())
                .setGone(R.id.tv_preview, !TextUtils.isEmpty(content.getPreview()))
        ;

        Picasso.get()
                .load(content.getUser().getAvatarUrl())
                .placeholder(R.drawable.ic_face)
                .fit()
                .centerInside()
                .into((ImageView)helper.getView(R.id.iv_avatar));

        if ( content.getImgs() != null && content.getImgs().size() > 0) {
            Picasso.get()
                    .load(content.getImgs().get(0).getStreamUrl())
                    .placeholder(R.drawable.ic_empty_dracula)
                    .fit()
                    .centerInside()
                    .into((ImageView)helper.getView(R.id.iv_image));
        } else {
            helper.setGone(R.id.iv_image, false);
        }

        //
        // 2. 팀 정보
        //
        if ( content.getTeam() != null ) {
            helper.setText(R.id.tv_team_name, content.getTeam().getTeamName());

            displayImageByPicasso(content.getTeamEmblemUrl(),
                    helper.getView(R.id.iv_emblem),
                    R.drawable.ic_empty_emblem_vector_1);
//            Picasso.get()
//                    .load(content.getTeamEmblemUrl())
//                    .placeholder(R.drawable.ic_empty_emblem_vector_1)
//                    .fit()
//                    .centerInside()
//                    .into((ImageView)helper.getView(R.id.iv_emblem));
        } else {
            helper.setGone(R.id.include_team, content.getTeam() != null);
        }
    }

    /**
     * 팀 게시글 바인딩
     */
    private void bindTeamTalkContent(BaseViewHolder helper, ContentHeaderModel content) {

        int start;
        int end;

        helper.addOnClickListener(R.id.iv_liked)
                .addOnClickListener(R.id.like_count)
                .addOnClickListener(R.id.iv_scraped)
                .addOnClickListener(R.id.comment_icon)
                .addOnClickListener(R.id.comment_count)
                .addOnClickListener(R.id.comment_box)
                .addOnClickListener(R.id.fl_profile_box)
        ;

        helper.setText(R.id.comment_count, String.valueOf(content.getComments()))
                .setText(R.id.contents_title, getTeamTalkTitleSpannable(helper.getView(R.id.contents_title), content))
                .setText(R.id.tv_detail_info, getContentInfoSpannable(content))
        ;

        GlideApp.with(mContext)
                .load(content.getUser().getAvatarUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_face)
                .into((ImageView)helper.getView(R.id.avatar));
    }

    /**
     * 팀 게시글 타이틀을(팀 이미지와 팀이름 그리고 제목) 스패너블형태로 반환한다.
     */
    private Spannable getTeamTalkTitleSpannable(TextView tvTitle, ContentHeaderModel content) {

        int start, end;
        Spannable html;
        SpannableStringBuilder ssbTitle = new SpannableStringBuilder();
        PicassoImageGetter imageGetter = new PicassoImageGetter(tvTitle);

        String teamImgSrc = mContext.getResources().getString(R.string.img_src, content.getTeamEmblemUrl());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            html = (Spannable) Html.fromHtml(teamImgSrc, Html.FROM_HTML_MODE_LEGACY, imageGetter, null);
        } else {
            html = (Spannable) Html.fromHtml(teamImgSrc, imageGetter, null);
        }

        // 이미지 추가
        ssbTitle.append(html);

        // 팀이름 추가
        start = ssbTitle.length();
        ssbTitle.append(" " + content.getTeam().getTeamName() + "   ");
        end = ssbTitle.length();
        ssbTitle.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.md_blue_grey_300)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssbTitle.setSpan(new RelativeSizeSpan(0.8f), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 게시글 제목 추가
        ssbTitle.append(content.getTitle());

        return ssbTitle;
    }


    /*------------------------------------------------------------------------*/


    /**
     * 매치리포트 게시글 바인딩
     */
    private void bindMatchReport(BaseViewHolder helper, ContentHeaderModel content) {

        helper.addOnClickListener(R.id.comment_box)
                .addOnClickListener(R.id.fl_profile_box)
        ;

        helper.setText(R.id.contents_title, content.getTitle())
                .setText(R.id.user_name, content.getNickName())
                .setText(R.id.reg_date, content.getCreatedString())
                .setText(R.id.like_count, /*"HiFive " + */String.valueOf(content.getLikers()))
                .setTextColor(R.id.like_count, ContextCompat.getColor(mContext, content.isLiked() ? R.color.md_indigo_300 : R.color.md_grey_500) )
                .setText(R.id.comment_count, String.valueOf(content.getComments()))
                .setImageResource(R.id.iv_liked, content.isLiked() ? R.drawable.ic_clap_color_vector2 : R.drawable.ic_clap_vector)
                .setImageResource(R.id.iv_scraped, content.isScraped() ? R.drawable.ic_scrap_vector : R.drawable.ic_scrap_border)
                .setText(R.id.tv_player_rating, content.getPlayerRatingString())
                .setText(R.id.tv_home_team_name, content.getMatch().getHomeTeamName())
                .setText(R.id.tv_away_team_name, content.getMatch().getAwayTeamName())
                .setText(R.id.tv_match_date, content.getMatch().getMatchDate())
                .setBackgroundColor(R.id.tv_player_rating, ColorUtils.getRatingBackgroundColor(mContext, content.getRating()))
                .setText(R.id.board_name, content.getBoardName())
        //.setGone(R.id.board_name, content.getPostType() == PostType.BOARD_ALL)
        ;

        // -- 유저 아바타 --
        GlideApp.with(mContext)
                .load(content.getUser().getAvatarUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_person_grey_vector)
                .into((ImageView)helper.getView(R.id.avatar));

        // --- 홈팀 ---
        GlideApp.with(mContext)
                .load(content.getHomeTeamEmblemUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_empty_emblem_vector_1)
                .into((ImageView)helper.getView(R.id.home_team_emblem));

        // --- 어웨이팀 ---
        GlideApp.with(mContext)
                .load(content.getAwayTeamEmblemUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_empty_emblem_vector_1)
                .into((ImageView)helper.getView(R.id.away_team_emblem));
    }

    /**
     *
     * @param helper
     * @param content
     */
    private void bindPlayerRatingContent(BaseViewHolder helper, ContentHeaderModel content) {

        helper.addOnClickListener(R.id.comment_box)
                .addOnClickListener(R.id.fl_profile_box);

        helper.setText(R.id.contents_title, content.getTitle())
                .setText(R.id.comment_count, String.valueOf(content.getComments()))
                .setText(R.id.board_name, content.getBoardName())
        //.setGone(R.id.board_name, content.getPostType() == PostType.BOARD_ALL)
        ;

        /**
         * 게시판 - 작성자 - 작성시간 - 하이파이브 - 스크랩
         */
        int start;
        int end;
        SpannableStringBuilder ssb = new SpannableStringBuilder();

        start = ssb.length();
        ssb.append(content.getNickName());
        end = ssb.length();
        ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.md_blue_grey_500)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        start = ssb.length();
        ssb.append("  ").append(content.getUpdatedString());
        end = ssb.length();
        ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.md_grey_500)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        if ( content.getLikers() > 0) {
            start = ssb.length();
            ssb.append("  ").append(String.valueOf(content.getLikers())).append(" 하이파이브");
            end = ssb.length();
            //ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.md_amber_500)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if ( content.isScraped() ) {
            start = ssb.length();
            ssb.append("  " + "스크랩됨");
            end = ssb.length();
            //ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.md_amber_500)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }


        helper.setText(R.id.tv_detail_info, ssb);

        if ( content.getMatch() != null ) {
            helper.setText(R.id.contents_title2, content.getMatch().getMatchDate2());

            // --- 홈팀 ---
            GlideApp.with(mContext)
                    .load(content.getHomeTeamEmblemUrl())
                    .centerCrop()
                    .transform(new CropCircleTransformation())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_empty_emblem_vector_1)
                    .into((ImageView)helper.getView(R.id.home_team_emblem));

            // --- 어웨이팀 ---
            GlideApp.with(mContext)
                    .load(content.getAwayTeamEmblemUrl())
                    .centerCrop()
                    .transform(new CropCircleTransformation())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_empty_emblem_vector_1)
                    .into((ImageView)helper.getView(R.id.away_team_emblem));
        }

        GlideApp.with(mContext)
                .load(content.getUser().getAvatarUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_person_grey_vector)
                .into((ImageView)helper.getView(R.id.avatar));
    }

    /*------------------------------------------------------------------------*/

    private void bindFootballNews(BaseViewHolder helper, ContentHeaderModel content) {

        helper.setText(R.id.tv_title, content.getTitle())
                .setText(R.id.tv_user_name, content.getNickName())
                .setText(R.id.tv_created, content.getCreatedString())
                .setText(R.id.tv_hifive_count, /*"HiFive " + */String.valueOf(content.getLikers()))
                .setTextColor(R.id.tv_hifive_count, ContextCompat.getColor(mContext, content.isLiked() ? R.color.md_indigo_300 : R.color.md_grey_500) )
                .setImageResource(R.id.iv_hifive, content.isLiked() ? R.drawable.ic_clap_color_vector2 : R.drawable.ic_clap_vector)
                .setImageResource(R.id.iv_scraped, content.isScraped() ? R.drawable.ic_scrap_vector : R.drawable.ic_scrap_border)
                .setText(R.id.comment_count, String.valueOf(content.getComments()))
        //.setText(R.id.board_name, content.getBoardName())
        //.setGone(R.id.board_name, content.getPostType() == PostType.BOARD_ALL)
        ;


        if ( content.getImgs() != null
                && content.getImgs().size() > 0
                && content.getImgs().get(0).getStreamType() == StreamViewerModel.IMAGE ) {

            GlideApp.with(mContext)
                    .load(content.getImgs().get(0).getStreamUrl())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_face)
                    .into((ImageView)helper.getView(R.id.iv_news));
        }
    }

    /**
     *
     * @param helper
     * @param content
     */
    private void bindFootballNews2(BaseViewHolder helper, ContentHeaderModel content) {

        FlexboxLayout fbTagList = helper.getView(R.id.fb_tags);
        fbTagList.removeAllViews();
        ChipCloud chipCloud = new ChipCloud(mContext, fbTagList, config);

        helper.setText(R.id.tv_title, content.getTitle())
                .setText(R.id.tv_user_name, content.getNickName())
                .setText(R.id.tv_created, content.getCreatedString())
                .setText(R.id.tv_hifive_count, /*"HiFive " + */String.valueOf(content.getLikers()))
                .setTextColor(R.id.tv_hifive_count, ContextCompat.getColor(mContext, content.isLiked() ? R.color.md_indigo_300 : R.color.md_grey_500) )
                .setImageResource(R.id.iv_hifive, content.isLiked() ? R.drawable.ic_clap_color_vector2 : R.drawable.ic_clap_vector)
                .setImageResource(R.id.iv_scraped, content.isScraped() ? R.drawable.ic_scrap_vector : R.drawable.ic_scrap_border)
                .setText(R.id.comment_count, String.valueOf(content.getComments()))
                .setText(R.id.board_name, content.getBoardName())
        //.setGone(R.id.board_name, content.getPostType() == PostType.BOARD_ALL)
        ;


        if ( content.getImgs() != null
                && content.getImgs().size() > 0
                && content.getImgs().get(0).getStreamType() == StreamViewerModel.IMAGE ) {

            GlideApp.with(mContext)
                    .load(content.getImgs().get(0).getStreamUrl())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_face)
                    .into((ImageView)helper.getView(R.id.iv_news));
        }

        if ( content.getTags() != null && content.getTags().size() > 0 ) {
            chipCloud.addChips(content.getTags());
        }
    }

    /*------------------------------------------------------------------------*/

    /**
     * 베스트 게시글 헤더
     *  - 현재는 베스트글을 날짜 단위로 가져오지 않기 때문에 사용을 안하고 있다.
     * @param helper
     * @param bestHeader
     */
    private void bindBestDateHeader(BaseViewHolder helper, BestContentListHeaderModel bestHeader) {

        helper.setText(R.id.best_content_date, bestHeader.getHeader());
    }


    /**
     * 베스트 게시글
     *  - 여기서 댓글, 하이파이브 다 하자....
     *  - 공감베스트 -> 댓글 베스트로 변경하자
     * @param helper
     * @param content
     */
    private void bindBestContent2(BaseViewHolder helper, ContentHeaderModel content) {

        helper.addOnClickListener(R.id.iv_liked)
                .addOnClickListener(R.id.like_count)
                .addOnClickListener(R.id.iv_scraped)
                .addOnClickListener(R.id.comment_icon)
                .addOnClickListener(R.id.comment_count)
                .addOnClickListener(R.id.comment_box)
                .addOnClickListener(R.id.fl_profile_box)
        ;

        String itemPosition = String.valueOf(helper.getLayoutPosition()+1);
        ImageView ivPosition = helper.getView(R.id.iv_position);
        TextDrawable drawable1 = TextDrawable.builder()
                .beginConfig()
                .fontSize(30)
                .endConfig()
                .buildRound(itemPosition, ColorGenerator.MATERIAL.getColor(itemPosition))
                ;
        ivPosition.setImageDrawable(drawable1);

        helper.setText(R.id.contents_title, content.getTitle())
                .setText(R.id.board_name, content.getBoardName())
                .setText(R.id.comment_count, String.valueOf(content.getComments()))
                .setText(R.id.tv_detail_info, getContentInfoSpannable(content))
        ;

        GlideApp.with(mContext)
                .load(content.getUser().getAvatarUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_face)
                .into((ImageView)helper.getView(R.id.avatar));
    }


    /*------------------------------------------------------------------------*/

    /**
     * 하이파이브 베스트 - 사용안함 ?
     * @param helper
     * @param content
     */
    private void bindBestContent3(BaseViewHolder helper, ContentHeaderModel content) {

        Timber.i("bindBestContent3");
        helper.addOnClickListener(R.id.iv_liked)
                .addOnClickListener(R.id.like_count)
                .addOnClickListener(R.id.iv_scraped)
                .addOnClickListener(R.id.comment_icon)
                .addOnClickListener(R.id.comment_count)
                .addOnClickListener(R.id.comment_box)
                .addOnClickListener(R.id.fl_profile_box)
        ;

        String itemPosition = String.valueOf(helper.getLayoutPosition()+1);
        ImageView ivPosition = helper.getView(R.id.iv_position);
        TextDrawable drawable1 = TextDrawable.builder()
                .beginConfig()
                .fontSize(30)
                .endConfig()
                .buildRound(itemPosition, ColorGenerator.MATERIAL.getColor(itemPosition))
                ;
        ivPosition.setImageDrawable(drawable1);

        helper.setText(R.id.contents_title, content.getTitle())
                .setText(R.id.board_name, content.getBoardName())
                //.setGone(R.id.board_name, content.getPostType() == PostType.BOARD_ALL)
                .setText(R.id.comment_count, String.valueOf(content.getLikers()))
                .setImageDrawable(R.id.comment_icon, mContext.getDrawable(R.drawable.ic_clap_color_vector))
        ;

        /**
         * 게시판 - 작성자 - 작성시간 - 하이파이브 - 스크랩
         */
        int start;
        int end;
        SpannableStringBuilder ssb = new SpannableStringBuilder();

        start = ssb.length();
        ssb.append(content.getNickName());
        end = ssb.length();
        ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.md_blue_grey_500)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        start = ssb.length();
        ssb.append("  ").append(content.getUpdatedString());
        end = ssb.length();
        ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.md_grey_500)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        if ( content.getComments() > 0) {
            start = ssb.length();
            ssb.append("  ").append(String.valueOf(content.getComments())).append(" 댓글");
            end = ssb.length();
            //ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.md_amber_500)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if ( content.isScraped() ) {
            start = ssb.length();
            ssb.append("  " + "스크랩됨");
            end = ssb.length();
            //ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.md_amber_500)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }


        helper.setText(R.id.tv_detail_info, ssb);

        GlideApp.with(mContext)
                .load(content.getUser().getAvatarUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_face)
                .into((ImageView)helper.getView(R.id.avatar));
    }

    /*------------------------------------------------------------------------*/

    /**
     * 2018.04.10 옛날방식 bindBestContent2() 함수로 변경한다.  - 사용안함 ?
     * @param helper
     * @param content
     */
    private void bindBestContent(BaseViewHolder helper, ContentHeaderModel content) {

        ImageView ivComment = helper.getView(R.id.comment_icon);

        helper.addOnClickListener(R.id.avatar)
                .addOnClickListener(R.id.iv_liked)
                .addOnClickListener(R.id.like_count)
                .addOnClickListener(R.id.iv_scraped)
                .addOnClickListener(R.id.comment_icon)
                .addOnClickListener(R.id.comment_count)
        ;

        helper.setText(R.id.contents_title, content.getTitle())
                .setText(R.id.user_name, content.getNickNameAndUserName())
                .setText(R.id.reg_date, content.getCreatedString())
                .setText(R.id.like_count, String.valueOf(content.getLikers()))
                //.setText(R.id.scrap_count, String.valueOf(content.getScraps()))
                .setText(R.id.comment_count, String.valueOf(content.getComments()))
                .setText(R.id.board_name, getBoardNameBadge(content.getBoardName()))
                .setText(R.id.like_count2, "HiFive " + String.valueOf(content.getLikers()))
                .setImageResource(R.id.iv_liked, content.isLiked() ? R.drawable.ic_clap_color_vector2 : R.drawable.ic_clap_vector)
                .setImageResource(R.id.iv_scraped, content.isScraped() ? R.drawable.ic_scrap_vector : R.drawable.ic_scrap_border)
                .setTextColor(R.id.like_count2, ContextCompat.getColor(mContext, content.isLiked() ? R.color.md_indigo_300 : R.color.md_grey_500) )
                //.setTextColor(R.id.scrap_count2, ContextCompat.getColor(mContext, content.isLiked() ? R.color.md_indigo_300 : R.color.md_grey_500) )
                .setGone(R.id.board_name, true);

        helper.setText(R.id.contents_preview, content.getPreview())
                .setGone(R.id.contents_preview, !TextUtils.isEmpty(content.getPreview()))
                .setGone(R.id.contents_image, content.getUserAction().getHasImgs() != 0)
        ;

        if ( content.getImgs() != null
                && content.getImgs().size() > 0
                && content.getImgs().get(0).getStreamType() == StreamViewerModel.IMAGE ) {

            GlideApp.with(mContext)
                    .load(content.getImgs().get(0).getStreamUrl())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_image)
                    .into((ImageView)helper.getView(R.id.contents_image));
        }

        // --- 사용자 프로필 ---
        GlideApp.with(mContext)
                .load(content.getUser().getAvatarUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_face)
                .into((ImageView)helper.getView(R.id.avatar));

        // --- 댓글 ---
        if ( content.getComments() > 100 ) {
            ivComment.setColorFilter(ContextCompat.getColor(mContext, R.color.md_indigo_600));
        } else if ( content.getComments() > 50 ) {
            ivComment.setColorFilter(ContextCompat.getColor(mContext, R.color.md_indigo_400));
        } else if ( content.getComments() > 20 ) {
            ivComment.setColorFilter(ContextCompat.getColor(mContext, R.color.md_indigo_200));
        } else if ( content.getComments() > 10 ) {
            ivComment.setColorFilter(ContextCompat.getColor(mContext, R.color.md_indigo_100));
        } else {
            ivComment.setColorFilter(ContextCompat.getColor(mContext, R.color.md_grey_400));
        }

    }
}

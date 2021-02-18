package com.ddastudio.hifivefootball_android.content_list;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.content_list.model.BestContentListHeaderModel;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.ui.utils.picasso.PicassoCircleTransformation;
import com.ddastudio.hifivefootball_android.utils.ColorGenerator;
import com.ddastudio.hifivefootball_android.utils.FileUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import timber.log.Timber;


/**
 * 게시글 리스트에서 mp4 자동 재생을 위한 아답터
 *  - 미완성
 */
public class ContentListRvAdapter2 extends BaseMultiItemQuickAdapter<MultiItemEntity, ContentViewHolder> {

    public ContentListRvAdapter2(List<MultiItemEntity> data) {
        super(data);

        // 1. 게시글 디폴트 - 자유게시판, 축구게시판 일반글
        //addItemType(ViewType.CONTENT_GENERAL, R.layout.row_content_list_item2);
        addItemType(ViewType.CONTENT_GENERAL, R.layout.row_content_default_card_item);
    }

    @Override
    protected int getDefItemViewType(int position) {
        return super.getDefItemViewType(position);
    }

    @Override
    protected void convert(ContentViewHolder helper, MultiItemEntity item) {

        switch (helper.getItemViewType()) {

            // 일반게시글
            case ViewType.CONTENT_GENERAL:
                bindDefaultCardContent(helper, (ContentHeaderModel)item);
                break;

            case ViewType.CONTENT_BEST_HIFIVE:
                bindBestContent3(helper, (ContentHeaderModel)item);
                break;
            case ViewType.CONTENT_BEST_COMMENT:
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

    private void bindDefaultCardContent(ContentViewHolder helper, ContentHeaderModel content) {

        helper.addOnClickListener(R.id.iv_comment)
                .addOnClickListener(R.id.iv_hifive)
                .addOnClickListener(R.id.iv_scraped)
                .addOnClickListener(R.id.iv_avatar)
                .addOnClickListener(R.id.include_player)
                .addOnClickListener(R.id.include_team);

//        if ( content.getImgs() != null && content.getImgs().size() > 0) {
//
//            String extension = FileUtil.getExtension(content.getImgs().get(0).getStreamUrl());
//
//            if ( extension.equals(".mp4")) {
//                helper.setGone(R.id.player, true);
//                helper.setGone(R.id.iv_image, false);
//            } else {
//                helper.setGone(R.id.player, false);
//                helper.setGone(R.id.iv_image, true);
//            }
//            helper.setMp4Uri(content.getImgs().get(0).getStreamUrl());
//            //helper.setMp4Uri("http://cdn.hifivefootball.com/origin/article/20180726_124500_889_oopk.mp4");
//        }


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

        if ( content.getImgs() != null && content.getImgs().size() > 0) {

            //helper.setGone(R.id.fl_media, true);
            String extension = FileUtil.getExtension(content.getImgs().get(0).getStreamUrl());

            if ( extension.equals(".mp4")) {
                helper.setMp4Uri(content.getImgs().get(0).getStreamUrl());
                helper.setGone(R.id.player, true);
                helper.setGone(R.id.iv_image, false);
            } else {
                helper.setGone(R.id.player, false);
                helper.setGone(R.id.iv_image, true);
                GlideApp.with(mContext)
                        .load(content.getImgs().get(0).getStreamUrl())
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_empty_dracula)
                        .into((ImageView)helper.getView(R.id.iv_image));
            }

        } else {
            //helper.setGone(R.id.fl_media, false);
            helper.setGone(R.id.player, false);
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
}

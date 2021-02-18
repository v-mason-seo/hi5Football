package com.ddastudio.hifivefootball_android.player_comment;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingInfoModel;
import com.ddastudio.hifivefootball_android.data.model.community.MatchTalkModel;
import com.ddastudio.hifivefootball_android.data.model.community.SimpleSectionHeaderModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.ui.utils.ColorUtils;
import com.ddastudio.hifivefootball_android.utils.DrawableHelper;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by hongmac on 2018. 1. 7..
 */

public class PlayerCommentRvAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public PlayerCommentRvAdapter(List<MultiItemEntity> data) {
        super(data);

        addItemType(ViewType.SIMPLE_SECTION_HEADER, R.layout.row_simple_section_item);
        addItemType(ViewType.PLAYER_RATING_INFO, R.layout.row_player_comment_item);
        addItemType(ViewType.MATCH_TALK, R.layout.row_match_talk_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {

            case ViewType.SIMPLE_SECTION_HEADER:
                bindPlayerRatingInfo(helper, (SimpleSectionHeaderModel)item);
                break;

            case ViewType.PLAYER_RATING_INFO:
                bindPlayerRatingInfo(helper, (PlayerRatingInfoModel)item);
                break;

            case ViewType.MATCH_TALK:
                bindMatchTalk(helper, (MatchTalkModel)item);
                break;
        }
    }

    /*------------------------------------------------------------*/

    /**
     * 심플 섹션 헤더정보
     * @param helper
     * @param header
     */
    private void bindPlayerRatingInfo(BaseViewHolder helper, SimpleSectionHeaderModel header) {

        helper.addOnClickListener(R.id.iv_more_item);

        helper.setText(R.id.tv_header, header.getTitle())
                .setGone(R.id.iv_more_item, header.isbVisibleMoreButton());

    }

    /**
     * 관련 글
     * @param helper
     * @param rating
     */
    private void bindPlayerRatingInfo(BaseViewHolder helper, PlayerRatingInfoModel rating) {

        helper.addOnClickListener(R.id.iv_avatar);

        helper.setText(R.id.tv_player_name, rating.getPlayer().getBackNumber() + ". " +rating.getPlayerName())
                .setTextColor(R.id.tv_player_name, ColorUtils.getRatingTextColor(mContext, rating.getPlayerRating()))
                .setText(R.id.tv_user_name, rating.getNickName())
                .setGone(R.id.tv_user_name, !TextUtils.isEmpty(rating.getNickName()))
                .setText(R.id.tv_player_comment, rating.getPlayerComment())
                .setGone(R.id.tv_player_comment, !TextUtils.isEmpty(rating.getPlayerComment()))
                .setText(R.id.tv_player_rating, rating.getPlayerRatingString())
                .setBackgroundColor(R.id.tv_player_rating, ColorUtils.getRatingBackgroundColor(mContext, rating.getPlayerRating()))
                .setText(R.id.tv_team_name, rating.getPlayer().getTeamName())
                .setText(R.id.tv_player_position, rating.getPlayer().getPos())
                .setGone(R.id.iv_goal, rating.hasGoals())
                .setGone(R.id.iv_yellow_card1, rating.hasYellowCards())
                .setGone(R.id.iv_red_card, rating.hasRedCards())
                .setGone(R.id.tv_player_subs_minute, rating.getSubsMinute() != null )
                .setText(R.id.tv_player_subs_minute, rating.getSubsMinute())
                .setGone(R.id.iv_player_substitutions, rating.hasSubstitutions())
                .setImageDrawable(R.id.iv_player_substitutions, getSubsitutionsImageResource(rating.getSubstitutions()))
        ;

        // --- 플레이어 프로필 ---
        GlideApp.with(mContext)
                .load(rating.getPlayer().getPlayerLargeImageUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_person_grey_vector)
                .into((ImageView)helper.getView(R.id.iv_avatar));

        GlideApp.with(mContext)
                .load(rating.getPlayer().getEmblemUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_empty_emblem_vector_1)
                .into((ImageView)helper.getView(R.id.iv_emblem));
    }

    /**
     * 관련 매치정보
     * @param helper
     * @param talk
     */
    private void bindMatchTalk(BaseViewHolder helper, MatchTalkModel talk) {

        helper.addOnClickListener(R.id.iv_avatar);

        helper.setText(R.id.tv_status, talk.getStatusMinute())
                .setText(R.id.tv_user_name, talk.getUser().getUsername())
                .setText(R.id.tv_created, talk.getCreated())
                .setText(R.id.tv_content, talk.getContent());

        GlideApp.with(mContext)
                .load(talk.getUser().getAvatarUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_person_grey_vector)
                .into((ImageView)helper.getView(R.id.iv_avatar));

    }

    /*------------------------------------------------------------*/

    private Drawable getSubsitutionsImageResource(String substitution) {

        if (substitution != null && substitution.equals("off")) {
            return DrawableHelper.withContext(mContext)
                    .withColor(R.color.md_red_400)
                    .withDrawable(R.drawable.ic_substitution_out)
                    .tint().get();

        } else if (substitution != null && substitution.equals("on")) {
            return DrawableHelper.withContext(mContext)
                    .withColor(R.color.md_green_400)
                    .withDrawable(R.drawable.ic_substitution_in)
                    .tint().get();
        } else {
            return null;
        }
    }
}

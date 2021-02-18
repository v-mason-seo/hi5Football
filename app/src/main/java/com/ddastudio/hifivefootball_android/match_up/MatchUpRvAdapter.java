package com.ddastudio.hifivefootball_android.match_up;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.model.community.SimpleSectionHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.utils.ColorGenerator;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import timber.log.Timber;

/**
 * Created by hongmac on 2018. 2. 22..
 */

public class MatchUpRvAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public MatchUpRvAdapter(List<MultiItemEntity> data) {
        super(data);

        // 매치업 헤더
        // 예) 국가대표 친선전 2018 1주차
        addItemType(ViewType.SIMPLE_SECTION_HEADER, R.layout.row_simple_section_item);

        // 매치업 기본 스타일
        // 현재는 모두 기본스타일을 사용한다.
        addItemType(ViewType.MATCH_UP_LIVE, R.layout.row_match_up_default_item);
        addItemType(ViewType.MATCH_UP_RECENT, R.layout.row_match_up_default_item);
        addItemType(ViewType.MATCH_UP_POPULAR, R.layout.row_match_up_default_item);

        // 종료된 인기매치는 다른 스타일 적용
        addItemType(ViewType.MATCH_UP_FINISH_RECENT, R.layout.row_match_up_list_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        switch (helper.getItemViewType()) {

            case ViewType.MATCH_UP_LIVE:
            case ViewType.MATCH_UP_RECENT:
            case ViewType.MATCH_UP_POPULAR:
                onBindMatchUp(helper, (MatchModel)item);
                break;
            case ViewType.MATCH_UP_FINISH_RECENT:
                onBindMatchUpList(helper, (MatchModel)item);
                break;
            case ViewType.SIMPLE_SECTION_HEADER:
                bindSimpleSection(helper, (SimpleSectionHeaderModel)item);
                break;
        }
    }

    private void onBindMatchUpList(BaseViewHolder helper, MatchModel match) {

        helper.addOnClickListener(R.id.iv_home_emblem)
                .addOnClickListener(R.id.iv_away_emblem)
                .addOnClickListener(R.id.iv_competition)
                .addOnClickListener(R.id.iv_more_item)
                .addOnClickListener(R.id.btn_rating)
        ;

        helper
                .setText(R.id.tv_home_team_name, match.getHomeTeamName())
                .setText(R.id.tv_away_team_name, match.getAwayTeamName())
                .setText(R.id.tv_home_team_score, match.getHomeTeamScoreString())
                .setText(R.id.tv_away_team_score, match.getAwayTeamScoreString());

        ImageView ivMatchDate = helper.getView(R.id.iv_match_date);
        TextDrawable drawable1 = TextDrawable.builder()
                .beginConfig()
                .fontSize(30)
                .endConfig()
                .buildRound(match.getMatchDate3(), ColorGenerator.MATERIAL.getColor(match.getMatchDate3()))
                ;
        ivMatchDate.setImageDrawable(drawable1);

        // --- 홈팀 ---
//        Glide.with(mContext)
//                .load(match.getHomeTeamEmblemUrl())
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_empty_emblem_vector_1))
//                .into((ImageView)helper.getView(R.id.iv_home_emblem));
        Picasso.get()
                .load(match.getHomeTeamEmblemUrl())
                .placeholder(R.drawable.ic_empty_emblem_vector_1)
                .fit()
                .centerInside()
                .into((ImageView)helper.getView(R.id.iv_home_emblem));

        // --- 어웨이팀 ---
        Picasso.get()
                .load(match.getAwayTeamLargeEmblemUrl())
                .placeholder(R.drawable.ic_empty_emblem_vector_1)
                .fit()
                .centerInside()
                .into((ImageView)helper.getView(R.id.iv_away_emblem));

//        Glide.with(mContext)
//                .load(match.getAwayTeamLargeEmblemUrl())
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_empty_emblem_vector_1))
//                .into((ImageView)helper.getView(R.id.iv_away_emblem));
    }

    private void onBindMatchUp(BaseViewHolder helper, MatchModel match) {

        helper.addOnClickListener(R.id.iv_home_emblem)
                .addOnClickListener(R.id.iv_away_emblem)
                .addOnClickListener(R.id.iv_competition)
                .addOnClickListener(R.id.iv_more_item);

        helper
                .setText(R.id.tv_competition_name, match.getCompetitionName() + "  " + match.getMatchDate2())
                .setText(R.id.tv_match_status, match.getStatus())
                .setText(R.id.tv_match_id, String.valueOf(match.getMatchId()))
                .setText(R.id.tv_match_date, match.getMatchDate())
                .setText(R.id.tv_home_team_name, match.getHomeTeamName())
                .setText(R.id.tv_away_team_name, match.getAwayTeamName())
                .setText(R.id.tv_home_team_score, match.getHomeTeamScoreString())
                .setText(R.id.tv_away_team_score, match.getAwayTeamScoreString())
                .setTextColor(R.id.tv_home_team_score, getScoreColor(match.getHomeTeamScore(), match.getAwayTeamScore()))
                .setTextColor(R.id.tv_away_team_score, getScoreColor(match.getAwayTeamScore(), match.getHomeTeamScore()))
                //.setText(R.id.tv_arena_chat_count, match.getUserCountString())
                .setText(R.id.tv_match_content_count, match.getContentCountString())
                .setText(R.id.tv_player_rating_count, match.getRatingCountString())
                ;

        // 컴피티션
        GlideApp.with(mContext)
                .load(match.getCompetitionSmallImage())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.placeholder(R.drawable.ic_circle_grey_vector)
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_circle_grey_vector))
                .into((ImageView)helper.getView(R.id.iv_competition));

        // --- 홈팀 ---
        GlideApp.with(mContext)
                .load(match.getHomeTeamLargeEmblemUrl())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_empty_emblem_vector_1))
                .into((ImageView)helper.getView(R.id.iv_home_emblem));

        // --- 어웨이팀 ---
        GlideApp.with(mContext)
                .load(match.getAwayTeamLargeEmblemUrl())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_empty_emblem_vector_1))
                .into((ImageView)helper.getView(R.id.iv_away_emblem));
    }

    private void bindSimpleSection(BaseViewHolder helper, SimpleSectionHeaderModel header) {

        helper.setText(R.id.tv_header, header.getTitle());
    }

    /*--------------------------------------------------------------------------*/

    private int getScoreColor(String src_score, String target_score) {

        Integer src;
        Integer target;

        try {
            src = Integer.parseInt(src_score);
        } catch (NumberFormatException e) {
            src = 0;
        }

        try {
            target = Integer.parseInt(target_score);
        } catch (NumberFormatException e) {
            target = 0;
        }

        if ( src > target ) {
            return ContextCompat.getColor(mContext, R.color.md_blue_700);
        } else {
            return ContextCompat.getColor(mContext, R.color.md_grey_700);
        }
    }
}

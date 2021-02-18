package com.ddastudio.hifivefootball_android.ui.rvadapter;

import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.football.CompModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchDateModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by hongmac on 2017. 10. 17..
 */

public class ScheduleRvAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ScheduleRvAdapter(List<MultiItemEntity> data) {
        super(data);

        addItemType(ViewType.SIMPLE_MATCH_DATE, R.layout.row_simple_match_date_item);
        addItemType(ViewType.COMPETITION, R.layout.row_simple_competition_item);
        addItemType(MultipleItem.ARENA_SCHEDULE, R.layout.row_schedule_item);
        addItemType(MultipleItem.ARENA_LAST_SCHEDULE, R.layout.row_last_schedule_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        switch (helper.getItemViewType()) {
            case MultipleItem.ARENA_SCHEDULE:
                bindMatch(helper, (MatchModel)item);
                break;
            case ViewType.SIMPLE_MATCH_DATE:
                bindMatchDate(helper, (MatchDateModel)item);
                break;
            case ViewType.COMPETITION:
                bindCompetition(helper, (CompModel)item);
                break;
        }
    }

    /*---------------------------------------------------------------*/

    private void bindMatch(BaseViewHolder helper, MatchModel match) {

        helper.addOnClickListener(R.id.hometeam_image)
                .addOnClickListener(R.id.awayteam_image)
                .addOnClickListener(R.id.btn_enter_arena)
        ;

        helper.setText(R.id.hometeam_name, match.getHomeTeamName())
                .setText(R.id.awayteam_name, match.getAwayTeamName())
                .setText(R.id.match_id, String.valueOf(match.getMatchFaId()) + " / " + match.getMatchId())
                .setText(R.id.match_time, match.getStatus().equals("FT") ? "경기 종료" : match.getTime())
                .setText(R.id.match_time2, match.getMatchDate())
                .setText(R.id.home_team_score, match.getHomeTeamScoreString())
                .setText(R.id.away_team_score, match.getAwayTeamScoreString())
                .setTextColor(R.id.home_team_score, getScoreColor(match.getHomeTeamScore(), match.getAwayTeamScore()))
                .setTextColor(R.id.away_team_score, getScoreColor(match.getAwayTeamScore(), match.getHomeTeamScore()))
        ;

        // --- Local team ---
        GlideApp.with(mContext)
                .load(match.getHomeTeamEmblemUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_empty_emblem_vector_1)
                .into((ImageView)helper.getView(R.id.hometeam_image));

        // -- Visitor team ---
        GlideApp.with(mContext)
                .load(match.getAwayTeamEmblemUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_empty_emblem_vector_1)
                .into((ImageView)helper.getView(R.id.awayteam_image));
    }

    private void bindMatchDate(BaseViewHolder helper, MatchDateModel matchDate) {
        helper.setText(R.id.tv_title, (matchDate.getTitle()));
    }

    private void bindCompetition(BaseViewHolder helper, CompModel comp) {

        helper.addOnClickListener(R.id.btn_show_standing);
        helper.setText(R.id.tv_competition_name, comp.getCompetitionName())
        ;

        // 리그 엠블럼
        GlideApp.with(mContext)
                .load(comp.getCompetitionSmallImageUrl())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_circle_grey_vector)
                .into((ImageView)helper.getView(R.id.iv_competition));
    }

    /*---------------------------------------------------------------*/

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

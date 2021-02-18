package com.ddastudio.hifivefootball_android.match_overview;

import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.db.chart.animation.Animation;
import com.db.chart.model.BarSet;
import com.db.chart.model.ChartSet;
import com.db.chart.model.LineSet;
import com.db.chart.renderer.AxisRenderer;
import com.db.chart.renderer.XRenderer;
import com.db.chart.renderer.YRenderer;
import com.db.chart.view.BarChartView;
import com.db.chart.view.HorizontalStackBarChartView;
import com.db.chart.view.LineChartView;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.data.model.overview.MatchArenaInfoModel;
import com.ddastudio.hifivefootball_android.data.model.overview.MatchPlayerRatingModel;
import com.ddastudio.hifivefootball_android.data.model.overview.MatchPredictionsModel;
import com.ddastudio.hifivefootball_android.data.model.overview.MatchRecentFormModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.ui.utils.ColorUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import timber.log.Timber;

/**
 * Created by hongmac on 2017. 12. 13..
 */

public class MatchOverviewRvAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public final static int POSITION_ARENA_INFO = 0;
    public final static int POSITION_BASIC_INFO = 1;
    public final static int POSITION_PLAYER_RATING = 2;
    public final static int POSITION_PREDICTIOMS = 3;
    public final static int POSITION_RECENT_FORM = 4;


    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public MatchOverviewRvAdapter(List<MultiItemEntity> data) {
        super(data);

        addItemType(ViewType.MATCH_OVERVIEW_ARENA_INFO, R.layout.row_match_overview_arena_item);
        addItemType(MultipleItem.ARENA_SCHEDULE, R.layout.row_match_overview_basic_info_item);
        addItemType(ViewType.MATCH_OVERVIEW_PLAYER_RATING, R.layout.row_match_overview_player_rating);
        addItemType(ViewType.MATCH_OVERVIEW_PREDICTIONS, R.layout.row_match_overview_predictions_item);
        addItemType(ViewType.MATCH_OVERVIEW_RECENT_FORM, R.layout.row_match_overview_recent_form_item);

        addItemType(ViewType.MATCH_OVERVIEW_RELATION_CONTENT, R.layout.row_match_overview_relation_content_item);
        addItemType(ViewType.MATCH_OVERVIEW_RELATION_COLUMN, R.layout.row_match_overview_relation_content_item);
        addItemType(ViewType.MATCH_OVERVIEW_RELATION_NEWS, R.layout.row_match_overview_relation_content_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        switch (helper.getItemViewType()) {
            case ViewType.MATCH_OVERVIEW_ARENA_INFO:
                bindArenaInfo(helper, (MatchArenaInfoModel)item);
                break;

            case ViewType.MATCH_OVERVIEW_PREDICTIONS:
                MatchPredictionsModel predictions = (MatchPredictionsModel)item;
                bindPredictions(helper, predictions);
                break;

            case ViewType.MATCH_OVERVIEW_PLAYER_RATING:
                bindPlayerRating(helper, (MatchPlayerRatingModel)item);
                break;

            case ViewType.MATCH_OVERVIEW_RECENT_FORM:
                MatchRecentFormModel recentForm = (MatchRecentFormModel)item;
                bindRecentForm(helper, recentForm);
                break;

            case MultipleItem.ARENA_SCHEDULE:
                bindMatchBasicInfo(helper, item);
                break;
        }
    }

    private final Runnable unlockAction = new Runnable() {
        @Override
        public void run() {

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //unlock();
                }
            }, 500);
        }
    };

    public void bindMatchBasicInfo(BaseViewHolder helper, MultiItemEntity item) {

        if ( item instanceof MatchModel) {

            MatchModel match = (MatchModel)item;

            helper.setText(R.id.tv_competition_name, match.getCompetitionName())
                    .setText(R.id.tv_venue, match.getVenue())
                    .setText(R.id.tv_season, match.getSeason())
                    .setText(R.id.tv_match_date, match.getMatchDate() + "  " + match.getTime())
            ;

            GlideApp.with(mContext)
                    .load(match.getCompetitionImage())
                    .centerCrop()
                    .transform(new CropCircleTransformation())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_empty_emblem_vector_2)
                    .into((ImageView)helper.getView(R.id.iv_comp));
        }
    }

    public void bindArenaInfo(BaseViewHolder helper, MatchArenaInfoModel arena) {

        helper.addOnClickListener(R.id.card_arena);
    }

    public void bindPredictions(BaseViewHolder helper, MatchPredictionsModel item) {

        TextView showResult = helper.getView(R.id.show_result);
        LinearLayout mResultContainer = helper.getView(R.id.result_container);
        LinearLayout mChoice = helper.getView(R.id.choice_container);
        HorizontalStackBarChartView mChart = helper.getView(R.id.chart);

        helper.addOnClickListener(R.id.home_team_container)
                .addOnClickListener(R.id.away_team_container)
                .addOnClickListener(R.id.draw_container);

        helper.setText(R.id.away_team_name, item.getAwayTeamName() )
                .setText(R.id.home_team_name, item.getHomeTeamName())
                .setText(R.id.home_team_rate_name, item.getHomeTeamName())
                .setText(R.id.away_team_rate_name, item.getAwayTeamName())
                .setText(R.id.user_count, item.getUserCountString())
                .setText(R.id.home_team_rate, item.getHomeRateString())
                .setText(R.id.draw_rate, item.getDrawRateString())
                .setText(R.id.away_team_rate, item.getAwayRateString())
                //.setBackgroundRes(R.id.home_team_header, R.drawable.circle_color)
        ;

        switch (item.getUserChoice()) {
            case "H":
                helper.setBackgroundRes(R.id.home_header, R.drawable.circle_color);
                break;
            case "D":
                helper.setBackgroundRes(R.id.draw_header, R.drawable.circle_color);
                break;
            case "A":
                helper.setBackgroundRes(R.id.away_header, R.drawable.circle_color);
                break;
        }

        if ( item.getStatus().equals("FT")) {
            helper.getView(R.id.show_result).setEnabled(false);
            mChoice.setVisibility(View.INVISIBLE);
            mResultContainer.setVisibility(View.VISIBLE);
        }

        helper.getView(R.id.show_result).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int visible = mChoice.getVisibility();

                mChoice.setVisibility(visible == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
                mResultContainer.setVisibility(visible == View.VISIBLE ? View.VISIBLE : View.INVISIBLE);
                showResult.setText(visible == View.VISIBLE ? "선택하기" : "결과보기");
            }
        });

        mChart.getData().clear();
        BarSet dataset = new BarSet();
        dataset.addBar("", item.getHomeRate());
        dataset.setColor(mContext.getResources().getColor(R.color.md_blue_600));
        mChart.addData(dataset);

        dataset = new BarSet();
        dataset.addBar("", item.getDrawRate());
        dataset.setColor(mContext.getResources().getColor(R.color.md_grey_400));
        mChart.addData(dataset);

        dataset = new BarSet();
        dataset.addBar("", item.getAwayRate());
        dataset.setColor(mContext.getResources().getColor(R.color.md_blue_grey_400));
        mChart.addData(dataset);

        mChart.setAxisBorderValues(0, 100, 10)
                .show(new Animation().setInterpolator(new DecelerateInterpolator())
                        .withEndAction(unlockAction));

    }

    public void bindPlayerRating(BaseViewHolder helper, MatchPlayerRatingModel item) {

        helper.addOnClickListener(R.id.player_rating_button);

        helper.setText(R.id.tv_best_player_name, item.getBestPlayerName())
                .setText(R.id.tv_worst_player_name, item.getWorstPlayerName())
                .setText(R.id.tv_best_rating, item.getBestPlayerRatingString())
                .setBackgroundColor(R.id.tv_best_rating, item.getBestPlayerRating() == 0f
                        ? ContextCompat.getColor(mContext, R.color.md_green_500)
                        : ColorUtils.getRatingBackgroundColor(mContext, item.getBestPlayerRating()))
                .setText(R.id.tv_worst_rating, item.getWorstPlayerRatingString())
                .setBackgroundColor(R.id.tv_worst_rating, item.getWorstPlayerRating() == 0f
                        ? ContextCompat.getColor(mContext, R.color.md_red_500)
                        : ColorUtils.getRatingBackgroundColor(mContext, item.getWorstPlayerRating()))
        ;

        GlideApp.with(mContext)
                .load(item.getBestPlayerImageUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.placeholder(R.drawable.ic_winner_vector)
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_winner_vector))
                .into((ImageView)helper.getView(R.id.iv_best_player));

        GlideApp.with(mContext)
                .load(item.getWorstPlayerImageUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.placeholder(R.drawable.ic_worst_player_vector)
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_worst_player_vector))
                .into((ImageView)helper.getView(R.id.iv_worst_player));
    }

    public void bindRecentForm(BaseViewHolder helper, MatchRecentFormModel recentForm) {

        BarChartView chartHome = helper.getView(R.id.chart_home);
        BarChartView chartAway = helper.getView(R.id.chart_away);

        helper.setText(R.id.home_header, String.format("라운드 : %d,  순위 : %d", recentForm.getHomeRound(), recentForm.getHomePosition()))
                .setText(R.id.away_header, String.format("라운드 : %d,  순위 : %d", recentForm.getAwayRound(), recentForm.getAwayPosition()));

        if (!TextUtils.isEmpty(recentForm.getHomeRecentForm())) {
            String[] homeLabel = recentForm.getHomeRecentFormLabelArray();
            float[] homeValue = recentForm.getHomeRecentFormValueArray();

            BarSet dataset = new BarSet(homeLabel, homeValue);

            for ( int i =0; i < dataset.getEntries().size(); i++) {

                if ( dataset.getEntries().get(i).getValue() == 1.0f) {
                    dataset.getEntries().get(i).setColor(mContext.getResources().getColor(R.color.md_red_300));
                } else if ( dataset.getEntries().get(i).getValue() == 2.0f) {
                    dataset.getEntries().get(i).setColor(mContext.getResources().getColor(R.color.md_blue_grey_400));
                } else if ( dataset.getEntries().get(i).getValue() == 3.0f) {
                    dataset.getEntries().get(i).setColor(mContext.getResources().getColor(R.color.md_blue_300));
                }
            }

            chartHome.getData().clear();
            chartHome.addData(dataset);
            chartHome.setXLabels(XRenderer.LabelPosition.OUTSIDE)
                    .setYLabels(YRenderer.LabelPosition.NONE)
                    .setAxisBorderValues(0, 3, 1)
                    .show(new Animation().setInterpolator(new DecelerateInterpolator())
                            .withEndAction(unlockAction));
            //.show(new Animation().inSequence(.5f, order).withEndAction(chartOneAction));
        }

        if (!TextUtils.isEmpty(recentForm.getAwayRecentForm())) {
            String[] awayLabel = recentForm.getAwayRecentFormLabelArray();
            float[] awayValue = recentForm.getAwayRecentFormValueArray();

            BarSet dataset2 = new BarSet(awayLabel, awayValue);

            for ( int i =0; i < dataset2.getEntries().size(); i++) {

                if ( dataset2.getEntries().get(i).getValue() == 1.0f) {
                    dataset2.getEntries().get(i).setColor(mContext.getResources().getColor(R.color.md_red_300));
                } else if ( dataset2.getEntries().get(i).getValue() == 2.0f) {
                    dataset2.getEntries().get(i).setColor(mContext.getResources().getColor(R.color.md_blue_grey_400));
                } else if ( dataset2.getEntries().get(i).getValue() == 3.0f) {
                    dataset2.getEntries().get(i).setColor(mContext.getResources().getColor(R.color.md_blue_300));
                }
            }

            chartAway.getData().clear();
            chartAway.addData(dataset2);


            chartAway.setXLabels(XRenderer.LabelPosition.OUTSIDE)
                    .setYLabels(YRenderer.LabelPosition.NONE)
                    .setAxisBorderValues(0, 3, 1)
                    .show(new Animation().setInterpolator(new DecelerateInterpolator())
                            .withEndAction(unlockAction));
            //.show(new Animation().inSequence(.5f, order).withEndAction(chartOneAction));
        }
    }


//    private int getRatingBackgroundColor(float rating) {
//
//        if ( rating >= 8.0) {
//            return ContextCompat.getColor(mContext, R.color.md_green_500);
//        } else if ( rating >= 6.0 && rating < 8.0) {
//            return ContextCompat.getColor(mContext, R.color.md_lime_600);
//        } else if ( rating >= 4.0 && rating < 6.0) {
//            return ContextCompat.getColor(mContext, R.color.md_amber_400);
//        } else if ( rating >= 2.0 && rating < 4) {
//            return ContextCompat.getColor(mContext, R.color.md_orange_400);
//        } else if ( rating > 0 && rating < 2) {
//            return ContextCompat.getColor(mContext, R.color.md_red_500);
//        }
//
//        return ContextCompat.getColor(mContext, R.color.md_grey_400);
//
//    }
}

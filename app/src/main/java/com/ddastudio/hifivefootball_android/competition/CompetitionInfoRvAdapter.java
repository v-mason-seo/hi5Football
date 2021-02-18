package com.ddastudio.hifivefootball_android.competition;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.footballdata.CompetitionMatchHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.football.CompetitionModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.MatchContainerModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.StandingModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.utils.CommonUtils;
import com.ddastudio.hifivefootball_android.utils.DrawableHelper;

import java.util.List;

import cn.nekocode.badge.BadgeDrawable;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by hongmac on 2017. 10. 28..
 */

public class CompetitionInfoRvAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public CompetitionInfoRvAdapter(List<MultiItemEntity> data) {
        super(data);

        addItemType(MultipleItem.BOTTOM_STANDING, R.layout.row_league_table_item);
        addItemType(MultipleItem.ARENA_SCHEDULE, R.layout.row_schedule_item);
        addItemType(MultipleItem.ARENA_MATCH_HEADER, R.layout.row_competition_match_header_item);
    }

    private SpannableString getRecentFormValue(String recentForm) {

        char[] recentArr = recentForm.toCharArray();
        CharSequence charSequence = "";
        SpannableString spannableString;

        for ( int i = 0 ; i < recentArr.length; i++ ) {

            int color;

            switch (recentArr[i]) {
                case 'W':
                    color = mContext.getResources().getColor(R.color.md_blue_50);
                    break;
                case 'L':
                    color = mContext.getResources().getColor(R.color.md_red_100);
                    break;
                default:
                    color = mContext.getResources().getColor(R.color.md_grey_300);
            }

            BadgeDrawable drawable2 =
                    new BadgeDrawable.Builder()
                            .type(BadgeDrawable.TYPE_ONLY_ONE_TEXT)
                            .badgeColor(color)
                            .textSize(CommonUtils.sp2px(mContext, 9))
                            .textColor(mContext.getResources().getColor(R.color.md_grey_700))
                            .text1(String.valueOf(recentArr[i]))
                            .build();

            charSequence = TextUtils.concat(charSequence, " ", drawable2.toSpannable());
        }

        spannableString = new SpannableString(charSequence);
        return spannableString;
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        if ( helper.getItemViewType() == MultipleItem.BOTTOM_STANDING) {

            StandingModel standing = (StandingModel)item;
            SpannableString recentFormValue = getRecentFormValue(standing.getRecent_form());

            helper.setText(R.id.league_table_position, StringValueOf(standing.getPosition()))
                    .setText(R.id.league_table_team_name, standing.getTeamName())
                    .setText(R.id.league_table_w, StringValueOf(standing.getOverallW()))
                    .setText(R.id.league_table_d, StringValueOf(standing.getOverallD()))
                    .setText(R.id.league_table_l, StringValueOf(standing.getOverallL()))
                    .setText(R.id.league_table_point, StringValueOf(standing.getPoints()))
                    .setVisible(R.id.league_table_team_recent, true)
                    .setText(R.id.league_table_team_recent, recentFormValue != null ? recentFormValue : "")
                    .setImageDrawable(R.id.league_table_status, getStatusImageRes(standing.getStatus()))
            ;

            GlideApp.with(mContext)
                    .load(standing.getTeamEmblemUrl())
                    .centerCrop()
                    //.transform(new CropCircleTransformation(mContext))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    //.placeholder(R.drawable.ic_empty_emblem_vector_1)
                    .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_empty_emblem_vector_1))
                    .into((ImageView)helper.getView(R.id.team_emblem));

        } else if ( helper.getItemViewType() == MultipleItem.ARENA_SCHEDULE) {

            MatchModel match = (MatchModel)item;
            helper.setText(R.id.hometeam_name, match.getHomeTeamName())
                    .setText(R.id.awayteam_name, match.getAwayTeamName())
                    //.setText(R.id.match_id, String.valueOf(match.getMatchId()))
                    .setText(R.id.match_time, match.getTime())
                    .setGone(R.id.match_time, !match.getStatus().equals("FT"))
                    .setText(R.id.match_score, String.valueOf(match.getHomeTeamScore()) + "   :   " + String.valueOf(match.getAwayTeamScore()))
                    .setGone(R.id.match_score, match.getStatus().equals("FT"))
            ;

            // --- Local team ---
            GlideApp.with(mContext)
                    .load(match.getHomeTeamEmblemUrl())
                    .centerCrop()
                    .transform(new CropCircleTransformation())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    //.placeholder(R.drawable.ic_empty_emblem_vector_1)
                    .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_empty_emblem_vector_1))
                    .into((ImageView)helper.getView(R.id.hometeam_image));

            // -- Visitor team ---
            GlideApp.with(mContext)
                    .load(match.getAwayTeamEmblemUrl())
                    .centerCrop()
                    .transform(new CropCircleTransformation())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    //.placeholder(R.drawable.ic_empty_emblem_vector_1)
                    .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_empty_emblem_vector_1))
                    .into((ImageView)helper.getView(R.id.awayteam_image));

        } else if ( helper.getItemViewType() == MultipleItem.ARENA_MATCH_HEADER ) {

            CompetitionMatchHeaderModel matchHeader = (CompetitionMatchHeaderModel)item;
            helper.setText(R.id.competition_match_header, matchHeader.getFixtureDate());
        }

    }

    /*---------------------------------------------------------------*/

    public void addMatchContainer(MatchContainerModel matchContainer) {

        if ( matchContainer != null ) {

            List<CompetitionModel> competitionList = matchContainer.getCompetitionList();
            if ( competitionList == null ) return;

            for ( int i =0 ; i < competitionList.size(); i++ ) {

                competitionList.get(i).setFixtureDate(matchContainer.getFixtureDate());
                matchContainer.addSubItem(competitionList.get(i));

                List<MatchModel> matchList = competitionList.get(i).getMatchList();
                if ( matchList == null ) continue;

                for ( int j = 0 ; j < matchList.size(); j++ ) {
                    competitionList.get(i).addSubItem(matchList.get(j));
                }

                //competitionList.get(i).addSubItem(new ScheduleFooterModel(competitionList.get(i).getCompetitionId()));
                addData(new CompetitionMatchHeaderModel(matchContainer.getFixtureDate()));
                addData(competitionList.get(i).getMatchList());
            }

            //addData(matchContainer);
        }
    }

    public void addMatchContainer(int position, MatchContainerModel matchContainer) {

        if ( matchContainer != null ) {

            List<CompetitionModel> competitionList = matchContainer.getCompetitionList();
            if ( competitionList == null ) return;

            for ( int i =0 ; i < competitionList.size(); i++ ) {

                competitionList.get(i).setFixtureDate(matchContainer.getFixtureDate());
                matchContainer.addSubItem(competitionList.get(i));

                List<MatchModel> matchList = competitionList.get(i).getMatchList();
                if ( matchList == null ) continue;

                for ( int j = 0 ; j < matchList.size(); j++ ) {
                    competitionList.get(i).addSubItem(matchList.get(j));
                }

                //competitionList.get(i).addSubItem(new ScheduleFooterModel(competitionList.get(i).getCompetitionId()));
                addData(position, competitionList.get(i).getMatchList());
                addData(position, new CompetitionMatchHeaderModel(matchContainer.getFixtureDate()));
            }

            //addData(matchContainer);
        }
    }

    private Drawable getStatusImageRes(String status) {

        if ( status.equals("up") ) {
            return DrawableHelper.withContext(mContext)
                    .withColor(R.color.md_blue_400)
                    .withDrawable(R.drawable.ic_keyboard_arrow_up)
                    .tint().get();
        } else if ( status.equals("down")) {
            return DrawableHelper.withContext(mContext)
                    .withColor(R.color.md_red_200)
                    .withDrawable(R.drawable.ic_keyboard_arrow_down)
                    .tint().get();
        } else {
            return DrawableHelper.withContext(mContext)
                    .withColor(R.color.md_grey_400)
                    .withDrawable(R.drawable.ic_no_change)
                    .tint().get();
        }
    }

    private String StringValueOf(int value) {

        try {
            return String.valueOf(value);
        } catch (Exception ex) {
            return "";
        }

    }
}

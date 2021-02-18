package com.ddastudio.hifivefootball_android.team_overview;

import android.content.Context;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamStatItemModel;

import java.util.List;

/**
 * Created by hongmac on 2017. 10. 30..
 */

public class TeamInfoRvAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public TeamInfoRvAdapter(List<MultiItemEntity> data) {
        super(data);

        addItemType(MultipleItem.TEAM_BASIC_INFO, R.layout.row_team_basic_info_item);
        addItemType(MultipleItem.TEAM_STATS, R.layout.row_team_stats_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        if ( helper.getItemViewType() == MultipleItem.TEAM_BASIC_INFO ) {
            TeamModel team = (TeamModel)item;
            helper.setText(R.id.team_country_city, team.getCountry() + " / " + team.getVenue_city())
                    .setText(R.id.venue_name, team.getVenue_name())
                    .setText(R.id.coach_name, team.getCoach_name());

        } else if ( helper.getItemViewType() == MultipleItem.TEAM_STATS ) {

            TeamStatItemModel stat = (TeamStatItemModel)item;
            helper.setText(R.id.team_stat_key, stat.getKey())
                    .setText(R.id.team_stat_value, stat.getValue());
        }
    }
}

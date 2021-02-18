package com.ddastudio.hifivefootball_android.player_overview;

import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.PlayerStatisticsModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;

import java.util.List;

/**
 * Created by hongmac on 2017. 10. 30..
 */

public class PlayerInfoRvAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public PlayerInfoRvAdapter(List<MultiItemEntity> data) {
        super(data);

        addItemType(MultipleItem.PLAYER_BASIC_INFO, R.layout.row_player_detail_info_item);
        addItemType(MultipleItem.PLAYER_STATS, R.layout.row_player_stats_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        if ( helper.getItemViewType() == MultipleItem.PLAYER_BASIC_INFO ) {

            PlayerModel playerInfo = (PlayerModel)item;
            helper.setText(R.id.player_birth_age, playerInfo.getBirthDateString())
                    .setText(R.id.player_height_weight, playerInfo.getHeight() + ", " + playerInfo.getWeight())
                    .setText(R.id.player_position, playerInfo.getPosition())
                    .setText(R.id.player_nationality, playerInfo.getNationality() + " / " + playerInfo.getBirthplace());

        } else if ( helper.getItemViewType() == MultipleItem.PLAYER_STATS ) {

            PlayerStatisticsModel stats = (PlayerStatisticsModel)item;

            helper.setText(R.id.competition_info, stats.getTitle())
                    .setText(R.id.player_goals, stats.getGoals())
                    .setText(R.id.player_appearances, stats.getAppearances())
                    .setText(R.id.player_lineups, stats.getLineups())
                    .setText(R.id.player_substitue_in_out, stats.getSubstitute_in() + " / " + stats.getSubstitute_out())
                    .setText(R.id.player_substitute_on_bench, stats.getSubstituteOnBench())
                    .setText(R.id.player_redcards, stats.getRedcards())
                    .setText(R.id.player_yellowcards, stats.getYellowcards());

            GlideApp.with(mContext)
                    .load(stats.getCompetitionImageUrl())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_empty_emblem_vector_2))
                    .into((ImageView)helper.getView(R.id.competition_emblem));
        }
    }
}

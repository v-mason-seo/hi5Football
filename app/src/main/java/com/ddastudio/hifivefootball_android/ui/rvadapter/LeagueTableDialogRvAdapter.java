package com.ddastudio.hifivefootball_android.ui.rvadapter;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.PlayerStatsPairModel;
import com.ddastudio.hifivefootball_android.data.model.community.SimpleSectionHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.StandingModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.utils.DrawableHelper;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by hongmac on 2017. 10. 25..
 */

public class LeagueTableDialogRvAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    private static SparseBooleanArray sSelectedItems;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public LeagueTableDialogRvAdapter(List<MultiItemEntity> data) {
        super(data);

        addItemType(ViewType.SIMPLE_SECTION_HEADER, R.layout.row_simple_section_item);
        addItemType(MultipleItem.BOTTOM_STANDING, R.layout.row_league_table_item);
        addItemType(MultipleItem.BOTTOM_STANDING_HEADER, R.layout.row_league_table_header_item);

        addItemType(MultipleItem.BOTTOM_MATCH_PLAYER_STATS, R.layout.row_match_player_stats_item);
        addItemType(MultipleItem.BOTTOM_TEAM_LIST, R.layout.row_search_team);
        addItemType(MultipleItem.BOTTOM_PLAYER_LIST, R.layout.row_search_player);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        if ( helper.getItemViewType() == MultipleItem.BOTTOM_STANDING) {

            StandingModel standing = (StandingModel)item;
            helper.setText(R.id.league_table_position, StringValueOf(standing.getPosition()))
                    .setText(R.id.league_table_team_name, standing.getTeamName())
                    .setText(R.id.league_table_gp, StringValueOf(standing.getOverallGp()))
                    .setText(R.id.league_table_w, StringValueOf(standing.getOverallW()))
                    .setText(R.id.league_table_d, StringValueOf(standing.getOverallD()))
                    .setText(R.id.league_table_l, StringValueOf(standing.getOverallL()))
                    .setText(R.id.league_table_gs, StringValueOf(standing.getOverallGs()))
                    .setText(R.id.league_table_ga, StringValueOf(standing.getOverallGa()))
                    .setText(R.id.league_table_point, StringValueOf(standing.getPoints()))
                    .setBackgroundColor(R.id.league_table_container, standing.isSelected()
                            ? ContextCompat.getColor(mContext, R.color.md_amber_100)
                            : ContextCompat.getColor(mContext, R.color.transparent))
                    .setImageDrawable(R.id.league_table_status, getStatusImageRes(standing.getStatus()))
            ;

            GlideApp.with(mContext)
                    .asBitmap()
                    .load(standing.getTeamEmblemUrl())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_empty_emblem_vector_1))
                    .into((ImageView)helper.getView(R.id.team_emblem));

        } else if ( helper.getItemViewType() == MultipleItem.BOTTOM_MATCH_PLAYER_STATS) {

            PlayerStatsPairModel stats = (PlayerStatsPairModel)item;
            helper.setText(R.id.stat_name, stats.getName())
                    .setText(R.id.stat_value, stats.getValue())
                    .setImageResource(R.id.stat_image, stats.getRes());
        } else if ( helper.getItemViewType() == MultipleItem.BOTTOM_TEAM_LIST) {

            TeamModel team = (TeamModel)item;
            helper.setText(R.id.team_name, team.getTeamName())
                    .setBackgroundRes(R.id.team_container, team.isSelected() ? R.color.md_red_200 : R.color.md_grey_50);

            helper.getView(R.id.team_container).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    team.setSelected(!team.isSelected());
                    notifyItemChanged(helper.getAdapterPosition());
                }
            });

        } else if ( helper.getItemViewType() == MultipleItem.BOTTOM_PLAYER_LIST) {

            PlayerModel player = (PlayerModel)item;
            helper.setText(R.id.player_name, player.getPlayerName())
                    .setBackgroundRes(R.id.player_container, player.isSelected() ? R.color.md_red_200 : R.color.md_grey_50)
            ;

            helper.getView(R.id.player_container).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    player.setSelected(!player.isSelected());
                    notifyItemChanged(helper.getAdapterPosition());
                }
            });
        } else if ( helper.getItemViewType() == ViewType.SIMPLE_SECTION_HEADER ) {
            bindSimpleSection(helper, (SimpleSectionHeaderModel)item);
        }
    }

    private void bindSimpleSection(BaseViewHolder helper, SimpleSectionHeaderModel header) {

        helper.setText(R.id.tv_header, header.getTitle());
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

    /*--------------------------------------------------------*/

    public List<PlayerModel> getSelectedPlayerData() {

        List<PlayerModel> selectedPlayerList = new ArrayList<>();
        List<MultiItemEntity> list = getData();

        for (int i = 0; i < list.size(); i++) {

            PlayerModel playerModel = (PlayerModel)list.get(i);

            if ( playerModel.isSelected() == true ) {
                selectedPlayerList.add(playerModel);
            }
        }

        return selectedPlayerList;
    }

    public List<TeamModel> getSelectedTeamData() {

        List<TeamModel> selectedTeamList = new ArrayList<>();
        List<MultiItemEntity> list = getData();

        for (int i = 0; i < list.size(); i++) {

            TeamModel teamModel = (TeamModel)list.get(i);

            if ( teamModel.isSelected() == true ) {
                Timber.d("Selected item event");
                selectedTeamList.add(teamModel);
            }
        }

        return selectedTeamList;
    }

}

package com.ddastudio.hifivefootball_android.ui.rvadapter;

import android.graphics.drawable.Drawable;
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
import com.ddastudio.hifivefootball_android.data.model.community.SimpleSectionHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.StandingModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.utils.DrawableHelper;

import java.util.List;

/**
 * Created by hongmac on 2018. 1. 11..
 */

public class LeagueTableRvAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>  {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public LeagueTableRvAdapter(List<MultiItemEntity> data) {
        super(data);

        addItemType(ViewType.SIMPLE_SECTION_HEADER, R.layout.row_simple_section_item);
        addItemType(MultipleItem.BOTTOM_STANDING, R.layout.row_league_table_item);
        addItemType(MultipleItem.BOTTOM_STANDING_HEADER, R.layout.row_league_table_header_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        switch (helper.getItemViewType()) {
            case MultipleItem.BOTTOM_STANDING:
                bindLeagueTable(helper, (StandingModel)item);
                break;
            case ViewType.SIMPLE_SECTION_HEADER:
                bindSimpleSection(helper, (SimpleSectionHeaderModel)item);
                break;
        }
    }

    private void bindSimpleSection(BaseViewHolder helper, SimpleSectionHeaderModel header) {

        helper.setText(R.id.tv_header, header.getTitle());
    }

    private void bindLeagueTable(BaseViewHolder helper, StandingModel standing) {
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
                //.transform(new CropCircleTransformation(mContext))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_empty_emblem_vector_1)
                .into((ImageView)helper.getView(R.id.team_emblem));
    }

    private String StringValueOf(int value) {

        try {
            return String.valueOf(value);
        } catch (Exception ex) {
            return "";
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
}

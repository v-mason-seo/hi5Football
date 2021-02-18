package com.ddastudio.hifivefootball_android.team_list;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.google.android.flexbox.FlexboxLayout;

import java.util.List;

import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by hongmac on 2018. 1. 26..
 */

public class TeamListRvAdapter extends BaseMultiItemQuickAdapter<TeamModel, BaseViewHolder> {

    final public static int BASIC = 0;
    final public static int GRID = 1;
    int viewMode = BASIC;
    ChipCloudConfig config;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public TeamListRvAdapter(List<TeamModel> data) {
        super(data);

        config = new ChipCloudConfig()
                .selectMode(ChipCloud.SelectMode.none)
                .checkedChipColor(Color.parseColor("#ddaa00"))
                .checkedTextColor(Color.parseColor("#ffffff"))
                .uncheckedChipColor(Color.parseColor("#efefef"))
                .uncheckedTextColor(Color.parseColor("#666666"))
                .useInsetPadding(true)
                ;

        if ( viewMode == BASIC ) {
            addItemType(MultipleItem.TEAM_BASIC_INFO, R.layout.row_team_item);;
        } else if ( viewMode == GRID ) {
            addItemType(MultipleItem.TEAM_BASIC_INFO, R.layout.row_team_grid_item);;
        }
    }

    public void onNewData(List<TeamModel> items) {

        final TeamListDiffCallback diffCallback = new TeamListDiffCallback(getData(), items);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.getData().clear();
        this.getData().addAll(items);
        diffResult.dispatchUpdatesTo(this);
    }

    public int getViewMode() {
        return viewMode;
    }

    public void setViewMode(int viewMode) {
        this.viewMode = viewMode;

        if ( viewMode == BASIC ) {
            addItemType(MultipleItem.TEAM_BASIC_INFO, R.layout.row_team_item);
        } else if ( viewMode == GRID ) {
            addItemType(MultipleItem.TEAM_BASIC_INFO, R.layout.row_team_grid_item);
        }

    }

    /*------------------------------------------------------------------------*/

    @Override
    protected void convert(BaseViewHolder helper, TeamModel item) {
        switch (helper.getItemViewType()) {
            case MultipleItem.TEAM_BASIC_INFO:
                if ( viewMode == BASIC ) {
                    bindTeamInfo(helper, item);
                } else if ( viewMode == GRID ) {
                    bindTeamGridInfo(helper, item);
                }
                break;
        }
    }

    private void bindTeamInfo(BaseViewHolder helper, TeamModel team) {

        FlexboxLayout fbTagList = helper.getView(R.id.fb_tags);

        helper.addOnClickListener(R.id.iv_more_item);

        helper.setText(R.id.tv_team_name, team.getTeamName())
                .setText(R.id.tv_country, team.getCountry())
                .setText(R.id.tv_team_hits, team.getHitString());

        fbTagList.removeAllViews();
        ChipCloud chipCloud = new ChipCloud(mContext, fbTagList, config);

        if ( team.getTagList() != null && team.getTagList().size() > 0 ) {
            chipCloud.addChips(team.getTagList());
        }

        GlideApp.with(mContext)
                .load(team.getEmblemUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.placeholder(R.drawable.ic_empty_emblem_vector_1)
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_empty_emblem_vector_1))
                .into((ImageView) helper.getView(R.id.iv_emblem));
    }

    private void bindTeamGridInfo(BaseViewHolder helper, TeamModel team) {

        helper.addOnClickListener(R.id.iv_more_item);

        helper.setText(R.id.tv_team_name, team.getTeamName())
                ;


        GlideApp.with(mContext)
                .load(team.getEmblemUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.placeholder(R.drawable.ic_empty_emblem_vector_1)
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_empty_emblem_vector_1))
                .into((ImageView) helper.getView(R.id.iv_emblem));
    }
}

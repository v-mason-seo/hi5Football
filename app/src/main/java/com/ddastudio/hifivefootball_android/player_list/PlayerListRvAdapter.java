package com.ddastudio.hifivefootball_android.player_list;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.google.android.flexbox.FlexboxLayout;

import java.util.List;

import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by hongmac on 2018. 1. 15..
 */

public class PlayerListRvAdapter extends BaseMultiItemQuickAdapter<PlayerModel, BaseViewHolder>  {

    ChipCloudConfig config;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public PlayerListRvAdapter(List<PlayerModel> data) {
        super(data);

        config = new ChipCloudConfig()
                .selectMode(ChipCloud.SelectMode.none)
                .checkedChipColor(Color.parseColor("#ddaa00"))
                .checkedTextColor(Color.parseColor("#ffffff"))
                .uncheckedChipColor(Color.parseColor("#efefef"))
                .uncheckedTextColor(Color.parseColor("#666666"))
                .useInsetPadding(true)
                ;

        addItemType(ViewType.PLAYER_INFO, R.layout.row_player_item);
    }

    public void onNewData(List<PlayerModel> items) {

        final PlayerListDiffCallback diffCallback = new PlayerListDiffCallback(getData(), items);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.getData().clear();
        this.getData().addAll(items);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    protected void convert(BaseViewHolder helper, PlayerModel item) {

        switch (helper.getItemViewType()) {
            case ViewType.PLAYER_INFO:
                bindPlayerInfo(helper, item);
                break;
        }
    }

    private void bindPlayerInfo(BaseViewHolder helper, PlayerModel player) {

        helper.addOnClickListener(R.id.comment_box)
                .addOnClickListener(R.id.iv_comment_icon)
                .addOnClickListener(R.id.iv_more_item);

        helper.setText(R.id.tv_player_name, player.getPlayerName())
                .setText(R.id.tv_team_name, player.getTeamName())
                .setText(R.id.tv_player_position, player.getPos())
                .setText(R.id.tv_player_nationality, player.getNationality())
                .setText(R.id.tv_player_hits, "HIT : " + player.getCommentCount())
                .setText(R.id.tv_comment_count, "0")
        ;

        FlexboxLayout fbTagList = helper.getView(R.id.fb_tags);
        fbTagList.removeAllViews();
        ChipCloud chipCloud = new ChipCloud(mContext, fbTagList, config);

        if ( player.getTagList() != null && player.getTagList().size() > 0 ) {
            chipCloud.addChips(player.getTagList());
        }

        GlideApp.with(mContext)
                .load(player.getPlayerLargeImageUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.placeholder(R.drawable.ic_person)
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_person))
                .into((ImageView) helper.getView(R.id.tv_player_avatar));

        GlideApp.with(mContext)
                .load(player.getSmallEmblemUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.placeholder(R.drawable.ic_empty_emblem_vector_1)
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_empty_emblem_vector_1))
                .into((ImageView) helper.getView(R.id.iv_emblem));
    }
}

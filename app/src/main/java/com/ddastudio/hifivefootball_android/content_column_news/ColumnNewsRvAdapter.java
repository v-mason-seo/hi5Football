package com.ddastudio.hifivefootball_android.content_column_news;

import android.graphics.Color;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.EntityType;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.StreamViewerModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.google.android.flexbox.FlexboxLayout;

import java.util.List;

import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by hongmac on 2017. 11. 30..
 */

public class ColumnNewsRvAdapter extends BaseMultiItemQuickAdapter<ContentHeaderModel, BaseViewHolder> {

    ChipCloudConfig config;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ColumnNewsRvAdapter(List<ContentHeaderModel> data) {
        super(data);

        addItemType(EntityType.POSTS_COLUMN, R.layout.row_contents_column_list_item);
        addItemType(EntityType.POSTS_HUMOR, R.layout.row_contents_humor_list_item);
        addItemType(EntityType.BOARD_FOOTBALL_NEWS, R.layout.row_content_small_preview_single_item);

        config = new ChipCloudConfig()
                .selectMode(ChipCloud.SelectMode.none)
                .checkedChipColor(Color.parseColor("#ddaa00"))
                .checkedTextColor(Color.parseColor("#ffffff"))
                .uncheckedChipColor(Color.parseColor("#efefef"))
                .uncheckedTextColor(Color.parseColor("#666666"))
                .useInsetPadding(true)
        ;
    }

    @Override
    protected void convert(BaseViewHolder helper, ContentHeaderModel item) {

        if ( helper.getItemViewType() == EntityType.POSTS_COLUMN) {

            // Click
            helper.addOnClickListener(R.id.iv_hifive)
                    .addOnClickListener(R.id.iv_comment);

            // Long Click
            helper.addOnLongClickListener(R.id.iv_hifive)
                    .addOnLongClickListener(R.id.iv_comment);

            helper.setText(R.id.column_title, item.getTitle())
                    .setText(R.id.column_preview, item.getPreview())
                    .setText(R.id.tv_info, item.getCreatedString())
                    .setText(R.id.column_comment_count, item.getCommentString())
                    .setText(R.id.column_hifive_count, item.getLikerString())
            .setText(R.id.tv_nickname, item.getNickNameAndUserName())
            ;

            if ( item.getImgs() != null
                    && item.getImgs().size() > 0
                    && item.getImgs().get(0).getStreamType() == StreamViewerModel.IMAGE ) {

                GlideApp.with(mContext)
                        .load(item.getImgs().get(0).getStreamUrl())
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_face)
                        .into((ImageView)helper.getView(R.id.column_image));
            }

            GlideApp.with(mContext)
                    .load(item.getUser().getAvatarUrl())
                    .centerCrop()
                    .transform(new CropCircleTransformation())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_face)
                    .into((ImageView)helper.getView(R.id.avatar));

        } else if (helper.getItemViewType() == EntityType.POSTS_HUMOR) {
            helper.addOnClickListener(R.id.humor_comment_container);

            helper.setText(R.id.humor_title, item.getTitle())
                    .setText(R.id.humor_preview, item.getPreview())
                    .setText(R.id.humor_reg_date, item.getCreatedString())
                    .setText(R.id.humor_comment_count, item.getCommentString())
            ;
        } else if ( helper.getItemViewType() == EntityType.BOARD_FOOTBALL_NEWS) {

            FlexboxLayout fbTagList = helper.getView(R.id.fb_tags);
            fbTagList.removeAllViews();
            ChipCloud chipCloud = new ChipCloud(mContext, fbTagList, config);

            helper.setText(R.id.tv_title, item.getTitle())
                    .setText(R.id.humor_preview, item.getPreview())
                    .setText(R.id.humor_reg_date, item.getCreatedString())
                    .setText(R.id.humor_comment_count, item.getCommentString())
            ;

            if ( item.getImgs() != null
                    && item.getImgs().size() > 0
                    && item.getImgs().get(0).getStreamType() == StreamViewerModel.IMAGE ) {

                GlideApp.with(mContext)
                        .load(item.getImgs().get(0).getStreamUrl())
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_face)
                        .into((ImageView)helper.getView(R.id.iv_news));
            }

            if ( item.getTags() != null && item.getTags().size() > 0 ) {
                chipCloud.addChips(item.getTags());
            }
        }
    }
}

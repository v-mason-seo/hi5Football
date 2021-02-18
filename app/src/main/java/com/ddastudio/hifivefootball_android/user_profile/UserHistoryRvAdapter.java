package com.ddastudio.hifivefootball_android.user_profile;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.community.UserCommentModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.utils.ColorGenerator;

import java.util.List;

import cn.nekocode.badge.BadgeDrawable;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by hongmac on 2017. 9. 20..
 */

public class UserHistoryRvAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public UserHistoryRvAdapter(List<MultiItemEntity> data) {
        super(data);

        addItemType(MultipleItem.USER_CONTENT, R.layout.row_my_content_list_item);
        addItemType(MultipleItem.USER_COMMENT, R.layout.row_my_comment_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        switch (helper.getItemViewType()) {
            case MultipleItem.USER_CONTENT:
                ContentHeaderModel contentModel = (ContentHeaderModel)item;
                bindContentListData(helper, contentModel);
                break;

            case MultipleItem.USER_COMMENT:
                UserCommentModel commentModel = (UserCommentModel)item;
                bindCommentData(helper, commentModel);
                break;
        }
    }

    private void bindContentListData(BaseViewHolder helper, ContentHeaderModel item) {

        ImageView ivComment = helper.getView(R.id.comment_icon);
        TextView boardTag = helper.getView(R.id.board_tag);

        TextDrawable drawable2 = TextDrawable.builder()
                .beginConfig()
                .fontSize(30)
                .endConfig()
                .buildRound(item.getBoardName(), ColorGenerator.MATERIAL.getColor(item.getBoardId()));

        helper.setText(R.id.contents_title, item.getTitle())
                .setText(R.id.contents_preview, item.getPreview())
                .setGone(R.id.contents_preview, !TextUtils.isEmpty(item.getPreview()))
                .setText(R.id.user_name, item.getUser().getUsername())
                .setText(R.id.reg_date, item.getCreatedString())
                .setText(R.id.like_count, String.valueOf(item.getLikers()))
                .setText(R.id.comment_count, String.valueOf(item.getComments()))
                .setImageDrawable(R.id.iv_board, drawable2)
        ;

        final BadgeDrawable drawableBoardTag =
                new BadgeDrawable.Builder()
                        .type(BadgeDrawable.TYPE_ONLY_ONE_TEXT)
                        .badgeColor(ColorGenerator.MATERIAL.getColor(item.getBoardId()))
                        .textSize(sp2px(mContext, 10))
                        .text1(item.getBoardName())
                        .build();

        SpannableString spannableString = new SpannableString(drawableBoardTag.toSpannable());
        boardTag.setText(spannableString);

        // --- 댓글 ---
        if ( item.getComments() > 100 ) {
            ivComment.setColorFilter(ContextCompat.getColor(mContext, R.color.md_teal_600));
        } else if ( item.getComments() > 50 ) {
            ivComment.setColorFilter(ContextCompat.getColor(mContext, R.color.md_teal_400));
        } else if ( item.getComments() > 20 ) {
            ivComment.setColorFilter(ContextCompat.getColor(mContext, R.color.md_teal_200));
        } else if ( item.getComments() > 10 ) {
            ivComment.setColorFilter(ContextCompat.getColor(mContext, R.color.md_teal_100));
        } else {
            ivComment.setColorFilter(ContextCompat.getColor(mContext, R.color.md_grey_400));
        }

        // --- 사용자 프로필 ---
//        Glide.with(mContext)
//                .load(item.getUser().getAvatarUrl())
//                .asBitmap()
//                .centerCrop()
//                .transform(new CropCircleTransformation(mContext))
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .placeholder(CommonUtils.getDrawable(mContext, R.drawable.ic_face))
//                .into(avatar);
    }

    private void bindCommentData(BaseViewHolder helper, UserCommentModel item) {

        //FlipView flipView = helper.getView(R.id.flipview);
        //ImageView avtar = flipView.getFrontImageView();
        ImageView contentAvatar = helper.getView(R.id.content_avatar);

        helper.setText(R.id.comment_title, item.getContent())
        .setText(R.id.content_title, item.getTitle())
                .setText(R.id.content_user_name, item.getContentUserName())
                .setText(R.id.comment_reg_date, item.getCreated())
                .setText(R.id.comment_like_count, String.valueOf(item.getLikers()))
                ;

//        helper.setText(R.id.comment_title, commentModel.isDeleted() ? mContext.getResources().getString(R.string.deleted_comment_info)
//                : commentModel.getContent())
//                .setText(R.id.comment_user_name, commentModel.getUser().getUsername())
//                .setText(R.id.comment_reg_date, commentModel.getCreated())
//                .setText(R.id.comment_like_count, String.valueOf(commentModel.getLikers()))
//                .setGone(R.id.comment_reply, !commentModel.isDeleted())
//                .setVisible(R.id.iv_comment_delete, App.getInstance().isSameUser(commentModel.getUser().getUsername()))
//                .setVisible(R.id.iv_comment_edit, App.getInstance().isSameUser(commentModel.getUser().getUsername()))
//                .setTextColor(R.id.comment_user_name,App.getInstance().isSameUser(commentModel.getUser().getUsername())
//                        ?mContext.getResources().getColor(R.color.md_teal_400)
//                        :mContext.getResources().getColor(R.color.md_grey_800))
//                .setImageResource(R.id.comment_like, commentModel.isLiked() ? R.drawable.ic_like_color
//                        : R.drawable.ic_like)
//        ;
//
        // *** 사용자 프로필 ***
        GlideApp.with(mContext)
                .load(item.getContentUserAvatarUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.placeholder(R.drawable.ic_face)
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_face))
                .into(contentAvatar);
    }

    int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}

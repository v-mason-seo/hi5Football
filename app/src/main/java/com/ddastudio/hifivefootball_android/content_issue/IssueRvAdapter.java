package com.ddastudio.hifivefootball_android.content_issue;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.IssueModel;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.utils.ColorGenerator;

import java.util.List;

import cn.nekocode.badge.BadgeDrawable;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by hongmac on 2017. 9. 26..
 */

public class IssueRvAdapter extends BaseMultiItemQuickAdapter<IssueModel, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public IssueRvAdapter(List<IssueModel> data) {
        super(data);

        addItemType(MultipleItem.ISSUE_TYPE_OPEN, R.layout.row_issue_open_item);
        addItemType(MultipleItem.ISSUE_TYPE_CLOSE, R.layout.row_issue_close_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, IssueModel item) {

        TextView tagBox = helper.getView(R.id.issue_tag);

        helper.setText(R.id.issue_title, item.getTitle())
        //.setText(R.id.issue_id, "#" + item.getIssueId())
        .setText(R.id.issue_date, item.getIssueInfo());


        // --- Platform ---
        CharSequence charSequence;
        final BadgeDrawable drawablePlatform =
                new BadgeDrawable.Builder()
                        .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
                        .badgeColor(0xff336699)
                        .text1("#" + item.getIssueId())
                        .text2(item.getPlatformName())
                        .padding(sp2px(mContext, 4), sp2px(mContext, 2), sp2px(mContext, 4), sp2px(mContext, 2), sp2px(mContext, 4))
                        //.strokeWidth(dp2px(1))
                        .build();

        charSequence = drawablePlatform.toSpannable();

        // --- 태그 ---
        List<String> tags = item.getTags();
        if ( tags != null && tags.size() > 0 ) {

            for ( int i = 0; i < tags.size(); i++ ) {

                final BadgeDrawable drawable =
                        new BadgeDrawable.Builder()
                                .type(BadgeDrawable.TYPE_ONLY_ONE_TEXT)
                                .badgeColor(ColorGenerator.MATERIAL.getColor(tags.get(i)))
                                .text1(tags.get(i))
                                .build();

                charSequence = TextUtils.concat(charSequence, "  ", drawable.toSpannable());
            }
        }

        SpannableString spannableString = new SpannableString(charSequence);
        tagBox.setText(spannableString);


        // --- 사용자 프로필 ---
        GlideApp.with(mContext)
                .asBitmap()
                .load(item.getAvatarUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.placeholder(R.drawable.ic_face)
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_face))
                .into((ImageView)helper.getView(R.id.issue_avatar));
    }

    int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}

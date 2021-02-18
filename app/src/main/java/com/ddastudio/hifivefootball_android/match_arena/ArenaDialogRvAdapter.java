package com.ddastudio.hifivefootball_android.match_arena;

import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by hongmac on 2018. 3. 21..
 */

public class ArenaDialogRvAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ArenaDialogRvAdapter(List<MultiItemEntity> data) {
        super(data);

        addItemType(MultipleItem.USER_TYPE, R.layout.row_user_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        switch (helper.getItemViewType()) {
            case MultipleItem.USER_TYPE:
                bindUser(helper, (UserModel)item);
                break;
        }
    }

    private void bindUser(BaseViewHolder helper, UserModel user) {

        helper.setText(R.id.user_name, user.getNickNameAndUserName())
                .setText(R.id.user_profile, user.getProfile())
                .setGone(R.id.user_profile, !TextUtils.isEmpty(user.getProfile()))
        ;

        // --- 사용자 프로필 ---
        GlideApp.with(mContext)
                .load(user.getAvatarUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_face)
                .into((ImageView)helper.getView(R.id.avatar));
    }
}

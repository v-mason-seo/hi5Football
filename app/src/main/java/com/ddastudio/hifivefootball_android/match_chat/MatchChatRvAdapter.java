package com.ddastudio.hifivefootball_android.match_chat;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class MatchChatRvAdapter extends BaseMultiItemQuickAdapter<MatchChatModel, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public MatchChatRvAdapter(List<MatchChatModel> data) {
        super(data);

        addItemType(5534, R.layout.row_match_chat_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MatchChatModel item) {
        bindChat(helper, item);
    }

    private void bindChat(BaseViewHolder helper, MatchChatModel item) {

        helper.setText(R.id.tv_chat_message, item.getChatMessage());

        if ( item.getUser() != null ) {
            helper.setText(R.id.tv_chat_nickname, item.getUser().getNickname());

            GlideApp.with(mContext)
                    .load(item.getAvatar())
                    .centerCrop()
                    .transform(new CropCircleTransformation())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_face)
                    .into((ImageView)helper.getView(R.id.iv_avatar));
        }
    }

}

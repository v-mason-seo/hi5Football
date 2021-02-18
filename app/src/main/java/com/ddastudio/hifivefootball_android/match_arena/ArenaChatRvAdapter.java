package com.ddastudio.hifivefootball_android.match_arena;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.model.arena_chat.ReactionModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by hongmac on 2018. 1. 26..
 */

public class ArenaChatRvAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ArenaChatRvAdapter(List<MultiItemEntity> data) {
        super(data);

        addItemType(ViewType.REACTION_BOT_MESSAGE, R.layout.row_arena_bot_message_item);

        addItemType(ViewType.REACTION_HOME_MESSAGE, R.layout.row_arena_home_message_item);
        addItemType(ViewType.REACTION_AWAY_MESSAGE, R.layout.row_arena_away_message_item);

        addItemType(ViewType.REACTION_HOME_IMAGE, R.layout.row_arena_home_image_item);
        addItemType(ViewType.REACTION_AWAY_IMAGE, R.layout.row_arena_away_image_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        switch (helper.getItemViewType()) {
            case ViewType.REACTION_HOME_MESSAGE:
            case ViewType.REACTION_AWAY_MESSAGE:
                bindMessage(helper, (ReactionModel)item);
                break;
            case ViewType.REACTION_BOT_MESSAGE:
                bindBotMessage(helper, (ReactionModel)item);
                break;

                /*-------------------------------------*/

//            case ViewType.REACTION_AWAY_MESSAGE:
//                break;
        }

//        if ( item instanceof ReactionModel) {
//            ReactionModel chat = (ReactionModel)item;
//
//            helper.setText(R.id.tv_message, chat.getMessage());
//        }
    }

    public void bindBotMessage(BaseViewHolder helper, ReactionModel reaction) {

        helper.setText(R.id.tv_bot_message, reaction.getBotMessage());
    }

    public void bindMessage(BaseViewHolder helper, ReactionModel reaction) {
        helper.setText(R.id.tv_message, reaction.getMessage())
                .setText(R.id.tv_nickname, reaction.getNickName())
        ;

        GlideApp.with(mContext)
                .load(reaction.getAvatar())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_face)
                .into((ImageView)helper.getView(R.id.iv_avatar));
    }
}

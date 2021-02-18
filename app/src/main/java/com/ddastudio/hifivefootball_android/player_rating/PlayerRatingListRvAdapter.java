package com.ddastudio.hifivefootball_android.player_rating;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingInfoModel;
import com.ddastudio.hifivefootball_android.utils.DrawableHelper;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import timber.log.Timber;

/**
 * Created by hongmac on 2018. 1. 5..
 */

public class PlayerRatingListRvAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    /*--------------------------------------------------------------------------------------------*/

    OnPlayerRatingBarChangeListener mPlayerRatingBarChangeListener;

    public interface OnPlayerRatingBarChangeListener {
        void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser, PlayerRatingInfoModel playerData, int position);
    }

    public void setOnPlayerRatingBarChangeListener(OnPlayerRatingBarChangeListener listener) {
        this.mPlayerRatingBarChangeListener = listener;
    }

    /*--------------------------------------------------------------------------------------------*/
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public PlayerRatingListRvAdapter(List<MultiItemEntity> data) {
        super(data);

        addItemType(ViewType.PLAYER_RATING_INFO, R.layout.row_player_rating_list_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        switch (helper.getItemViewType()) {
            case ViewType.PLAYER_RATING_INFO:

                bindPlayerRatingInfo(helper, (PlayerRatingInfoModel)item);
                break;
        }
    }

    private void bindPlayerRatingInfo(BaseViewHolder helper, PlayerRatingInfoModel playerData) {

        RatingBar rbPlayerRating = helper.getView(R.id.rb_player_rating);

        rbPlayerRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                if ( mPlayerRatingBarChangeListener != null ) {
                    mPlayerRatingBarChangeListener.onRatingChanged(ratingBar, rating, fromUser, playerData, helper.getAdapterPosition());
                }
            }
        });
    }


    private Drawable getSubsitutionsImageResource(BaseViewHolder helper, String substitution) {

        if (substitution != null && substitution.equals("off")) {
            return DrawableHelper.withContext(mContext)
                    .withColor(R.color.md_red_400)
                    .withDrawable(R.drawable.ic_substitution_out)
                    .tint().get();

        } else if (substitution != null && substitution.equals("on")) {
            return DrawableHelper.withContext(mContext)
                    .withColor(R.color.md_green_400)
                    .withDrawable(R.drawable.ic_substitution_in)
                    .tint().get();
        } else {
            return null;
        }
    }
}

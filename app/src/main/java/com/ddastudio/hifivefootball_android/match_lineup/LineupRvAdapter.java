package com.ddastudio.hifivefootball_android.match_lineup;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingInfoModel;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.LineupModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.ui.utils.ColorUtils;
import com.ddastudio.hifivefootball_android.utils.CommonUtils;
import com.ddastudio.hifivefootball_android.utils.DrawableHelper;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by hongmac on 2017. 10. 26..
 */

public class LineupRvAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public LineupRvAdapter(List<MultiItemEntity> data) {
        super(data);

        addItemType(ViewType.LINEUP, R.layout.row_lineup_item);
        addItemType(MultipleItem.MATCH_LINEUP, R.layout.row_player_rating_list_item);
    }

    //public LineupRvAdapter(@LayoutRes int layoutResId) {
//        super(layoutResId);
//    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity data) {

        switch (helper.getItemViewType()) {
            case ViewType.LINEUP:
                bindLineup(helper, (PlayerModel)data);
                break;
        }
    }

    private void bindLineup(BaseViewHolder helper, PlayerModel lineup) {

        helper.setText(R.id.tv_player_name, lineup.getBackNumber() + ". " +lineup.getPlayerName())
                .setText(R.id.tv_player_position, lineup.getPos())
                .setTextColor(R.id.tv_player_name, ColorUtils.getRatingTextColor(mContext, lineup.getRating()))
                .setText(R.id.tv_player_rating, lineup.getRatingString())
                .setBackgroundColor(R.id.tv_player_rating, ColorUtils.getRatingBackgroundColor(mContext, lineup.getRating()))
                .setText(R.id.tv_team_name, lineup.getTeamName())
                .setGone(R.id.iv_goal, lineup.getStats().hasGoals())
                .setGone(R.id.iv_yellow_card1, lineup.getStats().hasYellowCards())
                .setGone(R.id.iv_red_card, lineup.getStats().hasRedCards())
                .setGone(R.id.tv_player_subs_minute, lineup.getSubsMinute() != null )
                .setText(R.id.tv_player_subs_minute, lineup.getSubsMinute())
                .setGone(R.id.iv_player_substitutions, lineup.hasSubstitutions())
                .setImageDrawable(R.id.iv_player_substitutions, getSubsitutionsImageResource(lineup.getSubstitutions()))
        ;

        // --- 플레이어 프로필 ---
        GlideApp.with(mContext)
                .load(lineup.getPlayerLargeImageUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_person_grey_vector)
                .into((ImageView)helper.getView(R.id.iv_avatar));
    }

    private Drawable getSubsitutionsImageResource(String substitution) {

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

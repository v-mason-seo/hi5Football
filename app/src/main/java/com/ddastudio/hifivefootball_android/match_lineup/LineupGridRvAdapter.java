package com.ddastudio.hifivefootball_android.match_lineup;

import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.TextureView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.LineupModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.ui.utils.ColorUtils;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by hongmac on 2018. 1. 17..
 */

public class LineupGridRvAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public LineupGridRvAdapter(List<MultiItemEntity> data) {
        super(data);

        addItemType(MultipleItem.MATCH_LINEUP, R.layout.row_player_formation_item);
        addItemType(ViewType.PLAYER, R.layout.row_player_formation_item);
        addItemType(ViewType.EMPTY_TYPE, R.layout.row_empty_divider_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        switch (helper.getItemViewType()) {
            case ViewType.PLAYER:
                bindLineup(helper, (PlayerModel)item);
                break;
        }
    }

    private void bindLineup(BaseViewHolder helper, PlayerModel lineup) {



        helper.setText(R.id.tv_player_position, lineup.getPos())
                .setText(R.id.tv_player_name, getPlayerInfo(lineup))
                .setText(R.id.tv_player_rating, getRatingSpannable(lineup))
                .setGone(R.id.iv_goal, lineup.getStats().hasGoals())
                .setGone(R.id.iv_yellow_card1, lineup.getStats().hasYellowCards())
                .setGone(R.id.iv_red_card, lineup.getStats().hasRedCards())
        ;

        GlideApp.with(mContext)
                .load(lineup.getPlayerLargeImageUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_person))
                .into((ImageView) helper.getView(R.id.player_image));
    }

    /**
     * 공식평점, 유저 평점 스패너블 반환
     * @param lineup
     * @return
     */
    private Spannable getRatingSpannable(PlayerModel lineup) {

        // spannable start index
        int start;
        // spannable end index
        int end;
        // 평점
        SpannableStringBuilder ssbRating = new SpannableStringBuilder();

        // 1.공식평점
        start = 0;
        ssbRating.append(" 전 " + lineup.getRatingString() + " ");
        end = ssbRating.length();
        ssbRating.setSpan(new BackgroundColorSpan(ColorUtils.getRatingBackgroundColor(mContext, lineup.getRating())), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 2.공백
        ssbRating.append("  ");

        // 3.유저평점
        start = ssbRating.length();
        ssbRating.append(" 유 " + lineup.getUserRating() + " ");
        end = ssbRating.length();
        ssbRating.setSpan(new BackgroundColorSpan(ColorUtils.getRatingBackgroundColor(mContext, lineup.getUserRating())), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return ssbRating;
    }

    /**
     * 등번호, 선수이름, 교체시간 스패너블 반환
     * @param lineup
     * @return
     */
    private Spannable getPlayerInfo(PlayerModel lineup) {

        // spannable start index
        int start;
        // spannable end index
        int end;
        // 등번호-선수이름-교체시간
        SpannableStringBuilder ssbPlayer = new SpannableStringBuilder();

        // 1. 등번호 - 선수이름
        ssbPlayer.append(lineup.getBackNumber() + ". " + lineup.getPlayerName() + " ");

        // 2. 교체시간
        if (!TextUtils.isEmpty(lineup.getSubsMinute())) {
            start = ssbPlayer.length();
            ssbPlayer.append(lineup.getSubsMinute());
            end = ssbPlayer.length();
            ssbPlayer.setSpan(new ForegroundColorSpan(ColorUtils.getSubMinuteTextColor(mContext, lineup.getSubstitutions())), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return ssbPlayer;
    }
}

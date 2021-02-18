package com.ddastudio.hifivefootball_android.match_lineup;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
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
import com.ddastudio.hifivefootball_android.data.model.footballdata.SubLineupTeamSectionModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.ui.utils.ColorUtils;
import com.ddastudio.hifivefootball_android.utils.DrawableHelper;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by hongmac on 2018. 2. 7..
 * 후보선수 라인업 아답터 - LineupGridFragment에서 사용함.
 */

public class SubLineupRvAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public SubLineupRvAdapter(List<MultiItemEntity> data) {
        super(data);

        addItemType(ViewType.PLAYER, R.layout.row_sub_lineup_item);
        addItemType(ViewType.SUB_LINEUP_TEAM_SECTION, R.layout.row_sub_lineup_team_section_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        switch (helper.getItemViewType()) {
            case ViewType.PLAYER:
                bindSubLineup(helper, (PlayerModel)item);
                break;
            case ViewType.SUB_LINEUP_TEAM_SECTION:
                bindSection(helper, (SubLineupTeamSectionModel)item);
                break;
        }
    }

    private void bindSubLineup(BaseViewHolder helper, PlayerModel lineup) {

        helper.addOnClickListener(R.id.player_image);

        helper.setText(R.id.tv_player_name, lineup.getBackNumber() + ". " + lineup.getPlayerName())
                .setText(R.id.tv_player_rating, getRatingSpannable(lineup))
                .setGone(R.id.iv_goal, lineup.getStats().hasGoals())
                .setGone(R.id.iv_yellow_card1, lineup.getStats().hasYellowCards())
                .setGone(R.id.iv_red_card, lineup.getStats().hasRedCards())
                .setGone(R.id.tv_player_subs_minute, lineup.getSubsMinute() != null )
                .setText(R.id.tv_player_subs_minute, lineup.getSubsMinute())
                .setGone(R.id.iv_player_substitutions, lineup.hasSubstitutions())
                .setImageDrawable(R.id.iv_player_substitutions, getSubsitutionsImageResource(lineup.getSubstitutions()))
        ;

        GlideApp.with(mContext)
                .load(lineup.getPlayerLargeImageUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_person_grey_vector)
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

    private void bindSection(BaseViewHolder helper, SubLineupTeamSectionModel item) {

        helper.setText(R.id.tv_section_title, item.getTitle())
        .setBackgroundColor(R.id.fl_background, item.getColor());

    }

    /**
     * 교체 이미지 리소스 가져오기
     * @param substitution
     * @return
     */
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

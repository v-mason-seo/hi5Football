package com.ddastudio.hifivefootball_android.ui.rvadapter;

import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.football.CompModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchDateModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by hongmac on 2017. 12. 30..
 */

public class OrderRvAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public OrderRvAdapter(List<MultiItemEntity> data) {
        super(data);

        addItemType(ViewType.COMPETITION, R.layout.row_competition_small_item);
        addItemType(MultipleItem.ARENA_SCHEDULE, R.layout.row_simple_match_item);
        addItemType(ViewType.SIMPLE_MATCH_DATE, R.layout.row_simple_match_date_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        if ( helper.getItemViewType() == MultipleItem.ARENA_SCHEDULE ) {

            helper.addOnClickListener(R.id.hometeam_image)
                    .addOnClickListener(R.id.awayteam_image);

            MatchModel match = (MatchModel)item;
            helper.setText(R.id.hometeam_name, match.getHomeTeamName())
                    .setText(R.id.awayteam_name, match.getAwayTeamName())
                    .setText(R.id.match_time, match.getTime())
                    .setGone(R.id.match_time, !match.getStatus().equals("FT"))
                    .setText(R.id.match_score, String.valueOf(match.getHomeTeamScore()) + "   :   " + String.valueOf(match.getAwayTeamScore()))
                    .setGone(R.id.match_score, match.getStatus().equals("FT"))
            ;

            // --- Local team ---
            GlideApp.with(mContext)
                    .asBitmap()
                    .load(match.getHomeTeamEmblemUrl())
                    .centerCrop()
                    .transform(new CropCircleTransformation())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_empty_emblem_vector_1)
                    .into((ImageView)helper.getView(R.id.hometeam_image));

            // -- Visitor team ---
            GlideApp.with(mContext)
                    .asBitmap()
                    .load(match.getAwayTeamEmblemUrl())
                    .centerCrop()
                    .transform(new CropCircleTransformation())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_empty_emblem_vector_1)
                    .into((ImageView)helper.getView(R.id.awayteam_image));

        } else if ( helper.getItemViewType() == ViewType.COMPETITION ) {
            CompModel competition = (CompModel) item;

            helper.addOnClickListener(R.id.competition_container);

            helper.setText(R.id.competition_name, competition.getCompetitionName());

            GlideApp.with(mContext)
                    .load(competition.getCompetitionImageUrl())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_empty_emblem_vector_2)
                    .into((ImageView)helper.getView(R.id.iv_competition));

        } else if ( helper.getItemViewType() == ViewType.SIMPLE_MATCH_DATE ) {

            MatchDateModel matchDate = (MatchDateModel)item;

            helper.setText(R.id.tv_title, (matchDate.getTitle()));
        }


    }
}

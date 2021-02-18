package com.ddastudio.hifivefootball_android.match_event;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.footballdata.MatchEventModel;
import com.ddastudio.hifivefootball_android.utils.DrawableHelper;

import java.util.List;

/**
 * Created by hongmac on 2017. 10. 27..
 */

public class MatchTimeLineRvAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public MatchTimeLineRvAdapter(List<MultiItemEntity> data) {
        super(data);

        addItemType(MultipleItem.MATCH_EVENTS_LOCAL_TEAM, R.layout.row_match_event_local_team);
        addItemType(MultipleItem.MATCH_EVENTS_VISITOR_TEAM, R.layout.row_match_event_visitor_team);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        MatchEventModel matchEvent = (MatchEventModel)item;

        helper.setText(R.id.event_player_name, matchEvent.getPlayerName())
                .setGone(R.id.tv_extra_min, !TextUtils.isEmpty(matchEvent.getExtraMin()))
                .setText(R.id.tv_extra_min, matchEvent.getExtraMin())
                .setText(R.id.tv_score, matchEvent.getScoreResult())
                .setText(R.id.event_assist_player_name, matchEvent.getAssistPlayerName2())
                .setText(R.id.event_minutes, matchEvent.getMinute())
                .setImageResource(R.id.event_image, getEventImageResource(matchEvent.getEventType()))
            //.setImageDrawable(R.id.event_image, getEventImageDrawable(matchEvent.getEventType()))
        ;
    }

    private int getEventImageResource(String eventType) {

        if ( eventType.equals("redcard")) {
            return R.drawable.ic_red_card_vector;
        } else if ( eventType.equals("yellowcard") ) {
            return R.drawable.ic_yellow_card_vector;
        } else if ( eventType.equals("goal")) {
            return R.drawable.ic_soccer_ball_vector2;
        } else if ( eventType.equals("subst")) {
            return R.drawable.ic_compare_arrows;
        }

        return 0;
    }

    private Drawable getEventImageDrawable(String eventType) {

        if ( eventType.equals("redcard")) {
            return DrawableHelper.withContext(mContext)
                    .withColor(R.color.md_red_400)
                    .withDrawable(R.drawable.ic_card)
                    .tint().get();
        } else if ( eventType.equals("yellowcard") ) {
            return DrawableHelper.withContext(mContext)
                    //.withColor(R.color.md_yellow_500)
                    .withDrawable(R.drawable.ic_yellow_card_vector)
                    .tint().get();
        } else if ( eventType.equals("goal")) {
            return DrawableHelper.withContext(mContext)
                    //.withColor(R.color.md_grey_700)
                    .withDrawable(R.drawable.ic_soccer_ball_vector2)
                    .tint().get();
        } else if ( eventType.equals("subst")) {
            return DrawableHelper.withContext(mContext)
                    .withColor(R.color.md_green_600)
                    .withDrawable(R.drawable.ic_compare_arrows)
                    .tint().get();
        }

        return null;
    }
}

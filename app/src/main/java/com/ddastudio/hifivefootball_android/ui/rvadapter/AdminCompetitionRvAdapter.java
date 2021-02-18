package com.ddastudio.hifivefootball_android.ui.rvadapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.convert.CompetitionFDModel;

import java.util.List;

/**
 * Created by hongmac on 2017. 10. 15..
 */

public class AdminCompetitionRvAdapter extends BaseMultiItemQuickAdapter<CompetitionFDModel, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public AdminCompetitionRvAdapter(List<CompetitionFDModel> data) {
        super(data);

        addItemType(MultipleItem.ADMIN_COMPETITION, R.layout.row_admin_competition_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, CompetitionFDModel item) {

        helper.addOnClickListener(R.id.fixture_sync);

        helper.setText(R.id.competition_name, item.getCaption())
        .setText(R.id.competition_id, "[" + item.getId() +"]")
        .setText(R.id.competition_updated, item.getUpdatedString());
    }
}

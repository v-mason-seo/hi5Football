package com.ddastudio.hifivefootball_android.data.model;

import android.view.View;
import android.widget.TextView;

import com.ddastudio.hifivefootball_android.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by hongmac on 2017. 9. 13..
 */

public class SimpleDataModel extends AbstractFlexibleItem<SimpleDataModel.SimpleDataViewHolder> {

    String title;
    String subTitle;

    public SimpleDataModel() {

    }

    public SimpleDataModel(String title, String subTitle) {
        this.title = title;
        this.subTitle = subTitle;
    }

    @Override
    public boolean equals(Object o) {
        return title.equals(o.toString());
    }

    @Override
    public int getLayoutRes() {
        return R.layout.row_simple_data_item;
    }

    @Override
    public SimpleDataViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return new SimpleDataViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, SimpleDataViewHolder holder, int position, List payloads) {
        holder.tvTitle.setText("AAAA");
        holder.tvSubTitle.setText("BBBB");
    }

    static final class SimpleDataViewHolder extends FlexibleViewHolder {

        @BindView(R.id.simple_title) TextView tvTitle;
        @BindView(R.id.simple_sub_title) TextView tvSubTitle;

        public SimpleDataViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }

        public SimpleDataViewHolder(View view, FlexibleAdapter adapter, boolean stickyHeader) {
            super(view, adapter, stickyHeader);
            ButterKnife.bind(this, view);
        }
    }
}

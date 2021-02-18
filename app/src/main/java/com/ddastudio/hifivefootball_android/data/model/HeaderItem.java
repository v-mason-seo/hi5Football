package com.ddastudio.hifivefootball_android.data.model;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddastudio.hifivefootball_android.R;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractHeaderItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.flexibleadapter.items.ISectionable;
import eu.davidea.flexibleadapter.utils.Log;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by hongmac on 2017. 9. 5..
 */

public class HeaderItem extends AbstractHeaderItem<HeaderItem.HeaderViewHolder> implements IFilterable {

    private String id;
    private String title;
    private String subtitle;

    public HeaderItem(String id) {
        super();
        this.id = id;
        setDraggable(true);
    }

    @Override
    public boolean equals(Object inObject) {
        if (inObject instanceof HeaderItem) {
            HeaderItem inItem = (HeaderItem) inObject;
            return this.getId().equals(inItem.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    @Override
    public int getSpanSize(int spanCount, int position) {
        return spanCount;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.recycler_header_item;
    }

    @Override
    public HeaderViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return new HeaderViewHolder(view, adapter);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void bindViewHolder(FlexibleAdapter adapter, HeaderViewHolder holder, int position, List payloads) {
        if (payloads.size() > 0) {
        } else {
            holder.mTitle.setText(getTitle());
        }
        List<ISectionable> sectionableList = adapter.getSectionItems(this);
        String subTitle = (sectionableList.isEmpty() ? "Empty section" :
                sectionableList.size() + " section items");
        holder.mSubtitle.setText(subTitle);
    }

    @Override
    public boolean filter(String constraint) {
        return getTitle() != null && getTitle().toLowerCase().trim().contains(constraint);
    }

    static class HeaderViewHolder extends FlexibleViewHolder {

        TextView mTitle;
        TextView mSubtitle;
        ImageView mHandleView;

        HeaderViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter, true);//True for sticky
            mTitle = view.findViewById(R.id.title);
            mSubtitle = view.findViewById(R.id.subtitle);
            mTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("HeaderTitle", "Registered internal click on Header TitleTextView! " + mTitle.getText() + " position=" + getFlexibleAdapterPosition());
                }
            });
            this.mHandleView = view.findViewById(R.id.row_handle);
            if (adapter.isHandleDragEnabled()) {
                this.mHandleView.setVisibility(View.VISIBLE);
                setDragHandleView(mHandleView);
            } else {
                this.mHandleView.setVisibility(View.GONE);
            }

            // Support for StaggeredGridLayoutManager
            setFullSpan(true);
        }
    }

    @Override
    public String toString() {
        return "HeaderItem[id=" + id +
                ", title=" + title + "]";
    }

}
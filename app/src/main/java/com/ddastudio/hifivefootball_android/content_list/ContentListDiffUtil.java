package com.ddastudio.hifivefootball_android.content_list;

import android.support.v7.util.DiffUtil;

import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;

import java.util.List;

public class ContentListDiffUtil extends DiffUtil.Callback {

    private final List<ContentHeaderModel> oldList;
    private final List<ContentHeaderModel> newList;

    public ContentListDiffUtil(List<ContentHeaderModel> oldList, List<ContentHeaderModel> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList != null ? oldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newList != null ? newList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {

        final ContentHeaderModel oldItem = oldList.get(oldItemPosition);
        final ContentHeaderModel newItem = newList.get(newItemPosition);

        return oldItem.getContentId() == newItem.getContentId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

        final ContentHeaderModel oldItem = oldList.get(oldItemPosition);
        final ContentHeaderModel newItem = newList.get(newItemPosition);

        return oldItem.equals(newItem);
    }
}

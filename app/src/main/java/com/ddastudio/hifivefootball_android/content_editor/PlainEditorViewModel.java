package com.ddastudio.hifivefootball_android.content_editor;

import android.arch.lifecycle.ViewModel;
import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.ArrayList;
import java.util.List;

public class PlainEditorViewModel extends ViewModel {

    String mTitle;
    List<String> mTagList;
    List<MultiItemEntity> mItemList;

    public PlainEditorViewModel() {
        this.mTagList = new ArrayList<>();
        this.mItemList = new ArrayList<>();
    }

    public String getTitle() {
        return TextUtils.isEmpty(mTitle) ? "" : mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    /*-----------------------------------------------------*/

    public List<MultiItemEntity> getItemList() {
        return mItemList;
    }

    public void addItem(MultiItemEntity item) {
        if ( mItemList == null ) {
            this.mItemList = new ArrayList<>();
        }

        this.mItemList.add(item);
    }

    public void addItems(List<MultiItemEntity> items) {

        this.mItemList.addAll(items);
    }

    public void clearItem() {

        if ( mItemList != null ) {
            mItemList.clear();
        }
    }

    /*-----------------------------------------------------*/

    public List<String> getTagList() {
        return mTagList;
    }

    public void addTag(String tag) {
        if ( mTagList == null ) {
            this.mTagList = new ArrayList<>();
        }

        this.mTagList.add(tag);
    }

    public void cleardTag() {

        if ( mTagList != null ) {
            mTagList.clear();
        }
    }
}

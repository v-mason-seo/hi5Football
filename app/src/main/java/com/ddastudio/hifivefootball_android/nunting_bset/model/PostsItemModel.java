package com.ddastudio.hifivefootball_android.nunting_bset.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PostsItemModel {

    @SerializedName("pid") String mPid;
    @SerializedName("prev_pid") String mPrevPid;
    @SerializedName("items")
    List<PostsListModel> mItems;

    public String getPid() {
        return mPid;
    }

    public void setPid(String mPid) {
        this.mPid = mPid;
    }

    public String getPrevPid() {
        return mPrevPid;
    }

    public void setPrevPid(String mPrevPid) {
        this.mPrevPid = mPrevPid;
    }

    public List<PostsListModel> getItems() {
        return mItems;
    }

    public void setItems(List<PostsListModel> mItems) {
        this.mItems = mItems;
    }
}

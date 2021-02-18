package com.ddastudio.hifivefootball_android.nunting_bset.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.common.NuntingViewType;
import com.google.gson.annotations.SerializedName;

import java.net.URL;

public class PostsListModel implements Parcelable, MultiItemEntity {

    @SerializedName("uid") private String mNo;
    @SerializedName("url") private String mContentsUrl;
    @SerializedName("urlm") private String mContentsMobileUrl;
    @SerializedName("title") private String mTitle;
    @SerializedName("rcnt") private String mReplayCount;
    @SerializedName("unm") private String mUser;
    @SerializedName("dt") private String mRegdate;
    @SerializedName("hit") private String mCount;
    @SerializedName("good") private String mLikeCount;
    String mBaseUrl;
    String mBestBaseUrl;
    String mDataVer;
    String mRead = "N";
    String mScrap = "N";
    String mScrapDt = "";
    String mSiteName = "";

    int itemType;

    public PostsListModel() {

        itemType = NuntingViewType.POSTS;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public String getDataVer() {
        return mDataVer;
    }

    public void setDataVer(String DataVer) {
        this.mDataVer = DataVer;
    }

    public String getLikeCount() {
        return mLikeCount;
    }

    public void setLikeCount(String mLikeCount) {
        this.mLikeCount = mLikeCount;
    }

    public String getNo() {
        return mNo;
    }

    public String getCount() {
        return mCount;
    }

    public void setCount(String mCount) {
        this.mCount = mCount;
    }

    public void setNo(String mNo) {
        this.mNo = mNo;
    }


    public String getContentsUrl() {
        return mContentsUrl;
    }

    public void setContentsUrl(String contentsUrl) {

        mContentsUrl = this.mBaseUrl + contentsUrl;
    }

    public void setBestContentsUrl(String contentsUrl) {

        mContentsUrl = this.mBestBaseUrl + contentsUrl;
    }

    public String getContentsMobileUrl() {
        return mContentsMobileUrl;
    }

    public void setContentsMobileUrl(String ContentsMobileUrl) {
        this.mContentsMobileUrl = ContentsMobileUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }


    public String getUser() {
        return mUser;
    }

    public void setUser(String mUser) {
        this.mUser = mUser;
    }


    public String getRegdate() {
        return mRegdate;
    }

    public void setRegdate(String regdate) {


        //this.mRegdate = DateUtils.convertDateToString(mRegdate, "yyyy-MM-dd HH:mm:ss");
        this.mRegdate = regdate;
    }

    public String getReplayCount() {
        return mReplayCount;
    }

    public void setReplayCount(String mReplayCount) {
        this.mReplayCount = mReplayCount;
    }

    public String getRead() {
        return mRead;
    }

    public void setRead(String mRead) {
        this.mRead = mRead;
    }

    public String getScrap() {
        return mScrap;
    }

    public void setScrap(String mScrap) {
        this.mScrap = mScrap;
    }

    public String getScrapDt() {
        return mScrapDt;
    }

    public void setScrapDt(String mScrapDt) {
        this.mScrapDt = mScrapDt;
    }

    public String getSiteName() {
        return mSiteName;
    }

    public void setSiteName(String mSiteName) {
        this.mSiteName = mSiteName;
    }

    @Override
    public int hashCode() {

        try {
            URL urlHashCode = new URL(mContentsUrl);
            return urlHashCode.hashCode();
        } catch (Exception ex) {
            return 0;
        }
    }

    @Override
    public boolean equals(Object object) {

        if ( (object instanceof PostsListModel) == false)
            return false;

        PostsListModel model= (PostsListModel)object;

        if ( model.hashCode() == this.hashCode()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(mNo);
        dest.writeString(mContentsUrl);
        dest.writeString(mContentsMobileUrl);
        dest.writeString(mTitle);
        dest.writeString(mReplayCount);
        dest.writeString(mUser);
        dest.writeString(mRegdate);
        dest.writeString(mCount);
        dest.writeString(mLikeCount);
        dest.writeString(mBaseUrl);
        dest.writeString(mBestBaseUrl);
        dest.writeString(mRead);
        dest.writeString(mScrap);
    }

    public static final Parcelable.Creator<PostsListModel> CREATOR = new ClassLoaderCreator<PostsListModel>() {
        @Override
        public PostsListModel createFromParcel(Parcel source, ClassLoader loader) {

            PostsListModel plModel = new PostsListModel();
            plModel.mNo = source.readString();
            plModel.mContentsUrl = source.readString();
            plModel.mContentsMobileUrl = source.readString();
            plModel.mTitle = source.readString();
            plModel.mReplayCount = source.readString();
            plModel.mUser = source.readString();
            plModel.mRegdate = source.readString();
            plModel.mCount = source.readString();
            plModel.mLikeCount = source.readString();
            plModel.mBaseUrl = source.readString();
            plModel.mBestBaseUrl = source.readString();
            plModel.mRead = source.readString();
            plModel.mScrap = source.readString();

            return plModel;
        }

        @Override
        public PostsListModel createFromParcel(Parcel source) {
            return null;
        }

        @Override
        public PostsListModel[] newArray(int size) {
            return new PostsListModel[size];
        }
    };


}

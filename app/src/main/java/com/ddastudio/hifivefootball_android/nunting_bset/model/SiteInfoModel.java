package com.ddastudio.hifivefootball_android.nunting_bset.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class SiteInfoModel implements Parcelable {

    int mViewType = 0;
    int mOrderedId = -1;

    @SerializedName("id")
    String mSiteId;
    @SerializedName("nm")
    String mSiteName;
    String mType;
    int    mTypeId;

    public SiteInfoModel() {

    }

    public SiteInfoModel(String id, String name, int seq, int viewType) {

        this.mSiteId = id;
        this.mSiteName = name;
        this.mOrderedId = seq;
        this.mViewType = viewType;
    }

    public SiteInfoModel(int orderedId) {

        this.mOrderedId = orderedId;
    }

    /*public SiteInfoModel(String siteid, String siteName, String siteURL, int viewType, int orderedId) {

        this.mSiteId = siteid;
        this.mSiteName = siteName;
        this.mSiteUrl = siteURL;
        this.mViewType = viewType;
        this.mOrderedId = orderedId;
    }*/

    public int getmViewType() {
        return mViewType;
    }

    public void setmViewType(int mViewType) {
        this.mViewType = mViewType;
    }

    public int getmOrderedId() {
        return mOrderedId;
    }

    public void setmOrderedId(int mOrderedId) {
        this.mOrderedId = mOrderedId;
    }

    public String getSiteId() {
        return mSiteId;
    }

    public void setSiteId(String SiteId) {
        this.mSiteId = SiteId;
    }

    public String getSiteName() {
        return mSiteName;
    }

    public void setSiteName(String SiteName) {
        this.mSiteName = SiteName;
    }

    public String getType() {
        return mType;
    }

    public void setType(String mType) {
        this.mType = mType;
    }

    public int getTypeId() {
        return mTypeId;
    }

    public void setTypeId(int iTypeId) {
        this.mTypeId = iTypeId;
    }

    //    public String getSiteUrl() {
//        return mSiteUrl;
//    }
//
//    public void setSiteUrl(String SiteUrl) {
//        this.mSiteUrl = SiteUrl;
//    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(mOrderedId);
        dest.writeInt(mOrderedId);
        dest.writeString(mSiteName);
        //dest.writeString(mSiteUrl);
        dest.writeString(mSiteId);
        dest.writeString(mType);
        dest.writeInt(mTypeId);
    }

    public static final Parcelable.Creator<SiteInfoModel> CREATOR = new Parcelable.ClassLoaderCreator<SiteInfoModel>() {
        @Override
        public SiteInfoModel createFromParcel(Parcel source, ClassLoader loader) {

            SiteInfoModel model = new SiteInfoModel();
            model.mOrderedId = source.readInt();
            model.mViewType = source.readInt();
            model.mSiteName = source.readString();
            //model.mSiteUrl = source.readString();
            model.mSiteId = source.readString();
            model.mType = source.readString();
            model.mTypeId = source.readInt();

            return model;
        }

        @Override
        public SiteInfoModel createFromParcel(Parcel source) {
            return null;
        }

        @Override
        public SiteInfoModel[] newArray(int size) {
            return new SiteInfoModel[size];
        }
    };
}

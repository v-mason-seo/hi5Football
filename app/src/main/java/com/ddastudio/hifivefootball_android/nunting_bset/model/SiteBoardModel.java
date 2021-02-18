package com.ddastudio.hifivefootball_android.nunting_bset.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.common.NuntingViewType;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

public class SiteBoardModel implements MultiItemEntity {

    int itemType;
    String baseUri = "http://nb.hifivesoccer.com:4000/favicons/";

    @SerializedName("name")
    Name mName;
    @SerializedName("boards")
    List<Boards> mBoards;

    public SiteBoardModel() {
        this.itemType = NuntingViewType.SITE_LIST;
    }

    public Name getName() {
        return mName;
    }

    public void setName(Name mName) {
        this.mName = mName;
    }

    public List<Boards> getBoards() {
        return mBoards;
    }

    public void setBoards(List<Boards> mBoards) {
        this.mBoards = mBoards;
    }

    public String getSiteLogo() {

        return baseUri + getName().getId() + ".png";
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    /**
     *
     */
    @Parcel
    public static class Name {

        @SerializedName("id")
        String mId;
        @SerializedName("nm")
        String mNm;
        @SerializedName("type")
        String mType;

        public String getId() {
            return mId;
        }

        public void setId(String mId) {
            this.mId = mId;
        }

        public String getNm() {
            return mNm;
        }

        public void setNm(String mNm) {
            this.mNm = mNm;
        }

        public String getType() {
            return mType;
        }

        public void setType(String mType) {
            this.mType = mType;
        }
    }

    /**
     *
     */
    @Parcel
    public static class Boards {

        @SerializedName("bid")
        String mBid;
        @SerializedName("bnm")
        String mBnm;

        public String getBid() {
            return mBid;
        }

        public void setBid(String mBid) {
            this.mBid = mBid;
        }

        public String getBnm() {
            return mBnm;
        }

        public void setBnm(String mBnm) {
            this.mBnm = mBnm;
        }
    }
}

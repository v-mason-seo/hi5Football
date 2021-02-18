package com.ddastudio.hifivefootball_android.data.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by hongmac on 2017. 9. 21..
 */

@Parcel
public class UserActionModel {

    @SerializedName("like") int like;
    @SerializedName("scrap") int scrap;
    @SerializedName("hasImgs") int hasImgs;

    public boolean isLike() {
        return like == 0 ? false : true;
    }

    public boolean isScrap() {
        return scrap == 0 ? false : true;
    }

    public boolean isHasImgs() {
        return hasImgs == 0 ? false : true;
    }

    public int getLike() {
        return like;
    }

    public int getScrap() {
        return scrap;
    }

    public int getHasImgs() {
        return hasImgs;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public void setScrap(int scrap) {
        this.scrap = scrap;
    }

    public void setHasImgs(int hasImgs) {
        this.hasImgs = hasImgs;
    }
}

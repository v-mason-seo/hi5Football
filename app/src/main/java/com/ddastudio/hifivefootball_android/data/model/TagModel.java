package com.ddastudio.hifivefootball_android.data.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by hongmac on 2017. 9. 29..
 */

@Parcel
public class TagModel {

    @SerializedName("id") int id;
    @SerializedName("name") String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

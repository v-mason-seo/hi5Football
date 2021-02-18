package com.ddastudio.hifivefootball_android.data.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by hongmac on 2017. 9. 8..
 */

@Parcel
public class StreamViewerModel {

    public static final int IMAGE = 0;
    public static final int IMAGE_GIF = 1;
    public static final int YOUTUBE = 2;
    public static final int MP4 = 3;


    @SerializedName("type") int streamType;
    @SerializedName("url") String streamUrl;

    public StreamViewerModel() {}

    public StreamViewerModel(int type, String url) {

        this.streamType = type;
        this.streamUrl = url;
    }

    public int getStreamType() {
        return this.streamType;
    }

    public String getStreamUrl() {
        return this.streamUrl;
    }
}

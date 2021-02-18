package com.ddastudio.hifivefootball_android.match_chat.model;

import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MatchChatMessage implements MultiItemEntity {

    @Expose(serialize = false, deserialize = false)
    Integer itemType;

    @Expose
    @SerializedName("m")
    String message;
    @Expose
    @SerializedName("s")
    String sticker;
    @Expose(serialize = false, deserialize = false)
    int stickerResId;
    @Expose
    @SerializedName("e")
    String effect;
    @Expose
    @SerializedName("i")
    String image;

    @Override
    public int getItemType() {
        return 5534;
    }

    /*----------------------------------------------------*/

    public String getMessage() {
        return TextUtils.isEmpty(message) ? "" : message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

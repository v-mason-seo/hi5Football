package com.ddastudio.hifivefootball_android.data.model.arena_chat;

import android.content.Context;
import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hongmac on 2018. 1. 26..
 */

public class ReactionModel implements MultiItemEntity{

    final public static int HOME = 0;
    final public static int AWAY = 1;
    @Expose(serialize = false, deserialize = false)
    Integer itemType;

    @Expose
    @SerializedName("talkid")
    Integer talkId;
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
    @Expose
    @SerializedName("t")
    int side; // 0 : home, 1 : away
    @Expose
    @SerializedName("u")
    UserModel user;
    @Expose
    @SerializedName("match")
    MatchModel match;
    @Expose
    @SerializedName("h")
    String botMessage;

    public String getBotMessage() {
        return botMessage;
    }

    public ReactionModel() {
    }

    public ReactionModel(int itemType, int side, String data, UserModel user) {
        //itemType = ViewType.REACTION_HOME_MESSAGE;
        this.itemType = itemType;
        this.side = side;
        this.message = data;
        this.user = user;
    }

    @Override
    public int getItemType() {

        if ( itemType != null ) {
            return itemType;
        }

        if ( match != null ) {
            itemType = ViewType.REACTION_MATCH_DATA;
        } else if ( !TextUtils.isEmpty(image) ) {
            // 이미지일경우 - 이미지와 텍스트가 같이 올 수 있음.
            itemType = ( side == 0 ? ViewType.REACTION_HOME_IMAGE : ViewType.REACTION_AWAY_IMAGE );
        } else if ( !TextUtils.isEmpty(message) ) {
            // 텍스트 메시지
            itemType = side == 0 ? ViewType.REACTION_HOME_MESSAGE : ViewType.REACTION_AWAY_MESSAGE;
        } else if ( !TextUtils.isEmpty(effect) ) {
            // 이펙트
            itemType = side == 0 ? ViewType.REACTION_HOME_EFFECT : ViewType.REACTION_AWAY_EFFECT;
        } else if ( !TextUtils.isEmpty(sticker) ) {
            // 스티커
            itemType = side == 0 ? ViewType.REACTION_HOME_STICKER : ViewType.REACTION_AWAY_STICKER;
        } else if ( !TextUtils.isEmpty(botMessage)) {
            itemType = ViewType.REACTION_BOT_MESSAGE;
        } else {
            itemType = ViewType.REACTION_HOME_MESSAGE;
        }

        return itemType;
    }

    /*------------------------------------------------------*/

    public Integer getTalkId() {
        return talkId;
    }

    public void setTalkId(Integer talkId) {
        this.talkId = talkId;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getMessage() {
        return TextUtils.isEmpty(message) ? "" : message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSticker() {
        return sticker;
    }

    public void setSticker(String sticker) {
        this.sticker = sticker;
    }

    public void findStickerResId(Context context) {
        String resName = "ic_s_" + getSticker();
        int drawableid = context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
        this.stickerResId = drawableid;
    }

    public int getStickerResId() {
        return stickerResId;
    }

    public void setStickerResId(int stickerResId) {
        this.stickerResId = stickerResId;
    }

    public String getEffect() {
        return TextUtils.isEmpty(effect) ? "" : effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public int getSide() {
        return side;
    }

    public String getSideString() {
        return side == 0 ? "home" : "away";
    }

    public void setSide(int side) {
        this.side = side;
    }

    public UserModel getUser() {
        return user;
    }

    public String getNickName() {

        if ( user != null ) {
            return user.getNickname();
        }

        return "Guest";
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public String getAvatar() {
        if ( user != null )
            return user.getAvatarUrl();

        return "";
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return  String.format("message : %s\nsticker : %s\neffect : %s\n, which side : %s",
                getMessage(),
                getSticker(),
                getEffect(),
                getSideString());
    }




    public MatchModel getMatch() {
        return match;
    }
}

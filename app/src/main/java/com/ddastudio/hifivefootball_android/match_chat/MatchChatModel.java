package com.ddastudio.hifivefootball_android.match_chat;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kakao.usermgmt.response.model.User;

public class MatchChatModel implements MultiItemEntity{

    @Expose
    @SerializedName("m")
    String chatMessage;

    @Expose
    @SerializedName("s")
    String sticker;

    @Expose
    @SerializedName("u")
    UserModel user;


    public MatchChatModel(String chatMessage) {
        this.chatMessage = chatMessage;
    }

    public MatchChatModel(String chatMessage, UserModel user) {
        this.chatMessage = chatMessage;
        this.user = user;
    }

    @Override
    public int getItemType() {
        return 5534;
    }


    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }

    public UserModel getUser() {
        return user;
    }

    public String getAvatar() {

        if ( user != null ) {
            return user.getAvatarUrl();
        }

        return "";
    }
}

package com.ddastudio.hifivefootball_android.data.model;


import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.EntityType;
import com.ddastudio.hifivefootball_android.utils.CommonUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.ocpsoft.prettytime.PrettyTime;
import org.parceler.Parcel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by hongmac on 2017. 9. 4..
 */

@Parcel(analyze = {UserModel.class})
public class UserModel implements MultiItemEntity {

    String AvatrOriginBaseUrl = "http://cdn.hifivefootball.com/origin/profile/";
    String AvatrBaseUrl = "http://cdn.hifivefootball.com/resize/s/profile/";
    String AvatrMediumBaseUrl = "http://cdn.hifivefootball.com/resize/m/profile/";
    String AvatrLargeBaseUrl = "http://cdn.hifivefootball.com/resize/l/profile/";

    int itemType;
    @Expose
    @SerializedName("username")
    String username;
    @Expose
    @SerializedName("nickname")
    String nickname;
    @Expose(serialize = true, deserialize = false)
    @SerializedName("profile")
    String profile;
    @Expose
    @SerializedName("avatar_url")
    String avatarUrl;
    @SerializedName("deviceid")
    String deviceid;

    /*--------------------------------------------------------------*/

    @Override
    public String toString() {
        return String.format("UserModel {\n" +
                "\t- User name : %s\n" +
                "\t- Nick name : %s\n" +
                "\t- Profile : %s\n" +
                "\t- Avatar url : %s }", getUsername(), getNickname(), getProfile(), getAvatarUrl());
    }

    public UserModel() {
        this.itemType = MultipleItem.USER_TYPE;
        this.profile = "";
        this.avatarUrl = "";
        this.username = "";
        this.nickname = "";
    }

    public String getNickNameAndUserName() {

        return String.format("%s ( %s )", getNickname(), getUsername());
    }

    public String getUsername() {

        if ( TextUtils.isEmpty(username)) {
            return "";
        }
        return username;
    }

    public String getProfile() {

        return TextUtils.isEmpty(profile) ? "" : profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getAvatrOriginBaseUrl() {
        return AvatrOriginBaseUrl;
    }

    public String getAvatarUrl() {

        if ( TextUtils.isEmpty(avatarUrl)) {
            return "";
        }

        if ( avatarUrl.contains("http://") || avatarUrl.contains("https://")) {
            return avatarUrl;
        }

        return AvatrBaseUrl + avatarUrl;
    }

    public String getAvatarOriginUrl() {

        if ( TextUtils.isEmpty(avatarUrl)) {
            return "";
        }

        if ( avatarUrl.contains("http://") || avatarUrl.contains("https://")) {
            return avatarUrl;
        }

        return AvatrOriginBaseUrl + avatarUrl;
    }

    public String getAvatarMediumUrl() {
        return AvatrMediumBaseUrl + avatarUrl;
    }

    public String getAvatarLargeUrl() {
        return AvatrLargeBaseUrl + avatarUrl;
    }

    public String getAvatarFileName() {
        return avatarUrl;
    }

    public String getNickname() {

        if ( TextUtils.isEmpty(nickname)) {
            return "";
        }
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    /*--------------------------------------------------------------*/

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public boolean equals(Object o) {
        return username.equals(((UserModel)o).getUsername());
    }


    public UserModel(String username, String profile, String avatarUrl) {
        this.username = username;
        this.profile = profile;
        this.avatarUrl = avatarUrl;
    }
}

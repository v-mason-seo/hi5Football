package com.ddastudio.hifivefootball_android.signin;

import org.parceler.Parcel;

/**
 * Created by hongmac on 2017. 9. 6..
 */

@Parcel
public class SnsInfoModel {

    String provider_user_id;
    String provider;
    String email;
    String gender;
    String username;
    String avatar;
    String accessToken;
    String refreshToken;

    public SnsInfoModel() {

    }

    public String getProviderUserId() {
        return provider_user_id;
    }

    public String getProvider() {
        return provider;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setProviderUserId(String provider_user_id) {
        this.provider_user_id = provider_user_id;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

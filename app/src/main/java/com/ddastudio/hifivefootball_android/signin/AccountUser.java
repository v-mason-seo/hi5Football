package com.ddastudio.hifivefootball_android.signin;

import android.net.Uri;

/**
 * Created by hongmac on 2017. 11. 17..
 */

public class AccountUser {

    String providerUserId;
    String provider;
    String userName;
    String nickName;
    String avatar;
    String email;
    String accessToken;
    String refreshToken;

    public AccountUser() {}

    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSnsAccessToken() {
        return "bearer " + accessToken;
        //return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getSnsRefreshToken() {
        return "bearer " + refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {

        return String.format("AccountUser {\n" +
                "\t- provider id : %s\n" +
                "\t- provider : %s\n" +
                "\t- userName : %s\n" +
                "\t- nickName : %s\n" +
                "\t- avatar : %s\n" +
                "\t- email : %s\n" +
                "\t- access token : %s\n" +
                "\t- refresh token : %s }", providerUserId, provider, userName, nickName, avatar, email, accessToken, refreshToken);
    }
}

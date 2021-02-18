package com.ddastudio.hifivefootball_android.data.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by hongmac on 2017. 9. 7..
 */

@Parcel
public class AuthToken {

    public AuthToken() {
    }

    @SerializedName("access_token") String accessToken;
    @SerializedName("refresh_token") String refreshToken;
    @SerializedName("expires_in") int expiresIn;
    @SerializedName("token_type") String tokenType;

    public String getAccessToken() {
        return tokenType + " " + accessToken;
    }

    public String getRefreshToken() {
        return tokenType + " " + refreshToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Override
    public String toString() {

//        return String.format("[HifiveToken] Access token : %s, Refresh token : %s, ExpiresIn : %d, Token type : s%", accessToken, refreshToken, expiresIn, tokenType);

        return String.format("HifiveToken { " +
                "\r\n\t- Access token : %s" +
                "\r\n\t- Refresht token : %s" +
                "\r\n\t- Token type : %s" +
                "\r\n\t- ExpiresIn : %d }", accessToken, refreshToken, tokenType, expiresIn);
    }
}

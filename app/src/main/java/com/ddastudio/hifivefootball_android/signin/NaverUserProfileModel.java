package com.ddastudio.hifivefootball_android.signin;

import com.google.gson.annotations.SerializedName;

import static com.ddastudio.hifivefootball_android.signin.Constants.ACCOUNT_NAVER;

/**
 * Created by hongmac on 2017. 11. 16..
 */

public class NaverUserProfileModel {

    @SerializedName("resultcode") String resultcode;
    @SerializedName("message") String message;
    @SerializedName("response") NaverResponseModel response;


    public class NaverResponseModel {

        @SerializedName("nickname") String nickname;
        @SerializedName("enc_id") String enc_id;
        @SerializedName("profile_image") String profile_image;
        @SerializedName("age") String age;
        @SerializedName("gender") String gender;
        @SerializedName("id") String id;
        @SerializedName("email") String email;
        @SerializedName("birthday") String birthday;

    }

    public String getUserName() {
        return response != null ? response.nickname : "";
    }

    public String getEmail() {
        return response != null ? response.email : "";
    }

    public String getProviderId() {
        return response != null ? response.id : "";
    }

    public String getProviderType() {
        return ACCOUNT_NAVER;
    }

    public String getAvatar() {
        return response != null ? response.profile_image : "";
    }

    public String getResultcode() {
        return resultcode;
    }

    public String getMessage() {
        return message;
    }
}

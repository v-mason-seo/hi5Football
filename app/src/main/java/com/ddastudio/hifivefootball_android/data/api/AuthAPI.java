package com.ddastudio.hifivefootball_android.data.api;

import com.ddastudio.hifivefootball_android.data.model.AuthToken;
import com.ddastudio.hifivefootball_android.data.model.CheckEmailModel;
import com.ddastudio.hifivefootball_android.data.model.CheckUserNameModel;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;

import io.reactivex.Flowable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by hongmac on 2017. 9. 6..
 */

public interface AuthAPI {

    /**
     * 하이파이브 로그인
     * @param provider
     * @param provider_user_id
     * @param provider_access_key
     * @return
     */
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("v1/auth/login/social")
    Flowable<DBResultResponse> socialLogin(@Field("provider") String provider,
                                           @Field("provider_user_id") String provider_user_id,
                                           @Field(value = "access_token", encoded = true)String provider_access_key);

    /**
     * 하아피이브 Access token 갱신
     * @param refresh_toekn
     * @return
     */
    //@FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("v1/auth/token/refresh")
    Flowable<DBResultResponse> accessTokenRefresh(@Header("Authorization") String refresh_toekn);

    /**
     * 하이파이브 회원가입
     * @param provider
     * @param provider_user_id
     * @param provider_access_key
     * @param username
     * @param nickname
     * @param email
     * @param avatar_url
     * @return
     */
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("v1/auth/signup/social")
    Flowable<DBResultResponse> socialSignup(@Field(value = "provider", encoded = true) String provider,
                                            @Field(value = "provider_user_id", encoded = true) String provider_user_id,
                                            @Field(value = "access_token", encoded = true)String provider_access_key,
                                            @Field(value = "username", encoded = true) String username,
                                            @Field(value = "nickname", encoded = true) String nickname,
                                            @Field(value = "profile", encoded = true) String profile,
                                            @Field(value = "email", encoded = true) String email,
                                            @Field(value = "avatar_url", encoded = true) String avatar_url,
                                            @Field(value = "password", encoded = true) String password);

    @GET("v1/auth/valid/email")
    Flowable<CheckEmailModel> checkEmail(@Query("q") String email);

    @GET("v1/auth/valid/username")
    Flowable<CheckUserNameModel> checkUserName(@Query("q") String userName);








}

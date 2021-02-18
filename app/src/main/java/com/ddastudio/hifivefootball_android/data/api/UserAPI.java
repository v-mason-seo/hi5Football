package com.ddastudio.hifivefootball_android.data.api;

import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.community.UserCommentModel;
import com.ddastudio.hifivefootball_android.data.model.UserModel;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by hongmac on 2017. 9. 7..
 */

public interface UserAPI {

    /**
     * 유저(본인) 정보 조회
     * @param token
     * @return
     */
    @GET("v1/users")
    Flowable<UserModel> getLoginUser(@Header("authorization") String token);

    /**
     * 유저(다른사람) 정보 조회
     * @param username
     * @return
     */
    @GET("v1/users/{username}")
    Flowable<UserModel> onLoadUserInfo(@Path("username") String username);

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @PUT("v1/users/avatar")
    Flowable<DBResultResponse> updateAvatar(@Header("authorization") String token,
                                            @Field("avatar_url") String avatarUrl);

    /**
     * 유저정보 입력
     * @param login_path
     * @param email
     * @param username
     * @param profile
     * @param avatar_url
     * @param favorite_team
     * @param favorite_player
     * @param favorite_site
     * @param favorite_national
     * @return
     */
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("v1/users/insert/userinfo")
    Flowable<DBResultResponse> onInsertUserInfo(@Field("login_path") int login_path,
                                                @Field("email") String email,
                                                @Field("username") String username,
                                                @Field("profile") String profile,
                                                @Field("avatar_url") String avatar_url,
                                                @Field("favorite_team") String favorite_team,
                                                @Field("favorite_player") String favorite_player,
                                                @Field("favorite_site") String favorite_site,
                                                @Field("favorite_national") String favorite_national);


    /**
     * 사용자 정보 수정
     * @param email
     * @param username
     * @param profile
     * @param avatar_url
     * @param favorite_team
     * @param favorite_player
     * @param favorite_site
     * @param favorite_national
     * @return
     */
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @PUT("v1/users")
    Flowable<DBResultResponse> onUpdateUserInfo(@Header("authorization") String token,
                                                @Field("nickname") String nickname,
                                                @Field("profile") String profile,
                                                @Field("avatarurl") String avatar_url/*,
                                                @Field("favorite_team") String favorite_team,
                                                @Field("favorite_player") String favorite_player,
                                                @Field("favorite_site") String favorite_site,
                                                @Field("favorite_national") String favorite_national*/);


    /**
     * 사용자가 쓴글
     * @param username
     * @param limit
     * @param offset
     * @return
     */
    @GET("v1/users/{username}/contents")
    Flowable<List<ContentHeaderModel>> getUserContentList(@Path("username") String username,
                                                          @Query("limit") int limit,
                                                          @Query("offset") int offset);


    /**
     * 사용자가쓴 댓글
     * @param username
     * @param limit
     * @param offset
     * @return
     */
    @GET("v1/users/{username}/comments")
    Flowable<List<UserCommentModel>> getUserCommentList(@Path("username") String username,
                                                        @Query("limit") int limit,
                                                        @Query("offset") int offset);

    /**
     * 내가누른 좋아요
     * @param username
     * @param limit
     * @param offset
     * @return
     */
    @GET("v1/users/{username}/hifive")
    Flowable<List<ContentHeaderModel>> getUserLikedList(@Path("username") String username,
                                                        @Query("limit") int limit,
                                                        @Query("offset") int offset);

    @GET("v1/users/{username}/scrap")
    Flowable<List<ContentHeaderModel>> onLoadScrapList(@Header("authorization") String token,
                                                       @Path("username") String username,
                                                       @Query("limit") int limit,
                                                       @Query("offset") int offset);
}

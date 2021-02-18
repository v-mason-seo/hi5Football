package com.ddastudio.hifivefootball_android.data.api;

import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingInfoModel;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.annotations.Nullable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by hongmac on 2017. 9. 8..
 */

public interface ContentAPI {

    /**
     * 게시글 본문 조회
     * @param token
     * @param contentId
     * @return
     */
    @GET("v1/contents/{id}")
    Flowable<ContentHeaderModel> getContent(@Header("authorization") String token,
                                            @Path("id") int contentId);


    /**
     * 게시글과 관련된 글 가져오기
     * @param token
     * @param contentId
     * @return
     */
    @GET("v1/contents/{id}/relation")
    Flowable<List<ContentHeaderModel>> getRelationContent(@Header("authorization") String token,
                                                            @Path("id") int contentId);

    /**
     * 매치리포트 데이터 가져오기 ( 선수평점 및 코멘트 )
     * @param token
     * @param contentId
     * @return
     */
    @GET("v1/contents/{id}/relation/rating")
    Flowable<List<PlayerRatingInfoModel>> getRelationPlayerRatings(@Header("authorization") String token,
                                                                   @Path("id") int contentId);

    /**
     * 게시글과 관련된 매치정보 가져오기
     * @param contentId
     * @return
     */
    @GET("v1/contents/{id}/relation/match")
    Flowable<List<MatchModel>> getRelationMatches(@Path("id") int contentId);

    /**
     * 게시글 입력
     * @param token
     * @param boardid
     * @param title
     * @param preview
     * @param content
     * @param arenaid
     * @param tag
     * @param imgs
     * @param bodyType 0: HTML, 1: PLAIN, 2: MARKDOWN , 3.link
     * @param cellType 0 default( board type ), 1 matchChat, 2. matchReport, 3 preview
     * @param allowComment
     * @return
     */
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("v1/contents/")
    Flowable<DBResultResponse> postContent(@Header("authorization") String token,
                                           @Field("boardid") int boardid,
                                           @Field("title") String title,
                                           @Field("preview") String preview,
                                           @Field("content") String content,
                                           @Field("arenaid") Integer arenaid,
                                           @Field("playerid") Integer playerid,
                                           @Field("teamid") Integer teamid,
                                           @Field("tags") String tag,
                                           @Field("imgs") @Nullable String imgs,
                                           @Field("bodytype") int bodyType,
                                           @Field("celltype") int cellType,
                                           @Field("allowcomment") int allowComment);

    /**
     * 게시글 수
     * @param token
     * @param contentid
     * @param title
     * @param preview
     * @param content
     * @param boardid
     * @param tag
     * @param imgs
     * @param allowComment
     * @param bodytype
     * @return
     */
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @PUT("v1/contents/{id}")
    Flowable<DBResultResponse> updateContent(@Header("authorization") String token,
                                             @Path("id") int contentid,
                                             @Field("boardid") int boardid,
                                             @Field("title") String title,
                                             @Field("preview") String preview,
                                             @Field("content") String content,
                                             @Field("arenaid") Integer arenaid,
                                             @Field("playerid") Integer playerid,
                                             @Field("teamid") Integer teamid,
                                             @Field("tags") String tag,
                                             @Field("imgs") @Nullable String imgs,
                                             @Field("bodytype") int bodytype,
                                             @Field("celltype") int cellType,
                                             @Field("allowComment") int allowComment );


    /**
     * 게시글 삭제
     * @param token
     * @param contentId
     * @return
     */
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @HTTP(method = "DELETE", path = "/v1/contents/{id}", hasBody = true)
    Flowable<DBResultResponse> deleteContent(@Header("authorization") String token,
                                             @Path("id") int contentId);


    // *** Like ***


    @GET("v1/contents/{id}/hifiveduser")
    Flowable<List<UserModel>> getLikers(@Path("id") int id);

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @PUT("v1/contents/{id}/hifive")
    Flowable<DBResultResponse> setLike(@Header("authorization") String token,
                                       @Path("id") int contentId,
                                       @Query("cnt") int hifive_count);

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @PUT("v1/contents/{id}/unhifive")
    Flowable<DBResultResponse> setUnlike(@Header("authorization") String token,
                                         @Path("id") int contentId);


    // *** Scrap ***

//    @GET("{id}/scrap")
//    Flowable<List<UserModel>> getScrap(@Path("id") int id);

    @GET("v1/contents/{id}/scrapusers")
    Flowable<List<UserModel>> getScrpUsers(@Path("id") int id);

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("v1/contents/{id}/scrap")
    Flowable<DBResultResponse> postScrap(@Header("authorization") String token,
                                         @Path("id") int contentId);


    @Headers("Content-Type: application/x-www-form-urlencoded")
    //@DELETE("{id}/scrap")
    @HTTP(method = "DELETE", path = "v1/contents/{id}/scrap", hasBody = true)
    Flowable<DBResultResponse> deleteScrap(@Header("authorization") String token,
                                           @Path("id") int contentId);


    // *** Report ***

    @PUT("v1/contents/{id}/reported")
    Flowable<DBResultResponse> setReported(@Header("authorization") String token,
                                           @Path("id") int contentId);


    // *** Issue ***
}

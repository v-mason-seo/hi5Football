package com.ddastudio.hifivefootball_android.data.api;

import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.data.model.community.MatchTalkModel;

import java.util.List;

import io.reactivex.Flowable;
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
 * Created by hongmac on 2018. 3. 2..
 */

public interface MatchTalkAPI {

    /**
     * 매치토크 데이터를 가져온다.
     * @param token
     * @param matchId
     * @param userName
     * @param limit
     * @param offset
     * @return
     */
    @GET("/v1/matchtalk")
    Flowable<List<MatchTalkModel>> onLoadMatchTalks(@Header("authorization") String token,
                                                    @Query("matchid") Integer matchId,
                                                    @Query("username") String userName,
                                                    @Query("limit") Integer limit,
                                                    @Query("offset") Integer offset);

    /**
     * 베스트 매치토크 데이터를 가져온다.
     * @param token
     * @param matchId
     * @return
     */
    @GET("/v1/matchtalk/best")
    Flowable<List<MatchTalkModel>> onLoadBestMatchTalks(@Header("authorization") String token,
                                                      @Query("matchid") Integer matchId);

    /**
     * 유저의 매치토크 데이터를 가져온다.
     * @param token
     * @param matchId
     * @return
     */
    @GET("/v1/matchtalk/{id}")
    Flowable<List<MatchTalkModel>> onLoadUserMatchTalks(@Header("authorization") String token,
                                                      @Path("id") Integer matchId);

    /*--------------------------------------------------------------------------------------*/

    /**
     * 매치토크 생성
     * @param token
     * @param matchid
     * @param content
     * @param cellType
     * @param status
     * @return
     */
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("/v1/matchtalk")
    Flowable<DBResultResponse> onCreateMatchTalk(@Header("authorization") String token,
                                                 @Field("matchid") int matchid,
                                                 @Field("content") String content,
                                                 @Field("celltype") int cellType,
                                                 @Field("status") String status);

    /**
     * 매치토크 삭제
     * @param token
     * @param id
     * @return
     */
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @HTTP(method = "DELETE", path = "/v1/matchtalk/{id}", hasBody = true)
    Flowable<DBResultResponse> onDeleteMatchTalk(@Header("authorization") String token,
                                                 @Path("id") Integer id);

    /*--------------------------------------------------------------------------------------*/

    /**
     * 하이파이브
     * @param token
     * @param matchTalkId
     * @return
     */
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @PUT("/v1/matchtalk/{id}/hifive")
    Flowable<DBResultResponse> setHifve(@Header("authorization") String token,
                                        @Path("id") Integer matchTalkId);


    /**
     * 하이파이브 취소
     * @param token
     * @param matchTalkId
     * @return
     */
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @PUT("/v1/matchtalk/{id}/unhifive")
    Flowable<DBResultResponse> setUnHifive(@Header("authorization") String token,
                                           @Path("id") Integer matchTalkId);


    /**
     * 하이파이브한 유정 리스트 가져오기
     * @param matchTalkId
     * @return
     */
    @GET("/v1/matchtalk/{id}/hifiveduser")
    Flowable<List<UserModel>> onLoadHifivedUserList(@Path("id") Integer matchTalkId);




    /*--------------------------------------------------------------------------------------*/

    /**
     * 신고하기
     * @param matchTalkId
     * @return
     */
    @PUT("/v1/matchtalk/{id}/reported")
    Flowable<DBResultResponse> setReported(@Path("id") Integer matchTalkId);

}

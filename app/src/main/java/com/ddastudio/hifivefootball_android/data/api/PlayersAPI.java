package com.ddastudio.hifivefootball_android.data.api;

import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingInfoModel;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.PlayerCommentModel;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by hongmac on 2018. 1. 15..
 */

public interface PlayersAPI {

    @GET("/v1/players/{id}")
    Flowable<PlayerModel> onLoadPlayer(@Path("id") int playerId);

    /**
     * 선수 한줄평 전체 항목을 가져온다.
     * @param limit
     * @param offset
     * @return
     */
    @GET("/v1/players/{id}/comments")
    Flowable<List<PlayerCommentModel>> onLoadOverallPlayerComments(@Path("id") int playerId,
                                                                   @Query("limit") int limit,
                                                                   @Query("offset") int offset);


    @GET("/v1/players")
    Flowable<List<PlayerModel>> onLoadPlayerList(@Query("limit") int limit,
                                                 @Query("offset") int offset);

    /**
     * 선수이름 변경 요청
     * @param token
     * @param playerId
     * @param beforeName
     * @param afterName
     * @return
     */
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("/v1/players/{id}/request/changename")
    Flowable<DBResultResponse> requestChangePlayerName(@Header("authorization") String token,
                                                       @Path("id") int playerId,
                                                       @Field("bname") String beforeName,
                                                       @Field("aname") String afterName);


    /**
     * 선수 코멘트 데이터를 가져온다.
     * @param playerId
     * @param matchId
     * @return
     */
    @GET("/v1/players/{id}/comments")
    Flowable<List<PlayerRatingInfoModel>> getPlayerComments(@Path("id") Integer playerId,
                                                           @Query("matchid") Integer matchId);

    /**
     * 플레이어 관련 글 가져오기
      * @param token
     * @param playerId
     * @param boardId
     * @param limit
     * @param offset
     * @return
     */
    @GET("/v1/players/{id}/content")
    Flowable<List<ContentHeaderModel>> onLoadPlayerContentList(@Header("authorization") String token,
                                                               @Path("id") Integer playerId,
                                                               @Query("boardid") Integer boardId,
                                                               @Query("limit") Integer limit,
                                                               @Query("offset") Integer offset);
}

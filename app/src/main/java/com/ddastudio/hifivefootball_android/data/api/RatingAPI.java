package com.ddastudio.hifivefootball_android.data.api;

import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.annotations.Nullable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by hongmac on 2018. 2. 26..
 */

public interface RatingAPI {

    /**
     * 선수평가를 위한 라인업 정보를 가져온다.
     * @param matchId
     * @return
     */
    @GET("/v1/matches/{mid}/lineup")
    Flowable<List<PlayerModel>> onLoadLineup(@Path("mid") int matchId);

    /**
     * 선수평가 입력
     * @param token
     * @param matchId
     * @param playerId
     * @param teamId
     * @param timeSeq
     * @param rating
     * @param updown
     * @param comment
     * @return
     */
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("/v1/rating/{mid}/player")
    Flowable<DBResultResponse> onPostPlayerRating(@Header("authorization") String token,
                                                  @Path("mid") Integer matchId,
                                                  @Field("playerid") Integer playerId,
                                                  @Field("teamid") Integer teamId,
                                                  @Field("timeseq") String timeSeq,
                                                  @Field("rating") Float rating,
                                                  @Field("updown") String updown,
                                                  @Field("comment") String comment
    );


}

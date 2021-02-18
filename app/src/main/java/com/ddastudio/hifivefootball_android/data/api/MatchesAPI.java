package com.ddastudio.hifivefootball_android.data.api;

import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingInfoModel;
import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingParticipationUser;
import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingRecordModel;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchOverviewModel;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.MatchEventModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.data.model.overview.MatchPredictionsModel;
import com.ddastudio.hifivefootball_android.data.model.overview.MatchRecentFormModel;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.Body;
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
 * Created by hongmac on 2017. 12. 26..
 */

public interface MatchesAPI {

    @GET("/v1/matches/{match_id}")
    Flowable<MatchModel> onLoadMatche(@Path("match_id") int matchId);

    @GET("/v1/matches")
    Flowable<List<MatchModel>> onLoadMatches(@Query("comp_id") Integer compid,
                                             @Query("from_date") String fromDate,
                                             @Query("to_date") String toDate,
                                             @Query("offset") int offset,
                                             @Query("limit") int limit);

    @GET("/v1/matches")
    Flowable<List<MatchModel>> onLoadMatches(@Query("comp_id") Integer compid,
                                             @Query("comp_id_list") String complist,
                                             @Query("from_date") String fromDate,
                                             @Query("to_date") String toDate,
                                             @Query("offset") int offset,
                                             @Query("limit") int limit);

    /*----------------------------------------------------------------------------------------*/

    /**
     * 라인업 정보를 가져온다.
     * @param matchId
     * @return
     */
    @GET("/v1/matches/{mid}/lineup")
    Flowable<List<PlayerModel>> onLoadLineup(@Path("mid") int matchId);

    /*----------------------------------------------------------------------------------------*/

    @GET("/v1/matches/{mid}/matchevents")
    Flowable<List<MatchEventModel>> onLoadMatchEvents(@Path("mid") int matchId);

    /*----------------------------------------------------------------------------------------*/

    /**
     * MVP, Worst 플레이어
     * 선수평점 3개...
     * @param match_id
     * @return
     */
    @GET("/v1/matches/{mid}/overview")
    Flowable<MatchOverviewModel> onLoadMatchOverview(@Path("mid") int match_id);


   /**
    * 승무패 데이터 가져오기
    * @param token
    * @param match_id
    * @return
    */
    @GET("/v1/matches/{mid}/predictions")
    Flowable<MatchPredictionsModel> onLoadMatchPredictions(@Header("authorization") String token,
                                                           @Path("mid") int match_id);

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("/v1/matches/{mid}/predictions")
    Flowable<DBResultResponse> postMatchPredictions(@Header("authorization") String token,
                                                    @Path("mid") int match_id,
                                                    @Field("predictions") String predictions);


    /*----------------------------------------------------------------------------------------*/

    /**
     * 최근 5경기 결과를 가져온다.
     * @param match_id
     * @return
     */
    @GET("/v1/matches/{mid}/recentform")
    Flowable<MatchRecentFormModel> onLoadMatchRecentForm(@Path("mid") int match_id);

    /*----------------------------------------------------------------------------------------*/



    /**
     * 유저가 참여한 선수 평점 기록을 time_seq별로 평균, 업, 다운 정보를 가져온다.
     * @param match_id
     * @param player_fa_id
     * @param time_seq
     * @return
     */
    @GET("/v1/matches/{mid}/player/rating/record")
    Flowable<List<PlayerRatingRecordModel>> getPlayerRatingRecord(@Path("mid") int match_id,
                                                                  @Query("player_id") int player_fa_id,
                                                                  @Query("time_seq") String time_seq);

    @GET("/v1/matches/{mid}/player/rating/record")
    Flowable<List<PlayerRatingRecordModel>> getPlayerRatingRecord(@Path("mid") int match_id,
                                                                  @Query("time_seq") String time_seq);

    /**
     * 선수 평점에 참여한 사용자 정보를 가져온다.
     * @param match_id
     * @param player_fa_id
     * @param updown
     * @return
     */
    @GET("/v1/matches/{mid}/player/rating/userlist")
    Flowable<List<PlayerRatingParticipationUser>> getPlayerRatingParticipationUserList(@Path("mid") int match_id,
                                                                                       @Query("player_id") int player_fa_id,
                                                                                       @Query("updown") int updown);

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("/v1/matches/{mid}/player/rating")
    Flowable<DBResultResponse> setPlayerRating(@Header("authorization") String token,
                                               @Path("mid") int match_id,
                                               @Field("playerid") int player_id,
                                               @Field("timeseq") String time_seq,
                                               @Field("rating") double rating,
                                               @Field("updown") int updown);

    //@Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("/v1/matches/player/rating3")
    Flowable<DBResultResponse> setPlayerRating3(@Body PlayerRatingInfoModel abc);




    /*----------------------------------------------------------------------------------------*/




    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("/v1/matches/{mid}/player/comment")
    Flowable<DBResultResponse> setPlayerComment(@Header("authorization") String token,
                                                @Path("mid") int match_id,
                                                @Field("playerid") int player_id,
                                                @Field("ingame") int ingame,
                                                @Field("comment") String comment);

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @PUT("/v1/matches/player/comment/{pc_id}/hifive")
    Flowable<DBResultResponse> setPlayerCommentHifive(@Header("authorization") String token,
                                                      @Path("mid") int match_id,
                                                      @Field("playerid") int player_id,
                                                      @Field("writerid") int writer_id,
                                                      @Field("timeseq") String time_seq,
                                                      @Query("cnt") int hifiveCount);

    /*----------------------------------------------------------------------------------------*/

    /**
     * 선수 코멘트 데이터를 가져온다.
     * @param playerId
     * @param matchId
     * @return
     */
    @GET("/v1/matches/{mid}/comments")
    Flowable<List<PlayerRatingInfoModel>> getPlayerComments(@Path("mid") Integer matchId);

    /*----------------------------------------------------------------------------------------*/

    @GET("/v1/matches/{mid}/content")
    Flowable<List<ContentHeaderModel>> onLoadMatchContentList(@Path("mid") Integer matchId,
                                                              @Query("offset") Integer offset,
                                                              @Query("limit") Integer limit);

    /*----------------------------------------------------------------------------------------*/

    @GET("/v1/matches/{mid}/user/list")
    Flowable<List<UserModel>> onLoadParticipationUsers(@Path("mid") int matchId);

    @GET("/v1/matches/{mid}/user/count")
    Flowable<DBResultResponse> onLoadParticipationUserCount(@Path("mid") int matchId);

}

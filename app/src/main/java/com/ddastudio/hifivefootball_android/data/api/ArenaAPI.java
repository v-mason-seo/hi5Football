package com.ddastudio.hifivefootball_android.data.api;

import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.match_summery.model.CompetitionSectionWrapperModel;
import com.ddastudio.hifivefootball_android.match_summery.model.MatchSectionWrapperModel;
import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingInfoModel;
import com.ddastudio.hifivefootball_android.match_summery.model.PlayerSectionWrapperModel;
import com.ddastudio.hifivefootball_android.match_summery.model.TeamSectionWrapperModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;

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
 * Created by hongmac on 2018. 1. 4..
 */

public interface ArenaAPI {

    @GET("v1/arena/player")
    Flowable<List<PlayerSectionWrapperModel>> onLoadMatchPlayerSection();

    @GET("v1/arena/team")
    Flowable<List<TeamSectionWrapperModel>> onLoadMatchTeamSection();

    @GET("v1/arena/match")
    Flowable<List<MatchSectionWrapperModel>> onLoadMatchSection();

    @GET("v1/arena/competition")
    Flowable<List<CompetitionSectionWrapperModel>> onLoadCompetitionSection();


    /**
     * 매치업 리스트를 가져온다.
     * @param matchType  live, lastest, hit
     * @return
     */
    @GET("v1/arena/match/reaction")
    Flowable<List<MatchModel>> onLoadReactionMatches(@Query("q") String matchType);


    /**
     * 경기중 선수평가를 하기 위한 기본 정보를 가져온다.
     * @param token
     * @param match_id
     * @return
     */
    @GET("v1/arena/{mid}/player/rating")
    Flowable<List<PlayerRatingInfoModel>> getPlayerRatingInfo(@Header("authorization") String token,
                                                              @Path("mid") int match_id,
                                                              @Query("teamid") Integer team_id,
                                                              @Query("playerid") Integer player_id,
                                                              @Query("limit") int limit,
                                                              @Query("offset") int offset);

    /**
     * 선수평가 데이터 입력
     * @param token
     * @param match_id
     * @param player_id
     * @param content_id
     * @param title
     * @param content
     * @param timeseq
     * @param rating
     * @return
     */
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("v1/arena/{mid}/player/rating")
    Flowable<DBResultResponse> onPostPlayerRating(@Header("authorization") String token,
                                                  @Path("mid") int match_id,
                                                  @Field("teamid") int team_id,
                                                  @Field("playerid") int player_id,
                                                  @Field("contentid") Integer content_id,
                                                  @Field("title") String title,
                                                  @Field("content") String content,
                                                  @Field("timeseq") String timeseq,
                                                  @Field("rating") double rating );

    /**
     * 선수 코멘트 데이터를 가져온다.
     * @param token
     * @param match_id
     * @return
     */
    @GET("v1/arena/{mid}/player/comment")
    Flowable<List<PlayerRatingInfoModel>> getPlayerComment(@Path("mid") int match_id);




}

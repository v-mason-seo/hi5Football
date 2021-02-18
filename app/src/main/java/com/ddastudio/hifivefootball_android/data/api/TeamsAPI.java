package com.ddastudio.hifivefootball_android.data.api;

import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by hongmac on 2017. 10. 10..
 */

public interface TeamsAPI {



    @GET("/v1/teams")
    Flowable<List<TeamModel>> rxOnLoadTeamList(@Query("limit") int limit, @Query("offset") int offset);

    @GET("/v1/teams")
    Call<List<TeamModel>> loadTeamList(@Query("limit") int limit, @Query("offset") int offset);

    @GET("/v1/teams/{id}")
    Flowable<TeamModel> onLoadTeam(@Path("id") int teamId);

    @GET("/v1/teams/{id}/players")
    Flowable<List<PlayerModel>> onLoadPlayers(@Path("id") int teamId);

    @GET("/v1/teams/{id}/fixture")
    Flowable<List<MatchModel>> onLoadTeamFixtures(@Path("id") int teamId);

    @GET("/v1/teams/{id}/fixture")
    Flowable<List<ContentHeaderModel>> onLoadTeamContent(@Path("id") int teamId);

    /**
     * 팀 관련 글 가져오기
     * @param token
     * @param teamId
     * @param boardId
     * @param limit
     * @param offset
     * @return
     */
    @GET("/v1/teams/{id}/content")
    Flowable<List<ContentHeaderModel>> onLoadTeamContentList(@Header("authorization") String token,
                                                             @Path("id") Integer teamId,
                                                             @Query("boardid") Integer boardId,
                                                             @Query("limit") Integer limit,
                                                             @Query("offset") Integer offset);

    /**
     * 팀이름 변경 요청
     * @param token
     * @param teamId
     * @param beforeName
     * @param afterName
     * @return
     */
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("/v1/teams/{id}/request/changename")
    Flowable<DBResultResponse> requestChangeTeamName(@Header("authorization") String token,
                                                       @Path("id") int teamId,
                                                       @Field("bname") String beforeName,
                                                       @Field("aname") String afterName);
}

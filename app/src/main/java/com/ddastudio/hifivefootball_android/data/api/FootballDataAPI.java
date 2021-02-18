package com.ddastudio.hifivefootball_android.data.api;

import com.ddastudio.hifivefootball_android.data.model.convert.CompetitionFixtureModel;
import com.ddastudio.hifivefootball_android.data.model.convert.CompetitionFDModel;
import com.ddastudio.hifivefootball_android.data.model.football.CompetitionModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.LineupModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.MatchContainerModel;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.StandingModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by hongmac on 2017. 10. 13..
 */

public interface FootballDataAPI {


    @GET("/v1/footballdata/competitions/")
    Flowable<List<CompetitionModel>> onLoadCompetitions();

//    @GET("/v1/footballdata/matches/{match_date}")
//    Flowable<MatchContainerModel> onLoadMatches(@Path("match_date") String matchDate);

//    @GET("/v1/footballdata/matches/{match_date}")
//    Flowable<MatchContainerModel> onLoadMatches(@Path("match_date") String matchDate, @Query("comp_id") int competitionId);
    //http://127.0.0.1:5400/v1/fixtures?match_date=20171204
    //http://127.0.0.1:5400/v1/fixtures/matches?match_date=2017-12-04

    @GET("/v1/fixtures")
    Flowable<MatchContainerModel> onLoadMatches(@Query("match_date") String matchDate);

    @GET("/v1/fixtures")
    Flowable<MatchContainerModel> onLoadMatches(@Query("match_date") String matchDate, @Query("comp_id") int competitionId);

    @GET("/v1/fixtures")
    Flowable<MatchContainerModel> onLoadMatches(@Query("match_date") String matchDate, @Query("comp_id") Integer competitionId, @Query("team_id") Integer teamId);

    @GET("/v1/footballdata/standings/{comp_id}")
    Flowable<List<StandingModel>> onLoadStandings(@Path("comp_id") int competitionId);

//    @GET("/v1/footballdata/lineup/{match_id}")
//    Flowable<List<LineupModel>> onLoadLineup(@Path("match_id") int matchId);

    @GET("/v1/footballdata/lineup/{match_id}")
    Flowable<List<LineupModel>> onLoadLineup(@Path("match_id") int matchId, @Query("team_id") int teamId);

//    @GET("/v1/footballdata/matchevents/{match_id}")
//    Flowable<List<MatchEventModel>> onLoadMatchEvents(@Path("match_id") int matchId);

//    @GET("/v1/footballdata/player/{player_id}")
//    Flowable<PlayerModel> onLoadPlayer(@Path("player_id") int playerId);

    @GET("/v1/players/{player_id}")
    Flowable<PlayerModel> onLoadPlayer(@Path("player_id") int playerId);

//    @GET("/v1/footballdata/player/{player_id}")
//    Flowable<PlayerModel> onLoadPlayer(@Path("player_id") int playerId, @Query("id_type") int idType);

    @GET("/v1/players/{player_id}")
    Flowable<PlayerModel> onLoadPlayer(@Path("player_id") int playerId, @Query("id_type") int idType);

//    @GET("/v1/footballdata/player")
//    Flowable<List<PlayerModel>> onLoadPlayerList(@Query("offset") int offset, @Query("limit") int limit);

    @GET("/v1/players")
    Flowable<List<PlayerModel>> onLoadPlayerList(@Query("offset") int offset, @Query("limit") int limit);

//    @GET("/v1/footballdata/team/{team_id}")
//    Flowable<TeamModel> onLoadTeam(@Path("team_id") int teamId);

    @GET("/v1/teams/{team_id}")
    Flowable<TeamModel> onLoadTeam(@Path("team_id") int teamId);

//    @GET("/v1/footballdata/team/{team_id}")
//    Flowable<TeamModel> onLoadTeam(@Path("team_id") int teamId, @Query("id_type") int idType);

//    @GET("/v1/footballdata/team")
//    Flowable<List<TeamModel>> onLoadTeamList(@Query("offset") int offset, @Query("limit") int limit);

    @GET("/v1/teams")
    Flowable<List<TeamModel>> onLoadTeamList(@Query("offset") int offset, @Query("limit") int limit);

    /*-----------------------------------------------------------------------------*/

    /**
     * Competition 정보를 가져온다.
     * @param season
     * @return
     */
    @Headers("X-Auth-Token: c124824e2dfc47aeb8e711b54a944f5b")
    @GET("competitions/")
    Flowable<List<CompetitionFDModel>> onLoadCompetitions(@Query("season") String season);

    /**
     * Competition 경기 일정을 가져온다.
     * @param competition_id
     * @param matchday Integer /[1-4]*[0-9]* /
     * @return
     */
    @Headers("X-Auth-Token: c124824e2dfc47aeb8e711b54a944f5b")
    @GET("competitions/{competition_id}/fixtures")
    Flowable<CompetitionFixtureModel> onLoadCompetitionFixture(@Path("competition_id") int competition_id
            /*, @Query("matchday") int matchday*/);
}

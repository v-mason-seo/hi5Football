package com.ddastudio.hifivefootball_android.data.api;

import android.support.annotation.Nullable;

import com.ddastudio.hifivefootball_android.data.model.footballdata.MatchContainerModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by hongmac on 2018. 1. 2..
 */

public interface FixturesAPI {

    @GET("/v1/fixtures")
    Flowable<MatchContainerModel> onLoadMatches(@Query("match_date") String matchDate);

    @GET("/v1/fixtures")
    Flowable<MatchContainerModel> onLoadMatches(@Query("match_date") String matchDate, @Query("comp_id") int competitionId);

//    /**
//     *
//     * @param limit
//     * @param offset
//     * @return
//     */
//    @GET("/v1/fixtures/next")
//    Flowable<List<MatchModel>> onLoadNextFixtures(@Query("limit") Integer limit, @Query("offset") Integer offset);

    /**
     *
     * @param limit
     * @param offset
     * @param teamId
     * @return
     */
    @GET("/v1/fixtures/next")
    Flowable<List<MatchModel>> onLoadNextFixtures(@Query("limit") Integer limit,
                                                  @Query("offset") Integer offset,
                                                  @Query("teamid") @Nullable Integer teamId);

//    /**
//     *
//     * @param limit
//     * @param offset
//     * @return
//     */
//    @GET("/v1/fixtures/pre")
//    Flowable<List<MatchModel>> onLoadPreFixtures(@Query("limit") Integer limit,
//                                                 @Query("offset") Integer offset);

    /**
     *
     * @param limit
     * @param offset
     * @param teamId
     * @return
     */
    @GET("/v1/fixtures/pre")
    Flowable<List<MatchModel>> onLoadPreFixtures(@Query("limit") Integer limit,
                                                 @Query("offset") Integer offset,
                                                 @Query("teamid") @Nullable Integer teamId);

}

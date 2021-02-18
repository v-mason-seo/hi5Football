package com.ddastudio.hifivefootball_android.data.api;

import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.football.CompModel;
import com.ddastudio.hifivefootball_android.data.model.football.CompetitionModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.StandingModel;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by hongmac on 2017. 12. 30..
 */

public interface CompetitionsAPI {

    /**
     * 리그정보를 가져온다.
     * @return
     */
    @GET("/v1/competitions/")
    Flowable<List<CompModel>> onLoadCompetitions(@Query("active") int isActive);

    /**
     * 리그정보를 가져온다.
     * @param competitionId
     * @return
     */
    @GET("/v1/competitions/{comp_id}")
    Flowable<List<CompetitionModel>> onLoadCompetition(@Path("comp_id") int competitionId);

    /**
     * 리그 순위표를 가져온다.
     * @param competitionId
     * @return
     */
    @GET("/v1/competitions/{comp_id}/standing")
    Flowable<List<StandingModel>> onLoadLeagueTable(@Path("comp_id") int competitionId);

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 컴피티션 관련 글을 가져온다.
     * @param token
     * @param competitionId
     * @param limit
     * @param offset
     * @return
     */
    @GET("/v1/competitions/{comp_id}/content")
    Flowable<List<ContentHeaderModel>> onLoadCompetitionContentList(@Header("authorization") String token,
                                                                    @Path("comp_id") Integer competitionId,
                                                                    @Query("limit") Integer limit,
                                                                    @Query("offset") Integer offset);
}

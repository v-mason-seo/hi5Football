package com.ddastudio.hifivefootball_android.data.manager;

import android.content.Context;

import com.ddastudio.hifivefootball_android.BuildConfig;
import com.ddastudio.hifivefootball_android.data.api.FootballDataAPI;
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
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hongmac on 2017. 10. 13..
 */

public class FootballDataManager {

    static FootballDataManager mInstance;
    Retrofit mRetrofit;
    FootballDataAPI mAPI;

    public FootballDataManager() {
        initRetrofit();
    }

    public static FootballDataManager getInstance() {

        if ( mInstance == null ) {
            synchronized (FootballDataManager.class) {
                if ( mInstance == null ) {
                    mInstance = new FootballDataManager();
                }
            }
        }

        return mInstance;
    }

    private void initRetrofit() {

        if ( BuildConfig.DEBUG ) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            // add your other interceptors …
            // add logging as last interceptor
            httpClient.addInterceptor(logging);  // <-- this is the important line!

            mRetrofit = new Retrofit.Builder()
                    //.baseUrl("http://api.hifivesoccer.com:5400")
                    .baseUrl(BuildConfig.ARENA_SERVER_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClient.build())
                    .build();

        } else {

            mRetrofit = new Retrofit.Builder()
                    //.baseUrl("http://api.hifivesoccer.com:5400")
                    .baseUrl(BuildConfig.ARENA_SERVER_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }

        mAPI = mRetrofit.create(FootballDataAPI.class);

    }

    /*---------------------------------------------------------------------------------------------*/


    public Flowable<List<CompetitionModel>> onLoadCompetitions() {

        Flowable<List<CompetitionModel>> observable = mAPI.onLoadCompetitions();

        return  observable.subscribeOn(Schedulers.io());
    }


    public Flowable<MatchContainerModel> onLoadMatches(String matchDate) {

        Flowable<MatchContainerModel> observable = mAPI.onLoadMatches(matchDate);

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<MatchContainerModel> onLoadMatches(String matchDate, int competitionId) {

        Flowable<MatchContainerModel> observable = mAPI.onLoadMatches(matchDate, competitionId);

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<MatchContainerModel> onLoadMatches(String matchDate, Integer competitionId, Integer teamId) {

        Flowable<MatchContainerModel> observable = mAPI.onLoadMatches(matchDate, competitionId, teamId);

        return  observable.subscribeOn(Schedulers.io());
    }


    public Flowable<List<StandingModel>> onLoadStandings(int competitionId) {

        Flowable<List<StandingModel>> observable = mAPI.onLoadStandings(competitionId);

        return  observable.subscribeOn(Schedulers.io());
    }

//    public Flowable<List<LineupModel>> onLoadLineup(int match_id) {
//
//        Flowable<List<LineupModel>> observable = mAPI.onLoadLineup(match_id);
//
//        return  observable.subscribeOn(Schedulers.io());
//    }

    public Flowable<List<LineupModel>> onLoadLineup(int match_id, int team_id) {

        Flowable<List<LineupModel>> observable = mAPI.onLoadLineup(match_id, team_id);

        return  observable.subscribeOn(Schedulers.io());
    }

//    public Flowable<List<MatchEventModel>> onLoadMatchEvents(int match_id) {
//
//        Flowable<List<MatchEventModel>> observable = mAPI.onLoadMatchEvents(match_id);
//
//        return  observable.subscribeOn(Schedulers.io());
//    }

    public Flowable<PlayerModel> onLoadPlayer(int playerId) {

        Flowable<PlayerModel> observable = mAPI.onLoadPlayer(playerId);

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<PlayerModel> onLoadPlayer(int playerId, int idType) {

        Flowable<PlayerModel> observable = mAPI.onLoadPlayer(playerId, idType);

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<List<PlayerModel>> onLoadPlayerList(int offset, int limit) {

        Flowable<List<PlayerModel>> observable = mAPI.onLoadPlayerList(offset, limit);

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<TeamModel> onLoadTeam(int teamId) {

        Flowable<TeamModel> observable = mAPI.onLoadTeam(teamId);

        return  observable.subscribeOn(Schedulers.io());
    }

//    public Flowable<TeamModel> onLoadTeam(int teamId, int idType) {
//
//        Flowable<TeamModel> observable = mAPI.onLoadTeam(teamId, idType);
//
//        return  observable.subscribeOn(Schedulers.io());
//    }

    public Flowable<List<TeamModel>> onLoadTeamList(int offset, int limit) {

        Flowable<List<TeamModel>> observable = mAPI.onLoadTeamList(offset, limit);

        return  observable.subscribeOn(Schedulers.io());
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * Competition 정보를 가져온다.
     * @param season
     * @return
     */
    public Flowable<List<CompetitionFDModel>> onLoadCompetitions(String season) {

        Flowable<List<CompetitionFDModel>> observable = mAPI.onLoadCompetitions(season);

        return  observable.subscribeOn(Schedulers.io());
    }

    /**
     * Competition 경기 일정을 가져온다.
     * @param competition_id
     * @param matchday
     * @return
     */
    public Flowable<CompetitionFixtureModel> onLoadCompetitionFixture(int competition_id/*, int matchday*/) {

        Flowable<CompetitionFixtureModel> observable = mAPI.onLoadCompetitionFixture(competition_id/*, matchday*/);

        return  observable.subscribeOn(Schedulers.io());
    }
}

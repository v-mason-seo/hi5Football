package com.ddastudio.hifivefootball_android.data.manager;

import com.ddastudio.hifivefootball_android.BuildConfig;
import com.ddastudio.hifivefootball_android.data.api.TeamsAPI;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
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
 * Created by hongmac on 2017. 10. 10..
 */

public class TeamsManager {

    static TeamsManager mInstance;
    Retrofit mRetrofit;
    TeamsAPI mAPI;

    private TeamsManager() {

        initRetrofit();
    }

    public static TeamsManager getInstance() {

        if ( mInstance == null ) {
            synchronized (TeamsManager.class) {
                if ( mInstance == null ) {
                    mInstance = new TeamsManager();
                }
            }
        }

        return mInstance;
    }

    private void initRetrofit() {

        if ( BuildConfig.DEBUG ) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
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

        mAPI = mRetrofit.create(TeamsAPI.class);
    }

    /*---------------------------------------------------------------------------------------------*/

    public Flowable<List<TeamModel>> onLoadTeamList(int limit, int offset) {

        Flowable<List<TeamModel>> observable = mAPI.rxOnLoadTeamList(limit, offset);

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<TeamModel> onLoadTeam(int teamId) {

        Flowable<TeamModel> observable = mAPI.onLoadTeam(teamId);

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<List<MatchModel>> onLoadTeamFixtures(int teamId) {

        Flowable<List<MatchModel>> observable = mAPI.onLoadTeamFixtures(teamId);

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<List<PlayerModel>> onLoadPlayers(int teamId) {

        Flowable<List<PlayerModel>> observable = mAPI.onLoadPlayers(teamId);

        return  observable.subscribeOn(Schedulers.io());
    }

    /**
     * 팀 관련 글 가져오기
     * @param token
     * @param playerId
     * @param boardId
     * @param limit
     * @param offset
     * @return
     */
    public Flowable<List<ContentHeaderModel>> onLoadTeamContentList(String token,
                                                                    Integer playerId,
                                                                    Integer boardId,
                                                                    Integer limit,
                                                                    Integer offset) {

        Flowable<List<ContentHeaderModel>> observable
                = mAPI.onLoadTeamContentList(token, playerId, boardId, limit, offset);

        return  observable.subscribeOn(Schedulers.io());
    }

    /**
     * 팀이름 변경 요청
     * @param token
     * @param playerId
     * @param beforeName
     * @param afterName
     * @return
     */
    public Flowable<DBResultResponse> requestChangeTeamName(String token,
                                                            int playerId,
                                                            String beforeName,
                                                            String afterName) {

        Flowable<DBResultResponse> observable
                = mAPI.requestChangeTeamName(token, playerId, beforeName, afterName);

        return  observable.subscribeOn(Schedulers.io());
    }
}

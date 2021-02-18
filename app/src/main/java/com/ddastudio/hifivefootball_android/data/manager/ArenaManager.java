package com.ddastudio.hifivefootball_android.data.manager;

import com.ddastudio.hifivefootball_android.BuildConfig;
import com.ddastudio.hifivefootball_android.data.api.ArenaAPI;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.match_summery.model.CompetitionSectionWrapperModel;
import com.ddastudio.hifivefootball_android.match_summery.model.MatchSectionWrapperModel;
import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingInfoModel;
import com.ddastudio.hifivefootball_android.match_summery.model.PlayerSectionWrapperModel;
import com.ddastudio.hifivefootball_android.match_summery.model.TeamSectionWrapperModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hongmac on 2018. 1. 4..
 */

public class ArenaManager {

    static ArenaManager mInstance;
    Retrofit mRetrofit;
    ArenaAPI mAPI;

    ArenaManager() {
        initRetrofit();
    }

    public static ArenaManager getInstance() {

        if ( mInstance == null ) {
            synchronized (ArenaManager.class) {
                if ( mInstance == null ) {
                    mInstance = new ArenaManager();
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

        mAPI = mRetrofit.create(ArenaAPI.class);
    }

    /*---------------------------------------------------------------------------------------------*/

    public Flowable<List<PlayerSectionWrapperModel>> onLoadMatchPlayerSection() {

        Flowable<List<PlayerSectionWrapperModel>> observable = mAPI.onLoadMatchPlayerSection();

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<List<TeamSectionWrapperModel>> onLoadMatchTeamSection() {

        Flowable<List<TeamSectionWrapperModel>> observable = mAPI.onLoadMatchTeamSection();

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<List<MatchSectionWrapperModel>> onLoadMatchSection() {

        Flowable<List<MatchSectionWrapperModel>> observable = mAPI.onLoadMatchSection();

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<List<CompetitionSectionWrapperModel>> onLoadCompetitionSection() {

        Flowable<List<CompetitionSectionWrapperModel>> observable = mAPI.onLoadCompetitionSection();

        return  observable.subscribeOn(Schedulers.io());
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 매치업 리스트를 가져온다.
     * @param matchType
     * @return
     */
    public Flowable<List<MatchModel>> onLoadReactionMatches(String matchType) {

        Flowable<List<MatchModel>> observable = mAPI.onLoadReactionMatches(matchType);

        return  observable.subscribeOn(Schedulers.io());
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 경기중 선수평가를 하기 위한 기본 정보를 가져온다.
     * @param token
     * @param match_id
     * @return
     */
    public Flowable<List<PlayerRatingInfoModel>> getPlayerRatingInfo(String token, int match_id, Integer team_id, Integer player_id, int limit, int offset) {

        Flowable<List<PlayerRatingInfoModel>> observable = mAPI.getPlayerRatingInfo(token, match_id, team_id, player_id, limit, offset);

        return  observable.subscribeOn(Schedulers.io());
    }

    /**
     * 경기중 선수평가를 하기 위한 기본 정보를 가져온다. ( limit, offset 기본값으로 설정 )
     * @param token
     * @param match_id
     * @return
     */
    public Flowable<List<PlayerRatingInfoModel>> getPlayerRatingInfo(String token, int match_id, Integer team_id, Integer player_id) {

        int limit = 1000;
        int offset = 0;
        return  getPlayerRatingInfo(token, match_id, team_id, player_id, limit, offset);
    }

    /**
     *
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
    public Flowable<DBResultResponse> onPostPlayerRating(String token,
                                                         int match_id,
                                                         int team_id,
                                                         int player_id,
                                                         Integer content_id,
                                                         String title,
                                                         String content,
                                                         String timeseq,
                                                         double rating ) {

        Flowable<DBResultResponse> observable
                = mAPI.onPostPlayerRating(token, match_id, team_id, player_id, content_id, title, content, timeseq, rating);

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<List<PlayerRatingInfoModel>> onLoadPlayerComment(int match_id) {

        Flowable<List<PlayerRatingInfoModel>> observable
                = mAPI.getPlayerComment(match_id);

        return  observable.subscribeOn(Schedulers.io());
    }


}

package com.ddastudio.hifivefootball_android.data.manager;

import android.content.Context;

import com.ddastudio.hifivefootball_android.BuildConfig;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.api.CompetitionsAPI;
import com.ddastudio.hifivefootball_android.data.model.football.CompModel;
import com.ddastudio.hifivefootball_android.data.model.football.CompetitionModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.StandingModel;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hongmac on 2017. 12. 30..
 */

public class CompetitionsManager {

    static CompetitionsManager mInstance;
    Retrofit mRetrofit;
    CompetitionsAPI mAPI;

    public CompetitionsManager() {

        initRetrofit();
    }

    public static CompetitionsManager getInstance() {

        if ( mInstance == null ) {
            synchronized (CompetitionsManager.class) {
                if ( mInstance == null ) {
                    mInstance = new CompetitionsManager();
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

        mAPI = mRetrofit.create(CompetitionsAPI.class);

    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 리그정보를 가져온다.
     * @return
     */
    public Flowable<List<CompModel>> onLoadCompetitions() {

        return  onLoadCompetitions(0);
    }

    /**
     * 리그정보를 가져온다.
     * @return
     */
    public Flowable<List<CompModel>> onLoadCompetitions(int isActive) {

        Flowable<List<CompModel>> observable = mAPI.onLoadCompetitions(isActive);

        return  observable.subscribeOn(Schedulers.io());
    }

    /**
     * 리그정보를 가져온다.
     * @param competitionId
     * @return
     */
    public Flowable<List<CompetitionModel>> onLoadCompetition(int competitionId) {

        Flowable<List<CompetitionModel>> observable = mAPI.onLoadCompetition(competitionId);

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<List<StandingModel>> onLoadLeagueTable(int competitionId) {

        Flowable<List<StandingModel>> observable = mAPI.onLoadLeagueTable(competitionId);

        return  observable.subscribeOn(Schedulers.io());
    }

    /**
     * 컴피티션 관련글을 가져온다.
     * @param token
     * @param competitionId
     * @param limit
     * @param offset
     * @return
     */
    public Flowable<List<ContentHeaderModel>> onLoadCompetitionContentList(String token,
                                                                      Integer competitionId,
                                                                      Integer limit,
                                                                      Integer offset) {

        Flowable<List<ContentHeaderModel>> observable
                = mAPI.onLoadCompetitionContentList(token, competitionId, limit, offset);

        return  observable.subscribeOn(Schedulers.io());
    }
}

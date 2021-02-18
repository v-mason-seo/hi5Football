package com.ddastudio.hifivefootball_android.data.manager;

import android.content.Context;
import android.support.annotation.Nullable;

import com.ddastudio.hifivefootball_android.BuildConfig;
import com.ddastudio.hifivefootball_android.data.api.FixturesAPI;
import com.ddastudio.hifivefootball_android.data.model.footballdata.MatchContainerModel;
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
 * Created by hongmac on 2018. 1. 2..
 */

public class FixturesManager {

    static FixturesManager mInstance;
    Retrofit mRetrofit;
    FixturesAPI mAPI;

    public FixturesManager() {

        initRetrofit();
    }

    public static FixturesManager getInstance() {

        if ( mInstance == null ) {
            synchronized (FixturesManager.class) {
                if ( mInstance == null ) {
                    mInstance = new FixturesManager();
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

        mAPI = mRetrofit.create(FixturesAPI.class);
    }

    /*---------------------------------------------------------------------------------------------*/

    public Flowable<MatchContainerModel> onLoadMatches(String matchDate) {

        Flowable<MatchContainerModel> observable = mAPI.onLoadMatches(matchDate);

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<MatchContainerModel> onLoadMatches(String matchDate, int competitionId) {

        Flowable<MatchContainerModel> observable = mAPI.onLoadMatches(matchDate, competitionId);

        return  observable.subscribeOn(Schedulers.io());
    }

    /*----------------------------------------------------------------------------------------------*/

    public Flowable<List<MatchModel>> onLoadNextFixtures(Integer limit, Integer offset) {

        return onLoadNextFixtures(limit, offset, null);
    }

    public Flowable<List<MatchModel>> onLoadNextFixtures(Integer limit, Integer offset, @Nullable Integer teamId) {

        Flowable<List<MatchModel>> observable = mAPI.onLoadNextFixtures(limit, offset, teamId);

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<List<MatchModel>> onLoadPreFixtures(Integer limit, Integer offset) {

        return  onLoadPreFixtures(limit, offset, null);
    }

    public Flowable<List<MatchModel>> onLoadPreFixtures(Integer limit, Integer offset, @Nullable Integer teamId) {

        Flowable<List<MatchModel>> observable = mAPI.onLoadPreFixtures(limit, offset, teamId);

        return  observable.subscribeOn(Schedulers.io());
    }

    /*----------------------------------------------------------------------------------------------*/
}

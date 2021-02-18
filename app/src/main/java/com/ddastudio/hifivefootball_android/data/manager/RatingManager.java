package com.ddastudio.hifivefootball_android.data.manager;

import android.content.Context;

import com.ddastudio.hifivefootball_android.BuildConfig;
import com.ddastudio.hifivefootball_android.data.api.ArenaAPI;
import com.ddastudio.hifivefootball_android.data.api.RatingAPI;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.annotations.Nullable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.Path;

/**
 * Created by hongmac on 2018. 2. 26..
 */

public class RatingManager {

    Context mContext;
    Retrofit mRetrofit;
    RatingAPI mAPI;

    public RatingManager(Context context) {

        this.mContext = context;
        initRetrofit();
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

        mAPI = mRetrofit.create(RatingAPI.class);
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 선수평가를 위한 라인업 정보를 가져온다.
     * @param match_id
     * @return
     */
    public Flowable<List<PlayerModel>> onLoadLineup(int match_id) {

        Flowable<List<PlayerModel>> observable = mAPI.onLoadLineup(match_id);

        return  observable.subscribeOn(Schedulers.io());
    }

    /**
     *
     * @param token
     * @param matchId
     * @param playerId
     * @param teamId
     * @param timeSeq
     * @param rating
     * @param updown
     * @param comment
     * @return
     */
    public Flowable<DBResultResponse> onPostPlayerRating(String token,
                                                         Integer matchId,
                                                         Integer playerId,
                                                         Integer teamId,
                                                         String timeSeq,
                                                         Float rating,
                                                         String updown,
                                                         String comment) {

        Flowable<DBResultResponse> observable
                = mAPI.onPostPlayerRating(token, matchId, playerId, teamId, timeSeq, rating, updown, comment);

        return  observable.subscribeOn(Schedulers.io());
    }
}

package com.ddastudio.hifivefootball_android.data.manager;

import android.content.Context;

import com.ddastudio.hifivefootball_android.BuildConfig;
import com.ddastudio.hifivefootball_android.data.api.MatchTalkAPI;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.community.MatchTalkModel;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hongmac on 2018. 3. 2..
 */

public class MatchTalkManager {

    static MatchTalkManager mInstance;
    Retrofit mRetrofit;
    MatchTalkAPI mAPI;

    public MatchTalkManager() {

        initRetrofit();
    }

    public static MatchTalkManager getInstance() {

        if ( mInstance == null ) {
            synchronized (MatchTalkManager.class) {
                if ( mInstance == null ) {
                    mInstance = new MatchTalkManager();
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
                    //.baseUrl("http://api.hifivesoccer.com:5300")
                    .baseUrl(BuildConfig.HIFIVE_SERVER_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClient.build())
                    .build();

        } else {

            mRetrofit = new Retrofit.Builder()
                    //.baseUrl("http://api.hifivesoccer.com:5300")
                    .baseUrl(BuildConfig.HIFIVE_SERVER_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }

        mAPI = mRetrofit.create(MatchTalkAPI.class);

    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 매치토크 데이터를 가져온다.
     * @param token
     * @param matchId
     * @param userName
     * @param limit
     * @param offset
     * @return
     */
    public Flowable<List<MatchTalkModel>> onLoadMatchTalks(String token,
                                                           Integer matchId,
                                                           String userName,
                                                           Integer limit,
                                                           Integer offset) {

        Flowable<List<MatchTalkModel>> observable
                = mAPI.onLoadMatchTalks(token, matchId, userName, limit, offset);

        return  observable.subscribeOn(Schedulers.io());
    }

    /**
     * 매치토크 데이터를 가져온다.
     * @param token
     * @param matchId
     * @param userName
     * @return
     */
    public Flowable<List<MatchTalkModel>> onLoadMatchTalks(String token,
                                                           Integer matchId,
                                                           String userName) {

        return onLoadMatchTalks(token, matchId, userName, null, null);

    }


    /**
     * 베스트 매치토크 데이터를 가져온다.
     * @param token
     * @param matchId
     * @return
     */
    public Flowable<List<MatchTalkModel>> onLoadBestMatchTalks(String token,
                                                           Integer matchId) {

        Flowable<List<MatchTalkModel>> observable
                = mAPI.onLoadBestMatchTalks(token, matchId);

        return  observable.subscribeOn(Schedulers.io());
    }


    /**
     * 유저의 매치토크 데이터를 가져온다.
     * @param token
     * @param matchId
     * @return
     */
    public Flowable<List<MatchTalkModel>> onLoadUserMatchTalks(String token,
                                                               Integer matchId) {

        Flowable<List<MatchTalkModel>> observable
                = mAPI.onLoadUserMatchTalks(token, matchId);

        return  observable.subscribeOn(Schedulers.io());
    }

    /*--------------------------------------------------------------------------------------*/

    /**
     * 매치토크 생성
     * @param token
     * @param matchid
     * @param content
     * @param cellType
     * @param status
     * @return
     */
    public Flowable<DBResultResponse> onCreateMatchTalk(String token,
                                                        Integer matchid,
                                                        String content,
                                                        Integer cellType,
                                                        String status) {

        Flowable<DBResultResponse> observable
                = mAPI.onCreateMatchTalk(token, matchid, content, cellType, status);

        return  observable.subscribeOn(Schedulers.io());
    }
}

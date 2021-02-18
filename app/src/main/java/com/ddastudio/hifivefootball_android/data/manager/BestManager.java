package com.ddastudio.hifivefootball_android.data.manager;

import com.ddastudio.hifivefootball_android.BuildConfig;
import com.ddastudio.hifivefootball_android.data.api.BestAPI;
import com.ddastudio.hifivefootball_android.content_list.model.BestContentsHeaderModel;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hongmac on 2017. 9. 5..
 */

public class BestManager {

    static BestManager mInstance;
    Retrofit mRetrofit;
    BestAPI mAPI;

    public BestManager() {

        initRetrofit();
    }

    public static BestManager getInstance() {

        if ( mInstance == null ) {
            synchronized (BestManager.class) {
                if ( mInstance == null ) {
                    mInstance = new BestManager();
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
                    //.baseUrl("http://api.hifivesoccer.com:5300/v1/")
                    .baseUrl(BuildConfig.HIFIVE_SERVER_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClient.build())
                    .build();

        } else {

            mRetrofit = new Retrofit.Builder()
                    //.baseUrl("http://api.hifivesoccer.com:5300/v1/")
                    .baseUrl(BuildConfig.HIFIVE_SERVER_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }

        mAPI = mRetrofit.create(BestAPI.class);
    }

    /*---------------------------------------------------------------------------------------------*/

    public Flowable<List<BestContentsHeaderModel>> getALLBestContentList(String best_id,
                                                                String best_roll,
                                                                int limit,
                                                                int offset) {

        Flowable<List<BestContentsHeaderModel>> observable
                = mAPI.getALLBestContentList(best_id, best_roll, limit, offset);

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<List<BestContentsHeaderModel>> getBestContentList(int boardid,
                                                             String best_id,
                                                             String best_roll,
                                                             int limit,
                                                             int offset) {

        Flowable<List<BestContentsHeaderModel>> observable
                = mAPI.getBestContentList(boardid, best_id, best_roll, limit, offset);

        return  observable.subscribeOn(Schedulers.io());
    }

    /*---------------------------------------------------------------------------------------------*/

    public Flowable<List<ContentHeaderModel>> getALLBestContentList2(String token, String bestRoll) {

        Flowable<List<ContentHeaderModel>> observable
                = mAPI.getALLBestContentList2(token, bestRoll);

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<List<ContentHeaderModel>> getBestContentList2(String token,
                                                                       int boardid,
                                                                       String bestRoll) {

        Flowable<List<ContentHeaderModel>> observable
                = mAPI.getBestContentList2(token, boardid, bestRoll);

        return  observable.subscribeOn(Schedulers.io());
    }
}

package com.ddastudio.hifivefootball_android.data.manager;

import com.ddastudio.hifivefootball_android.BuildConfig;
import com.ddastudio.hifivefootball_android.data.api.SettingsAPI;
import com.ddastudio.hifivefootball_android.board.model.BoardMasterModel;
import com.ddastudio.hifivefootball_android.data.model.NotificationTypeMasterModel;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hongmac on 2017. 11. 30..
 */

public class SettingsManager {

    static SettingsManager mInstance;
    Retrofit mRetrofit;
    SettingsAPI mAPI;

    private SettingsManager() {

        initRetrofit();
    }

    public static SettingsManager getInstance() {

        if ( mInstance == null ) {
            synchronized (SettingsManager.class) {
                if ( mInstance == null ) {
                    mInstance = new SettingsManager();
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

        mAPI = mRetrofit.create(SettingsAPI.class);
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 게시판 타입 데이터를 가져온다.
     * @param isUseData
     * @return
     */
    public Flowable<List<BoardMasterModel>> getBoardList(boolean isUseData) {

        Flowable<List<BoardMasterModel>> observable = mAPI.getBoardList(isUseData == true ? 1 : 0);

        return  observable.subscribeOn(Schedulers.io());
    }

    /**
     * 알람 타입 데이터를 가져온다.
     * @return
     */
    public Flowable<List<NotificationTypeMasterModel>> getNotificationType() {

        Flowable<List<NotificationTypeMasterModel>> observable = mAPI.getNotificationType();

        return  observable.subscribeOn(Schedulers.io());
    }
}

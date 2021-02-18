package com.ddastudio.hifivefootball_android.data.manager;

import android.content.Context;

import com.ddastudio.hifivefootball_android.BuildConfig;
import com.ddastudio.hifivefootball_android.data.api.NotificationAPI;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.community.NotificationModel;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hongmac on 2017. 10. 18..
 */

public class NotificationManager {

    static NotificationManager mInstance;
    Retrofit mRetrofit;
    NotificationAPI mAPI;

    public NotificationManager() {
        initRetrofit();
    }

    public static NotificationManager getInstance() {

        if ( mInstance == null ) {
            synchronized (NotificationManager.class) {
                if ( mInstance == null ) {
                    mInstance = new NotificationManager();
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

        mAPI = mRetrofit.create(NotificationAPI.class);
    }

    /*---------------------------------------------------------------------------------------------*/

    public Flowable<List<NotificationModel>> getNotification(String token) {

        Flowable<List<NotificationModel>> observable = mAPI.getNotification(token);

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<List<NotificationModel>> getNotification(String token, boolean isConfirm) {

        Flowable<List<NotificationModel>> observable = mAPI.getNotification(token, isConfirm ? 1 : 0);

        return  observable.subscribeOn(Schedulers.io());
    }


    public Flowable<DBResultResponse> confirmNotification(String token, int notificationid) {

        Flowable<DBResultResponse> observable
                = mAPI.confirmNotification(token, notificationid);

        return  observable.subscribeOn(Schedulers.io());
    }

    /**
     * 내소식 읽음 처리
     * @param token
     * @param notificationTypeId
     * @param senderId
     * @return
     */
    public Flowable<DBResultResponse> confirmNotification2(String token, int notificationTypeId, int contentId) {

        Flowable<DBResultResponse> observable
                = mAPI.confirmNotification2(token, notificationTypeId, contentId);

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<DBResultResponse> confirmAllNotification(String token, int notificationid) {

        Flowable<DBResultResponse> observable
                = mAPI.confirmNotification(token, notificationid);

        return  observable.subscribeOn(Schedulers.io());
    }
}

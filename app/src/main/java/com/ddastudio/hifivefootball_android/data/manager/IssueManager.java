package com.ddastudio.hifivefootball_android.data.manager;

import android.content.Context;
import android.util.Log;

import com.ddastudio.hifivefootball_android.BuildConfig;
import com.ddastudio.hifivefootball_android.data.api.IssueAPI;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.IssueModel;

import io.reactivex.Flowable;
import io.reactivex.annotations.Nullable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hongmac on 2017. 9. 28..
 */

public class IssueManager {

    static IssueManager mInstance;
    Retrofit mRetrofit;
    IssueAPI mAPI;

    public IssueManager() {
        initRetrofit();
    }

    public static IssueManager getInstance() {

        if ( mInstance == null ) {
            synchronized (IssueManager.class) {
                if ( mInstance == null ) {
                    mInstance = new IssueManager();
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

        mAPI = mRetrofit.create(IssueAPI.class);
    }

    /*---------------------------------------------------------------------------------------------*/

    public Flowable<IssueModel> getIssue(String token, int issueId) {

        Flowable<IssueModel> observable = mAPI.getIssue(token, issueId);

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<DBResultResponse> postIssue(String token,
                                                  int boardid,
                                                  String title,
                                                  String content,
                                                  String tag,
                                                  @Nullable String imgs,
                                                  int bodytype,
                                                  int platform) {

        Flowable<DBResultResponse> observable
                = mAPI.postIssue(token, boardid, title, content, tag, imgs, bodytype, platform);

        return  observable.subscribeOn(Schedulers.io());
    }
}

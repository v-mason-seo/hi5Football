package com.ddastudio.hifivefootball_android.data.manager;

import com.ddastudio.hifivefootball_android.BuildConfig;
import com.ddastudio.hifivefootball_android.data.api.FootballChatService;
import com.ddastudio.hifivefootball_android.football_chat.model.ChatModel;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class FootballChatManager {

    // 1. 하이파이브 서버 베이스 URL
    final static String HIFIVE_SERVER_BASE_URL = "http://api.hifivefootball.com:5300/";
    // 2. 아레나 서버 베이스 URL
    final static String ARENA_SERVER_BASE_URL="http://api.hifivefootball.com:5400/";

    static FootballChatManager mInstance;
    Retrofit mRetrofit;
    FootballChatService mAPI;

    private FootballChatManager() {

        initRetrofit();
    }

    public static FootballChatManager getInstance() {

        if ( mInstance == null ) {
            synchronized (FootballChatManager.class) {
                if ( mInstance == null ) {
                    mInstance = new FootballChatManager();
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
                    .baseUrl(BuildConfig.HIFIVE_SERVER_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }

        mAPI = mRetrofit.create(FootballChatService.class);
    }

    /*---------------------------------------------------------------------------------------------*/

    public Flowable<List<ChatModel>> onLoadPlayer() {

        Flowable<List<ChatModel>> observable = mAPI.onLoadFootballChatList();

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<List<ChatModel>> onLoadPlayer(int limit, int offset) {

        Flowable<List<ChatModel>> observable = mAPI.onLoadFootballChatList(limit, offset);

        return  observable.subscribeOn(Schedulers.io());
    }
}

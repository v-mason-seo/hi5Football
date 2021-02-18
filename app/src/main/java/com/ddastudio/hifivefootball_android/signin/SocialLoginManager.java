package com.ddastudio.hifivefootball_android.signin;

import android.content.Context;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hongmac on 2017. 11. 16..
 */

public class SocialLoginManager {

    Context mContext;
    Retrofit mRetrofit;
    SocialLoginAPI mAPI;
    /**
     * 네이버 API
     */
    SocialLoginManager mSocialManager;

    public SocialLoginManager(Context context) {

        this.mContext = context;
        initRetrofit();
    }

    private void initRetrofit() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …
        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://openapi.naver.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //.client(httpClient.build())
                .build();

        mAPI = mRetrofit.create(SocialLoginAPI.class);

    }

    /*---------------------------------------------------------------------------------------------*/


    public Flowable<NaverUserProfileModel> onLoadNaverUserProfile(String token) {

        Flowable<NaverUserProfileModel> observable = mAPI.getNaverUserProfile(token);

        return  observable.subscribeOn(Schedulers.io());
    }
}

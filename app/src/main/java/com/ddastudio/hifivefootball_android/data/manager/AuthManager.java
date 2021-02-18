package com.ddastudio.hifivefootball_android.data.manager;

import android.content.Context;
import android.util.Log;

import com.ddastudio.hifivefootball_android.BuildConfig;
import com.ddastudio.hifivefootball_android.data.api.AuthAPI;
import com.ddastudio.hifivefootball_android.data.model.AuthToken;
import com.ddastudio.hifivefootball_android.data.model.CheckEmailModel;
import com.ddastudio.hifivefootball_android.data.model.CheckUserNameModel;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;

import java.net.URLEncoder;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * Created by hongmac on 2017. 9. 6..
 */

public class AuthManager {

    static AuthManager mInstance;
    Retrofit mRetrofit;
    AuthAPI mAPI;

    public AuthManager() {
        initRetrofit();
    }

    public static AuthManager getInstance() {

        if ( mInstance == null ) {
            synchronized (AuthManager.class) {
                if ( mInstance == null ) {
                    mInstance = new AuthManager();
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

        mAPI = mRetrofit.create(AuthAPI.class);
    }

    /*---------------------------------------------------------------------------------------------*/


    /**
     * 로그인
     * @param provider
     * @param provider_user_id
     * @return
     */
    public Flowable<DBResultResponse> socialLogin(String provider,
                                                  String provider_user_id,
                                                  String provider_access_token) {

        try {
            provider_access_token = URLEncoder.encode(provider_access_token, "UTF-8");
        } catch (Exception ex) {

        }

        Flowable<DBResultResponse> observable = mAPI.socialLogin(provider, provider_user_id, provider_access_token);

        return  observable.subscribeOn(Schedulers.io());
    }

    /**
     * 하아피이브 Access token 갱신
     * @param refrehs_token
     * @return
     */
    public Flowable<DBResultResponse> accessTokenRefresh(String refrehs_token) {

        //Timber.d("[accessTokenRefresh] - 1 %s", refrehs_token);
//        try {
//            refrehs_token = URLEncoder.encode(refrehs_token, "UTF-8");
//        } catch (Exception ex) {
//
//        }

        //Timber.d("[accessTokenRefresh] - 2 %s", refrehs_token);

        Flowable<DBResultResponse> observable = mAPI.accessTokenRefresh(refrehs_token);

        return  observable.subscribeOn(Schedulers.io());
    }

    /**
     * 회원가입
     * @param provider
     * @param provider_user_id
     * @param username
     * @param email
     * @param avatar_url
     * @return
     */
    public Flowable<DBResultResponse> socialSignup(String provider,
                                                   String provider_user_id,
                                                   String provider_access_token,
                                                   String username,
                                                   String nickname,
                                                   String profile,
                                                   String email,
                                                   String avatar_url) {

//        Timber.i("[socialSignup]---------------------------------------------");
//        Timber.i("  - provider: "+ provider);
//        Timber.i("  - provider_user_id: "+ provider_user_id);
//        Timber.i("  - provider_access_token: "+ provider_access_token);
//        Timber.i("  - username: "+ username);
//        Timber.i("  - nickname: "+ nickname);
//        Timber.i("  - profile: "+ profile);
//        Timber.i("  - email: "+ email);
//        Timber.i("  - avatar_url: "+ avatar_url);
//        Timber.i("[socialSignup]---------------------------------------------");

        try {
            provider_access_token = URLEncoder.encode(provider_access_token, "UTF-8");
        } catch (Exception ex) {

        }

        Flowable<DBResultResponse> observable
                = mAPI.socialSignup(provider, provider_user_id, provider_access_token, username, nickname, profile, email, avatar_url, "");

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<CheckEmailModel> checkEmail(String email) {

        Flowable<CheckEmailModel> observable = mAPI.checkEmail(email);

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<CheckUserNameModel> checkUserName(String email) {

        Flowable<CheckUserNameModel> observable = mAPI.checkUserName(email);

        return  observable.subscribeOn(Schedulers.io());
    }
}

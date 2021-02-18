package com.ddastudio.hifivefootball_android.data.manager;

import com.ddastudio.hifivefootball_android.BuildConfig;
import com.ddastudio.hifivefootball_android.data.api.NuntingAPI;
import com.ddastudio.hifivefootball_android.nunting_bset.model.PostsItemModel;
import com.ddastudio.hifivefootball_android.nunting_bset.model.SiteBoardModel;

import java.util.List;

import io.reactivex.Flowable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NuntingManager {

    static NuntingManager mInstance;
    Retrofit mRetrofit;
    NuntingAPI mAPI;

    public NuntingManager() {
        initRetrofit();
    }

    public static NuntingManager getInstance() {

        if ( mInstance == null ) {
            synchronized (NuntingManager.class) {
                if ( mInstance == null ) {
                    mInstance = new NuntingManager();
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
                    //.baseUrl("http://nb.hifivesoccer.com:4000/")
                    .baseUrl(BuildConfig.NB_SERVER_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClient.build())
                    .build();

        } else {

            mRetrofit = new Retrofit.Builder()
                    //.baseUrl("http://nb.hifivesoccer.com:4000/")
                    .baseUrl(BuildConfig.NB_SERVER_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }

        mAPI = mRetrofit.create(NuntingAPI.class);
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 사이트 정보를 가져온다.
     * @return
     */
    public Flowable<List<SiteBoardModel>> onLoadSiteList() {

        Flowable<List<SiteBoardModel>> observable = mAPI.onLoadSiteList();

        return observable.subscribeOn(io.reactivex.schedulers.Schedulers.io());
    }

    public Flowable<PostsItemModel> onLoadPostsList(final String siteId, final String boardId, final String yyyyMMddHH) {

        Flowable<PostsItemModel> observable
                = mAPI.loadPostsList(siteId, boardId, yyyyMMddHH);

        return observable.subscribeOn(io.reactivex.schedulers.Schedulers.io());
    }
}

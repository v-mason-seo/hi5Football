package com.ddastudio.hifivefootball_android.data.manager;

import com.ddastudio.hifivefootball_android.BuildConfig;
import com.ddastudio.hifivefootball_android.data.api.ReportAPI;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.Header;

public class ReportRepository {

    static ReportRepository mInstance;
    Retrofit mRetrofit;
    ReportAPI mAPI;

    private ReportRepository() {

        initRetrofit();
    }

    public static ReportRepository getInstance() {

        if ( mInstance == null ) {
            synchronized (ReportRepository.class) {
                if ( mInstance == null ) {
                    mInstance = new ReportRepository();
                }
            }
        }

        return mInstance;
    }

    private void initRetrofit() {

        if ( BuildConfig.DEBUG ) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);  // <-- this is the important line!

            mRetrofit = new Retrofit.Builder()
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

        mAPI = mRetrofit.create(ReportAPI.class);
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 게시글 신고하기
     * @param token
     * @param userid
     * @param boardid
     * @param title
     * @param content
     * @param platform
     * @return
     */
    public Flowable<DBResultResponse> postReport(String token,
                                                 int boardid,
                                                 String title,
                                                 String content,
                                                 int platform) {

        Flowable<DBResultResponse> observable
                = mAPI.postReport(token, boardid, title, content, platform);

        return  observable.subscribeOn(Schedulers.io());
    }
}

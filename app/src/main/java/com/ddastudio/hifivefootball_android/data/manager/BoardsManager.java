package com.ddastudio.hifivefootball_android.data.manager;

import com.ddastudio.hifivefootball_android.BuildConfig;
import com.ddastudio.hifivefootball_android.data.api.BoardsAPI;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.IssueModel;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hongmac on 2017. 9. 4..
 */

public class BoardsManager {

    static BoardsManager mInstance;
    Retrofit mRetrofit;
    BoardsAPI mAPI;

    public BoardsManager() {

        initRetrofit();
    }

    public static BoardsManager getInstance() {

        if ( mInstance == null ) {
            synchronized (BoardsManager.class) {
                if ( mInstance == null ) {
                    mInstance = new BoardsManager();
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

        mAPI = mRetrofit.create(BoardsAPI.class);
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 게시글 조회
     * @param id 게시판 종류
     * @param limit
     * @param offset
     * @return
     */
    public Flowable<List<ContentHeaderModel>> getBoardContentList(String token,
                                                                  int id,
                                                                  int limit,
                                                                  int offset) {

        Flowable<List<ContentHeaderModel>> observable
                = mAPI.getBoardContentList(token, id, limit, offset);

        return  observable.subscribeOn(Schedulers.io());
    }


    /**
     * 전체 게시글 조회
     * @param token
     * @param excludeMatchReport 매치리포트 제외 여부
     * @param excludeFootballNews 축구소식 제외 여부
     * @param limit
     * @param offset
     * @return
     */
    public Flowable<List<ContentHeaderModel>> getBoardALLContentList(String token,
                                                                     int excludeMatchReport,
                                                                     int excludeFootballNews,
                                                                     int limit,
                                                                     int offset) {

        Flowable<List<ContentHeaderModel>> observable
                = mAPI.getBoardALLContentList(token, excludeMatchReport, excludeFootballNews, limit, offset);

        return  observable.subscribeOn(Schedulers.io());
    }


    /**
     * 이슈 조회
     * @param token
     * @param boardid
     * @param limit
     * @param offset
     * @param colsed
     * @return
     */
    public Flowable<List<IssueModel>> getIssueBoardContentList(String token,
                                                                       int boardid,
                                                                       int limit,
                                                                       int offset,
                                                                       int colsed) {

        Flowable<List<IssueModel>> observable
                = mAPI.getIssueBoardContentList(token, boardid, limit, offset, colsed);

        return  observable.subscribeOn(Schedulers.io());
    }
}

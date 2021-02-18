package com.ddastudio.hifivefootball_android.data.manager;

import com.ddastudio.hifivefootball_android.BuildConfig;
import com.ddastudio.hifivefootball_android.data.api.PlayersAPI;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingInfoModel;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hongmac on 2018. 1. 15..
 */

public class PlayersManager {

    static PlayersManager mInstance;
    Retrofit mRetrofit;
    PlayersAPI mAPI;

    private PlayersManager() {
        initRetrofit();
    }

    public static PlayersManager getInstance() {

        if ( mInstance == null ) {
            synchronized (PlayersManager.class) {
                if ( mInstance == null ) {
                    mInstance = new PlayersManager();
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
                    //.baseUrl("http://api.hifivesoccer.com:5400")
                    .baseUrl(BuildConfig.ARENA_SERVER_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClient.build())
                    .build();

        } else {

            mRetrofit = new Retrofit.Builder()
                    //.baseUrl("http://api.hifivesoccer.com:5400")
                    .baseUrl(BuildConfig.ARENA_SERVER_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }

        mAPI = mRetrofit.create(PlayersAPI.class);
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 선수 한줄평 전체 항목을 가져온다.
     * @param player_id
     * @param limit
     * @param offset
     * @return
     */
//    public Flowable<List<PlayerCommentModel>> onLoadPlayerComment(int player_id, int limit, int offset) {
//
//        Flowable<List<PlayerCommentModel>> observable = mAPI.onLoadOverallPlayerComments(player_id, limit, offset);
//
//        return  observable.subscribeOn(Schedulers.io());
//    }

    public Flowable<PlayerModel> onLoadPlayer(int playerId) {

        Flowable<PlayerModel> observable = mAPI.onLoadPlayer(playerId);

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<List<PlayerModel>> onLoadPlayerList(int limit, int offset) {

        Flowable<List<PlayerModel>> observable = mAPI.onLoadPlayerList(limit, offset);

        return  observable.subscribeOn(Schedulers.io());
    }

    /**
     * 선수이름 변경 요청
     * @param token
     * @param playerId
     * @param beforeName
     * @param afterName
     * @return
     */
    public Flowable<DBResultResponse> requestChangePlayerName(String token,
                                                              int playerId,
                                                              String beforeName,
                                                              String afterName) {

        Flowable<DBResultResponse> observable
                = mAPI.requestChangePlayerName(token, playerId, beforeName, afterName);

        return  observable.subscribeOn(Schedulers.io());
    }

    /**
     * 선수 코멘트 데이터를 가져온다.
     * @param player_id
     * @param match_id
     * @return
     */
    public Flowable<List<PlayerRatingInfoModel>> onLoadPlayerComments(Integer player_id, Integer match_id) {

        Flowable<List<PlayerRatingInfoModel>> observable
                = mAPI.getPlayerComments(player_id, match_id);

        return  observable.subscribeOn(Schedulers.io());
    }

    /**
     * 선수관련글을 가져온다.
     * @param playerId
     * @return
     */
    public Flowable<List<ContentHeaderModel>> onLoadPlayerContentList(String token,
                                                                      Integer playerId,
                                                                      Integer boardId,
                                                                      Integer limit,
                                                                      Integer offset) {

        Flowable<List<ContentHeaderModel>> observable
                = mAPI.onLoadPlayerContentList(token, playerId, boardId, limit, offset);

        return  observable.subscribeOn(Schedulers.io());
    }
}

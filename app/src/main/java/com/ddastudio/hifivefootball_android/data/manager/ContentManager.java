package com.ddastudio.hifivefootball_android.data.manager;

import com.ddastudio.hifivefootball_android.BuildConfig;
import com.ddastudio.hifivefootball_android.data.api.ContentAPI;
import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingInfoModel;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.Nullable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hongmac on 2017. 9. 8..
 */

public class ContentManager {

    static ContentManager mInstance;
    Retrofit mRetrofit;
    ContentAPI mAPI;

    public ContentManager() {

        initRetrofit();
    }

    public static ContentManager getInstance() {

        if ( mInstance == null ) {
            synchronized (ContentManager.class) {
                if ( mInstance == null ) {
                    mInstance = new ContentManager();
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

        mAPI = mRetrofit.create(ContentAPI.class);
    }

    /*---------------------------------------------------------------------------------------------*/

    // *** Contents ***

    public Flowable<ContentHeaderModel> getContent(String token, int contentId) {

        Flowable<ContentHeaderModel> observable
                = mAPI.getContent(token, contentId);

        return  observable.subscribeOn(Schedulers.io());
    }



    /**
     * 관련 글 가져오기
     * @param token
     * @param contentId
     * @return
     */
    public Flowable<List<ContentHeaderModel>> getRelationContent(String token, int contentId) {

        Flowable<List<ContentHeaderModel>> observable
                = mAPI.getRelationContent(token, contentId);

        return  observable.subscribeOn(Schedulers.io());
    }

    /**
     *
     * @param token
     * @param contentId
     * @return
     */
    public Flowable<List<PlayerRatingInfoModel>> getRelationPlayerRatings(String token, int contentId) {

        Flowable<List<PlayerRatingInfoModel>> observable
                = mAPI.getRelationPlayerRatings(token, contentId);

        return  observable.subscribeOn(Schedulers.io());
    }

    /**
     * 관련 매치 정보 가져오기
     * contents.tags 에 M+매치번호 태그정보를 읽어 매치정보를 가져온다.
     * @param contentId
     * @return
     */
    public Flowable<List<MatchModel>> getRelationMatches(int contentId) {

        Flowable<List<MatchModel>> observable
                = mAPI.getRelationMatches(contentId);

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<DBResultResponse> postContent(String token,
                                                  int boardid,
                                                  String title,
                                                  String preview,
                                                  String content,
                                                  Integer arenaid,
                                                  Integer playerid,
                                                  Integer teamid,
                                                  String tag,
                                                  @Nullable String imgs,
                                                  int bodyType,
                                                  int cellType,
                                                  int allowComment) {

        Flowable<DBResultResponse> observable
                = mAPI.postContent(token, boardid, title, preview, content, arenaid, playerid, teamid, tag, imgs, bodyType, cellType, allowComment);

        return  observable.subscribeOn(Schedulers.io());
    }

    /**
     * 게시글 수정
     * @param token
     * @param contentid
     * @param boardid
     * @param title
     * @param preview
     * @param content
     * @param arenaid
     * @param tag
     * @param imgs
     * @param bodytype
     * @param cellType
     * @param allowComment
     * @return
     */
    public Flowable<DBResultResponse> updateContent(String token,
                                                    int contentid,
                                                    int boardid,
                                                    String title,
                                                    String preview,
                                                    String content,
                                                    Integer arenaid,
                                                    Integer playerid,
                                                    Integer teamid,
                                                    String tag,
                                                    @Nullable String imgs,
                                                    int bodytype,
                                                    int cellType,
                                                    int allowComment ) {

        Flowable<DBResultResponse> observable
                = mAPI.updateContent(token, contentid, boardid, title, preview, content, arenaid, playerid, teamid, tag, imgs, bodytype, cellType, allowComment);

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<DBResultResponse> deleteContent(String token, int contentId) {

        Flowable<DBResultResponse> observable
                = mAPI.deleteContent(token, contentId);

        return  observable.subscribeOn(Schedulers.io());
    }


    // *** Like ***


    public Flowable<List<UserModel>> getLikers(int id) {

        Flowable<List<UserModel>> observable = mAPI.getLikers(id);

        return  observable.subscribeOn(Schedulers.io());
    }


    public Flowable<DBResultResponse> setLike(String token, int contentId, int hifive_count) {

        Flowable<DBResultResponse> observable
                = mAPI.setLike(token, contentId, hifive_count);

        return  observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<DBResultResponse> setUnlike(String token, int contentId) {

        Flowable<DBResultResponse> observable
                = mAPI.setUnlike(token, contentId);

        return  observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    // *** Scrap ***

    public Flowable<List<UserModel>> getScrpUsers(int id) {

        Flowable<List<UserModel>> observable = mAPI.getScrpUsers(id);

        return  observable.subscribeOn(Schedulers.io());
    }

    /**
     * 스크랩
     * @param token
     * @param contentId
     * @return
     */
    public Flowable<DBResultResponse> postScrap(String token, int contentId) {

        Flowable<DBResultResponse> observable = mAPI.postScrap(token, contentId);

        return  observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 스크랩 취소
     * @param token
     * @param contentId
     * @return
     */
    public Flowable<DBResultResponse> deleteScrap(String token, int contentId) {

        Flowable<DBResultResponse> observable = mAPI.deleteScrap(token, contentId);

        return  observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    // *** Reported ***

    public Flowable<DBResultResponse> setReported(String token, int contentId) {

        Flowable<DBResultResponse> observable = mAPI.setReported(token, contentId);

        return  observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}

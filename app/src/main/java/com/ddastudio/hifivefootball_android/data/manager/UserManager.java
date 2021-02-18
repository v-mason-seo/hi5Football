package com.ddastudio.hifivefootball_android.data.manager;

import com.ddastudio.hifivefootball_android.BuildConfig;
import com.ddastudio.hifivefootball_android.data.api.UserAPI;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.community.UserCommentModel;
import com.ddastudio.hifivefootball_android.data.model.UserModel;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hongmac on 2017. 9. 20..
 */

public class UserManager {

    static UserManager mInstance;
    Retrofit mRetrofit;
    UserAPI mAPI;

    public UserManager() {
        initRetrofit();
    }

    public static UserManager getInstance() {

        if ( mInstance == null ) {
            synchronized (UserManager.class) {
                if ( mInstance == null ) {
                    mInstance = new UserManager();
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

        mAPI = mRetrofit.create(UserAPI.class);

        //http://45.32.254.69:5300/v1/users/서홍원/coments?limit=15&offset=0
        //http://45.32.254.69:5300/v1/users/날두/comments?limit=15&offset=0
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 유저(본인) 정보 조회
     * @param token
     * @return
     */
    public Flowable<UserModel> getLoginUser(String token) {

        Flowable<UserModel> observable = mAPI.getLoginUser(token);

        return  observable.subscribeOn(Schedulers.io());
    }

    /**
     * 유저(다른사람) 정보 조회
     * @param username
     * @return
     */
    public Flowable<UserModel> onLoadUserInfo(String username) {

        Flowable<UserModel> observable = mAPI.onLoadUserInfo(username);

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<DBResultResponse> updateAvatar(String token, String avatarUrl) {

        Flowable<DBResultResponse> observable = mAPI.updateAvatar(token, avatarUrl);
                //= mAPI.updateAvatar("bearer " + token, avatarUrl);

        return  observable.subscribeOn(Schedulers.io());
    }


    /**
     * 유저정보 입력
     * @param user
     * @return
     */
    /*public Flowable<DBResultResponse> onInsertUserInfo(UserModel user) {

        Flowable<DBResultResponse> observable
                = mAPI.onInsertUserInfo(user.getLogin_path()
                , user.getEmail()
                , user.getUser_name()
                , user.getProfile()
                , user.getAvatar()
                , user.getFavorite_team()
                , user.getFavorite_player()
                , user.getFavorite_site()
                , user.getFavorite_national());

        return  observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }*/


    /**
     * 유저정보 수정
     * @param token
     * @param nickname
     * @param profile
     * @param avatarUrl
     * @return
     */
    public Flowable<DBResultResponse> onUpdateUserInfo(String token,
                                                       String nickname,
                                                       String profile,
                                                       String avatarUrl) {

        Flowable<DBResultResponse> observable
                = mAPI.onUpdateUserInfo(token, nickname, profile, avatarUrl);

        return  observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /*---------------------------------------------------------------------------------------------*/


    /**
     * 내가쓴글
     * @param username
     * @param limit
     * @param offset
     * @return
     */
    public Flowable<List<ContentHeaderModel>> getUserContentList(String username,
                                                                     int limit,
                                                                     int offset) {

        Flowable<List<ContentHeaderModel>> observable
                = mAPI.getUserContentList(username, limit, offset);

        return  observable.subscribeOn(Schedulers.io());
    }

    /**
     * 내가쓴댓글
     * @param username
     * @param limit
     * @param offset
     * @return
     */
    public Flowable<List<UserCommentModel>> getUserCommentList(String username,
                                                               int limit,
                                                               int offset) {

        Flowable<List<UserCommentModel>> observable = mAPI.getUserCommentList(username, limit, offset);

        return  observable.subscribeOn(Schedulers.io());
    }

    /**
     * 내가누른 좋아요
     * @param username
     * @param limit
     * @param offset
     * @return
     */
    public Flowable<List<ContentHeaderModel>> getUserLikedList(String username,
                                                               int limit,
                                                               int offset) {

        Flowable<List<ContentHeaderModel>> observable
                = mAPI.getUserLikedList(username, limit, offset);

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<List<ContentHeaderModel>> onLoadScrapList(String token,
                                                              String username,
                                                              int limit,
                                                              int offset) {

        Flowable<List<ContentHeaderModel>> observable
                = mAPI.onLoadScrapList(token, username, limit, offset);

        return  observable.subscribeOn(Schedulers.io());
    }
}

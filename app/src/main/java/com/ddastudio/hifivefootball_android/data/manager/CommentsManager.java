package com.ddastudio.hifivefootball_android.data.manager;

import android.content.Context;

import com.ddastudio.hifivefootball_android.BuildConfig;
import com.ddastudio.hifivefootball_android.data.api.CommentsAPI;
import com.ddastudio.hifivefootball_android.data.model.community.CommentModel;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.UserModel;

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
 * Created by hongmac on 2017. 9. 6..
 */

public class CommentsManager {

    static CommentsManager mInstance;
    Retrofit mRetrofit;
    CommentsAPI mAPI;

    public CommentsManager() {

        initRetrofit();
    }

    public static CommentsManager getInstance() {

        if ( mInstance == null ) {
            synchronized (CommentsManager.class) {
                if ( mInstance == null ) {
                    mInstance = new CommentsManager();
                }
            }
        }

        return mInstance;
    }

    private void initRetrofit() {

        if ( BuildConfig.DEBUG ) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
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

        mAPI = mRetrofit.create(CommentsAPI.class);
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 댓글 정보를 가져온다.
     * @param contentid
     * @return
     */
    public Flowable<List<CommentModel>> getComments(String token,int contentid) {

        Flowable<List<CommentModel>> observable = mAPI.getComments(token, contentid);

        return  observable.subscribeOn(Schedulers.io());
    }

    /**
     * 그룹에 속한 댓글 정보를 가져온다.
     * @param token
     * @param commentid
     * @return
     */
    public Flowable<List<CommentModel>> getGroupComments(String token, int commentid) {

        Flowable<List<CommentModel>> observable = mAPI.getGroupComments(token, commentid);

        return  observable.subscribeOn(Schedulers.io());
    }


    public Flowable<CommentModel> getComment(int commentid,
                                             @Nullable int userid) {

        Flowable<CommentModel> observable = mAPI.getComment(commentid, userid);

        return  observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 댓글 입력
     * @param token
     * @param contentId
     * @param parentId
     * @param groupId
     * @param depth
     * @param content
     * @return
     */
    public Flowable<DBResultResponse> createComment(String token,
                                                    int contentId,
                                                    int parentId,
                                                    int groupId,
                                                    int depth,
                                                    String content) {

        Flowable<DBResultResponse> observable
                = mAPI.createComment(token, contentId, parentId, groupId, depth, content);

        return  observable.subscribeOn(Schedulers.io());
    }


    /**
     * 댓글 삭제
     * @param id
     * @param userid
     * @return
     */
    public Flowable<DBResultResponse> deleteComment(String token,
                                                    int commentId,
                                                    int contentid) {

        Flowable<DBResultResponse> observable = mAPI.deleteComment(token, commentId, contentid);

        return  observable.subscribeOn(Schedulers.io());
    }


    /**
     * 댓글 수정
     * @param id
     * @param content
     * @return
     */
    public Flowable<DBResultResponse> updateComment(String token,
                                                    Integer id,
                                                    String content) {

        Flowable<DBResultResponse> observable = mAPI.updateComment(token, id, content);

        return  observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    // *** Like ***


    /**
     * 좋아요
     * @param id
     * @param userid
     * @return
     */
    public Flowable<DBResultResponse> setLike(String token,
                                              int commentId) {

        Flowable<DBResultResponse> observable = mAPI.setLike(token, commentId);

        return  observable.subscribeOn(Schedulers.io());
    }

    /**
     * 좋아요 취소
     * @param id
     * @param user_id
     * @return
     */
    public Flowable<DBResultResponse> setUnlike(String token,
                                                int commentId) {

        Flowable<DBResultResponse> observable = mAPI.setUnlike(token, commentId);

        return  observable.subscribeOn(Schedulers.io());
    }


    /**
     * 좋아요버튼 누른 유저 리스트
     * @param content_id
     * @return
     */
    public Flowable<List<UserModel>> getLikers(int content_id) {

        Flowable<List<UserModel>> observable = mAPI.getLikers(content_id);

        return  observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    // *** Reported ***


    /**
     * 신고하기
     * @param id
     * @return
     */
    public Flowable<DBResultResponse> setReported(String token, int id) {

        Flowable<DBResultResponse> observable = mAPI.setReported(token, id);

        return  observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}

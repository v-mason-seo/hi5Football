package com.ddastudio.hifivefootball_android.data.manager;

import com.ddastudio.hifivefootball_android.BuildConfig;
import com.ddastudio.hifivefootball_android.data.api.MatchesAPI;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingInfoModel;
import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingParticipationUser;
import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingRecordModel;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchOverviewModel;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.MatchEventModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.data.model.overview.MatchPredictionsModel;
import com.ddastudio.hifivefootball_android.data.model.overview.MatchRecentFormModel;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hongmac on 2017. 12. 26..
 */

public class MatchesManager {

    static MatchesManager mInstance;
    Retrofit mRetrofit;
    MatchesAPI mAPI;

    MatchesManager() {
        initRetrofit();
    }

    public static MatchesManager getInstance() {

        if ( mInstance == null ) {
            synchronized (MatchesManager.class) {
                if ( mInstance == null ) {
                    mInstance = new MatchesManager();
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

        mAPI = mRetrofit.create(MatchesAPI.class);

    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     *
     * @param matchId
     * @return
     */
    public Flowable<MatchModel> onLoadMatche(int matchId) {

        Flowable<MatchModel> observable = mAPI.onLoadMatche(matchId);

        return  observable.subscribeOn(Schedulers.io());
    }


    /**
     * 매치정보 가져오기
     * @param fromDate
     * @param toDate
     * @param offset
     * @param limit
     * @return
     */
    public Flowable<List<MatchModel>> onLoadMatches(String fromDate,
                                                    String toDate,
                                                    int offset,
                                                    int limit) {

        Flowable<List<MatchModel>> observable
                = mAPI.onLoadMatches(null, fromDate, toDate, offset, limit);

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<List<MatchModel>> onLoadMatches(String compList,
                                                    String fromDate,
                                                    String toDate,
                                                    int offset,
                                                    int limit) {

        Flowable<List<MatchModel>> observable
                = mAPI.onLoadMatches(null, compList, fromDate, toDate, offset, limit);

        return  observable.subscribeOn(Schedulers.io());
    }

    /**
     *
     * @param compid
     * @param fromDate
     * @param toDate
     * @param offset
     * @param limit
     * @return
     */
    public Flowable<List<MatchModel>> onLoadMatches(int compid,
                                                    String fromDate,
                                                    String toDate,
                                                    int offset,
                                                    int limit) {

        Flowable<List<MatchModel>> observable
                = mAPI.onLoadMatches(compid, fromDate, toDate, offset, limit);

        return  observable.subscribeOn(Schedulers.io());
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 라인업 정보를 가져온다.
     * @param match_id
     * @return
     */
    public Flowable<List<PlayerModel>> onLoadLineup(int match_id) {

        Flowable<List<PlayerModel>> observable = mAPI.onLoadLineup(match_id);

        return  observable.subscribeOn(Schedulers.io());
    }

    /*---------------------------------------------------------------------------------------------*/

    public Flowable<List<MatchEventModel>> onLoadMatchEvents(int match_id) {

        Flowable<List<MatchEventModel>> observable = mAPI.onLoadMatchEvents(match_id);

        return  observable.subscribeOn(Schedulers.io());
    }

    /*---------------------------------------------------------------------------------------------*/

    public Flowable<MatchOverviewModel> onLoadMatchOverview(int match_id) {

        Flowable<MatchOverviewModel> observable = mAPI.onLoadMatchOverview( match_id);

        return  observable.subscribeOn(Schedulers.io());
    }

    /**
     * 승무패 정보를 가져온다.
     * @param token
     * @param match_id
     * @return
     */
    public Flowable<MatchPredictionsModel> onLoadMatchPredictions(String token, int match_id) {

        Flowable<MatchPredictionsModel> observable = mAPI.onLoadMatchPredictions(token, match_id);

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<DBResultResponse> postMatchPredictions(String token, int match_id, String predictions) {

        Flowable<DBResultResponse> observable
                = mAPI.postMatchPredictions(token, match_id, predictions);

        return  observable.subscribeOn(Schedulers.io());
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 최근 5경기 결과를 가져온다.
     * @param match_id
     * @return
     */
    public Flowable<MatchRecentFormModel> onLoadMatchRecentForm(int match_id) {

        Flowable<MatchRecentFormModel> observable = mAPI.onLoadMatchRecentForm(match_id);

        return  observable.subscribeOn(Schedulers.io());
    }

    /*---------------------------------------------------------------------------------------------*/


    public Flowable<List<PlayerRatingRecordModel>> getPlayerRatingRecord(int match_id, int player_id, String time_seq) {

        Flowable<List<PlayerRatingRecordModel>> observable = mAPI.getPlayerRatingRecord(match_id, player_id, time_seq);

        return  observable.subscribeOn(Schedulers.io());
    }


    public Flowable<List<PlayerRatingRecordModel>> getPlayerRatingRecord(int match_id, String time_seq) {

        Flowable<List<PlayerRatingRecordModel>> observable = mAPI.getPlayerRatingRecord(match_id, time_seq);

        return  observable.subscribeOn(Schedulers.io());
    }

    /**
     * 선수 평점에 참여한 사용자 정보를 가져온다.
     * @param match_id
     * @param player_fa_id
     * @param updown
     * @return
     */
    public Flowable<List<PlayerRatingParticipationUser>> getPlayerRatingParticipationUserList(int match_id, int player_fa_id, int updown) {

        Flowable<List<PlayerRatingParticipationUser>> observable
                = mAPI.getPlayerRatingParticipationUserList(match_id, player_fa_id, updown);

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<DBResultResponse> setPlayerRating(String token,
                                                      int match_id,
                                                      int player_id,
                                                      String time_seq,
                                                      double rating,
                                                      int updown) {

        Flowable<DBResultResponse> observable
                = mAPI.setPlayerRating(token, match_id, player_id, time_seq, rating, updown);

        return  observable.subscribeOn(Schedulers.io());
    }



    /*---------------------------------------------------------------------------------------------*/

    public Flowable<DBResultResponse> setPlayerRating3(PlayerRatingInfoModel abc) {

        Flowable<DBResultResponse> observable
                = mAPI.setPlayerRating3(abc);

        return  observable.subscribeOn(Schedulers.io());
    }

    /*---------------------------------------------------------------------------------------------*/




    public Flowable<DBResultResponse> setPlayerComment(String token,
                                                       int match_id,
                                                       int player_id,
                                                       int ingame,
                                                       String comment) {

        Flowable<DBResultResponse> observable
                = mAPI.setPlayerComment(token, match_id, player_id, ingame, comment);

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<DBResultResponse> setPlayerCommentHifive(String token,
                                                             int match_id,
                                                             int player_id,
                                                             int writer_id,
                                                             String time_seq,
                                                             int hifiveCount) {

        Flowable<DBResultResponse> observable
                = mAPI.setPlayerCommentHifive(token, match_id, player_id, writer_id, time_seq, hifiveCount);

        return  observable.subscribeOn(Schedulers.io());
    }

    /*---------------------------------------------------------------------------------------------*/

    public Flowable<List<PlayerRatingInfoModel>> onLoadPlayerComments(Integer match_id) {

        Flowable<List<PlayerRatingInfoModel>> observable
                = mAPI.getPlayerComments(match_id);

        return  observable.subscribeOn(Schedulers.io());
    }

    /*---------------------------------------------------------------------------------------------*/

    public Flowable<List<ContentHeaderModel>> onLoadMatchContentList(Integer matchId,
                                                                        Integer offset,
                                                                        Integer limit) {

        Flowable<List<ContentHeaderModel>> observable
                = mAPI.onLoadMatchContentList(matchId, offset, limit);

        return  observable.subscribeOn(Schedulers.io());
    }

    /*---------------------------------------------------------------------------------------------*/

    public Flowable<List<UserModel>> onLoadParticipationUsers(Integer match_id) {

        Flowable<List<UserModel>> observable
                = mAPI.onLoadParticipationUsers(match_id);

        return  observable.subscribeOn(Schedulers.io());
    }

    public Flowable<DBResultResponse> onLoadParticipationUserCount(Integer match_id) {

        Flowable<DBResultResponse> observable
                = mAPI.onLoadParticipationUserCount(match_id);

        return  observable.subscribeOn(Schedulers.io());
    }
}

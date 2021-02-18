package com.ddastudio.hifivefootball_android.data.api;

import com.ddastudio.hifivefootball_android.content_list.model.BestContentsHeaderModel;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by hongmac on 2017. 9. 5..
 */

public interface BestAPI {

    @GET("v1/best")
    Flowable<List<BestContentsHeaderModel>> getALLBestContentList(@Query("bestid") String best_id,
                                                                  @Query("roll") String best_roll,
                                                                  @Query("limit") int limit,
                                                                  @Query("offset") int offset);

    @GET("v1/best/{boardid}")
    Flowable<List<BestContentsHeaderModel>> getBestContentList(@Path("boardid") int boardid,
                                                               @Query("bestid") String best_id,
                                                               @Query("roll") String best_roll,
                                                               @Query("limit") int limit,
                                                               @Query("offset") int offset);

    //-------------------------------------------------------------------------------------------

    @GET("v1/best")
    Flowable<List<ContentHeaderModel>> getALLBestContentList2(@Header("authorization") String token,
                                                                   @Query("roll") String best_roll);

    @GET("v1/best/{boardid}")
    Flowable<List<ContentHeaderModel>> getBestContentList2(@Header("authorization") String token,
                                                           @Path("boardid") int boardid,
                                                           @Query("roll") String best_roll);
}

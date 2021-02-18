package com.ddastudio.hifivefootball_android.data.api;

import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;

import io.reactivex.Flowable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ReportAPI {

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("/v1/report")
    Flowable<DBResultResponse> postReport(@Header("authorization") String token,
                                             @Field("boardid") int boardid,
                                             @Field("title") String title,
                                             @Field("content") String content,
                                             @Field("platform") int platform);
}

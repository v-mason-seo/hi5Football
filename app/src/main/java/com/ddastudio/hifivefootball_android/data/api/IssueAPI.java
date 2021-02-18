package com.ddastudio.hifivefootball_android.data.api;

import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.IssueModel;

import io.reactivex.Flowable;
import io.reactivex.annotations.Nullable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by hongmac on 2017. 9. 26..
 */

public interface IssueAPI {

    @GET("v1/issue/{id}")
    Flowable<IssueModel> getIssue(@Header("authorization") String token,
                                  @Path("id") int issueId);

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("v1/issue")
    Flowable<DBResultResponse> postIssue(@Header("authorization") String token,
                                         @Field("boardid") int boardid,
                                         @Field("title") String title,
                                         @Field("content") String content,
                                         @Field("tags") String tag,
                                         @Field("imgs") @Nullable String imgs,
                                         @Field("bodytype") int bodytype,
                                         @Field("platform") int platform);
}

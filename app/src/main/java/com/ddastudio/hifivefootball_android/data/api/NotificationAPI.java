package com.ddastudio.hifivefootball_android.data.api;

import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.community.NotificationModel;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by hongmac on 2017. 10. 18..
 */

public interface NotificationAPI {

    @GET("v1/notification")
    Flowable<List<NotificationModel>> getNotification(@Header("authorization") String token);

    @GET("v1/notification")
    Flowable<List<NotificationModel>> getNotification(@Header("authorization") String token,
                                                      @Query("confirm") int confirm);

    //@FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @PUT("v1/notification/{id}/confirm")
    Flowable<DBResultResponse> confirmNotification(@Header("authorization") String token,
                                                   @Path("id") int notificationId);

    /**
     * 내소식 읽음 처리
     * @param token
     * @param notificationTypeId
     * @param contentId
     * @return
     */
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @PUT("v1/notification/confirm")
    Flowable<DBResultResponse> confirmNotification2(@Header("authorization") String token,
                                                    @Query("notitypeid") int notificationTypeId,
                                                    @Query("contentid") int contentId);

    //@FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @PUT("v1/notification/{id}/allconfirm")
    Flowable<DBResultResponse> confirmAllNotification(@Header("authorization") String token,
                                                      @Path("id") int notificationId);
}

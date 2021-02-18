package com.ddastudio.hifivefootball_android.data.api;

import com.ddastudio.hifivefootball_android.board.model.BoardMasterModel;
import com.ddastudio.hifivefootball_android.data.model.NotificationTypeMasterModel;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by hongmac on 2017. 11. 30..
 */

public interface SettingsAPI {

    @GET("v1/settings/boards")
    Flowable<List<BoardMasterModel>> getBoardList(@Query("useyn") int useyn);

    @GET("v1/settings/notification_type")
    Flowable<List<NotificationTypeMasterModel>> getNotificationType();
}

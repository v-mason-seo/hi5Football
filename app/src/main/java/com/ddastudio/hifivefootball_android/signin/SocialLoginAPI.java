package com.ddastudio.hifivefootball_android.signin;

import com.ddastudio.hifivefootball_android.data.model.arena.PlayerRatingInfoModel;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Created by hongmac on 2017. 11. 16..
 */

public interface SocialLoginAPI {

    @GET("v1/nid/me")
    Flowable<NaverUserProfileModel> getNaverUserProfile(@Header("authorization") String token);
}

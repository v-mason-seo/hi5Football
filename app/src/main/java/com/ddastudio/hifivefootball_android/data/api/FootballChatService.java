package com.ddastudio.hifivefootball_android.data.api;

import com.ddastudio.hifivefootball_android.football_chat.model.ChatModel;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FootballChatService {

    @GET("v1/footballchats/favorite")
    Flowable<List<ChatModel>> onLoadFavoriteFootballChatList();

    @GET("v1/footballchats/list")
    Flowable<List<ChatModel>> onLoadFootballChatList();

    @GET("v1/footballchats/list")
    Flowable<List<ChatModel>> onLoadFootballChatList(@Query("limit") int limit,
                                                     @Query("offset") int offset);

}

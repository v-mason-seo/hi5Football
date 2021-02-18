package com.ddastudio.hifivefootball_android.football_chat.api;

import android.arch.lifecycle.LiveData;

import com.ddastudio.hifivefootball_android.football_chat.model.ChatModel;
import com.ddastudio.hifivefootball_android.football_chat.repo.ApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ChatService {

    /**
     * FootballChats 즐겨찾기 목록을 가져온다.
     * @return
     */
    @GET("v1/footballchats/favorite")
    Call<List<ChatModel>> onLoadFavoriteFootballChatList();


    /**
     * FootballChat 리스트를 가져온다.
     * @return
     */
    @GET("v1/footballchats/list")
    Call<List<ChatModel>> onLoadFootballChatList();


    /**
     * FootballChat 리스트를 가져온다.
     *  - limit, offset
     * @param limit
     * @param offset
     * @return
     */
    @GET("v1/footballchats/list")
    Call<List<ChatModel>> onLoadFootballChatList(@Query("limit") int limit,
                                                 @Query("offset") int offset);


    /**
     * FootballChat 리스트를 가져온다.
     *  - limit, offset
     *  - LiveData를 반환한다.
     * @param limit
     * @param offset
     * @return
     */
    @GET("v1/footballchats/list")
    LiveData<ApiResponse<List<ChatModel>>> onLoadFootballChatListLiveData(@Query("limit") int limit,
                                                                          @Query("offset") int offset);

}

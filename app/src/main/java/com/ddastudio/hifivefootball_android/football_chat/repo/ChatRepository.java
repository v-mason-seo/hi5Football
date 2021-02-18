package com.ddastudio.hifivefootball_android.football_chat.repo;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ddastudio.hifivefootball_android.football_chat.api.ChatService;
import com.ddastudio.hifivefootball_android.football_chat.model.ChatAndAttributeModel;
import com.ddastudio.hifivefootball_android.football_chat.dao.ChatDao;
import com.ddastudio.hifivefootball_android.football_chat.model.ChatModel;
import com.ddastudio.hifivefootball_android.football_chat.utils.NetworkBoundResource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRepository {

    private final ChatDao dao;
    private final ChatService api;

    public ChatRepository(ChatDao dao, ChatService api) {
        this.dao = dao;
        this.api = api;
    }

    /**
     * 데이터 로드
     * @param offset
     * @param limit
     * @return
     */
    public LiveData<Resource<List<ChatAndAttributeModel>>> onLoadFootballChatList(final int offset, final int limit) {

        LiveData<Resource<List<ChatAndAttributeModel>>> liveData
                = new NetworkBoundResource<List<ChatAndAttributeModel>, List<ChatModel>>() {
            @Override
            protected void saveCallResult(@NonNull List<ChatModel> items) {
                //Log.i("hong", "[GitHubListRepository] saveCallResult");
                dao.saveChats(items);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<ChatAndAttributeModel> data) {
                //Log.i("hong", "[GitHubListRepository] shouldFetch");
                return true;//let's always refresh to be up to date. data == null || data.isEmpty();
            }

            @NonNull
            @Override
            protected LiveData<List<ChatAndAttributeModel>> loadFromDb() {
                //Log.i("hong", "[GitHubListRepository] loadFromDb");
                return dao.loadChatAndAttrList(limit, offset);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<ChatModel>>> createCall() {
                //Log.i("hong", "[GitHubListRepository] createCall");
                LiveData<ApiResponse<List<ChatModel>>> response = api.onLoadFootballChatListLiveData(limit, offset);
                return response;
            }
        }.getAsLiveData();

        return liveData;
    }

}

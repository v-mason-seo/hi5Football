package com.ddastudio.hifivefootball_android.football_chat;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.ddastudio.hifivefootball_android.football_chat.model.ChatAndAttributeModel;
import com.ddastudio.hifivefootball_android.football_chat.model.ChatModel;
import com.ddastudio.hifivefootball_android.football_chat.repo.ChatRepository;
import com.ddastudio.hifivefootball_android.football_chat.repo.ChatResponse;
import com.ddastudio.hifivefootball_android.football_chat.repo.Resource;

import java.util.ArrayList;
import java.util.List;

public class FootballChatViewModel extends ViewModel {

    ChatRepository mRepository;
    final private MutableLiveData<Request> request = new MutableLiveData();

    public FootballChatViewModel(ChatRepository repository) {
        this.mRepository = repository;

    }

    public void load(int page, int limit) {
        request.setValue(new Request(page, limit));
    }


    final private LiveData<Resource<ChatResponse>> result
            = Transformations.switchMap(request, new Function<Request, LiveData<Resource<ChatResponse>>>() {

        @Override
        public LiveData<Resource<ChatResponse>> apply(final Request input) {

            // 데이터 로드
            LiveData<Resource<List<ChatAndAttributeModel>>> resourceLiveData
                    = mRepository.onLoadFootballChatList(input.page, input.limit);

            final MediatorLiveData<Resource<ChatResponse>> mediator = new MediatorLiveData<Resource<ChatResponse>>();

            mediator.addSource(resourceLiveData, new Observer<Resource<List<ChatAndAttributeModel>>>() {
                @Override
                public void onChanged(@Nullable Resource<List<ChatAndAttributeModel>> gitHubDtos) {
                    ChatResponse resp = new ChatResponse(input.page, input.limit, gitHubDtos.getData());
                    Resource<ChatResponse> response = null;
                    switch (gitHubDtos.getStatus()){
                        case LOADING:
                            response =  Resource.<ChatResponse>loading(resp);
                            break;
                        case SUCCESS:
                            response =  Resource.<ChatResponse>success(resp);
                            break;
                        case ERROR:
                            response =  Resource.<ChatResponse>error(gitHubDtos.getException(), null);
                            break;

                    }
                    mediator.setValue(response);
                }
            });
            return mediator;
        }
    });

    public LiveData<Resource<ChatResponse>> getResult() {
        return result;
    }

    public void clearCache() {
        //repository.clearCache();
    }

    /*-----------------------------------------------------------------------*/

    public static class Request {
        final private int page, limit;
        public Request(int page, int limit) {
            this.page = page;
            this.limit = limit;
        }
        public int getLimit() {
            return limit;
        }
    }
}

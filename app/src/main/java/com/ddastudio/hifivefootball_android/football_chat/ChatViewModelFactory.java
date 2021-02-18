package com.ddastudio.hifivefootball_android.football_chat;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.ddastudio.hifivefootball_android.football_chat.dao.ChatDao;
import com.ddastudio.hifivefootball_android.football_chat.repo.ChatRepository;
import com.ddastudio.hifivefootball_android.football_chat.api.ChatService;

public class ChatViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    private final ChatRepository repository;

    ChatViewModelFactory(@NonNull ChatService api,
                         @NonNull ChatDao dao) {

        this.repository = new ChatRepository(dao, api);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.equals(FootballChatViewModel.class)) {
            //noinspection unchecked
            return (T) new FootballChatViewModel(repository);
        }

        try {
            return modelClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

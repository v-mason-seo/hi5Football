package com.ddastudio.hifivefootball_android.team_list;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.ddastudio.hifivefootball_android.data.manager.TeamRepository;

public class TeamListViewModelFactory implements ViewModelProvider.Factory {

    private final TeamRepository mDataSource;

    public TeamListViewModelFactory(TeamRepository dataSource) {
        this.mDataSource = dataSource;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if ( modelClass.isAssignableFrom(TeamListViewModel.class)) {
            return (T) new TeamListViewModel(mDataSource);
        }
        return null;
    }
}

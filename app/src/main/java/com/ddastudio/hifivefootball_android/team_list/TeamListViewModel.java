package com.ddastudio.hifivefootball_android.team_list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.ddastudio.hifivefootball_android.data.manager.TeamRepository;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;



public class TeamListViewModel extends ViewModel {

    MutableLiveData<List<TeamModel>> teams;
    TeamRepository mDataSource;

    public TeamListViewModel(TeamRepository dataSource) {
        this.mDataSource = dataSource;
    }

    public LiveData<List<TeamModel>> getTeams() {

        if ( teams == null ) {
            teams = new MutableLiveData<>();
            //LiveData<List<TeamModel>> data = mDataSource.loadTeamList(15, 0);
            //teams.setValue(data.getValue());
            Call<List<TeamModel>> call = mDataSource.getApi().loadTeamList(15, 0);
            call.enqueue(new Callback<List<TeamModel>>() {
                @Override
                public void onResponse(Call<List<TeamModel>> call, Response<List<TeamModel>> response) {
                    teams.setValue(response.body());
                }

                @Override
                public void onFailure(Call<List<TeamModel>> call, Throwable t) {

                }
            });
        }

        return teams;
    }

    public void getMoreData(int offset) {

        Timber.i("getMoreData offset : " + offset);
        //LiveData<List<TeamModel>> data = mDataSource.loadTeamList(15, offset);
        //teams.setValue(data.getValue());

        Call<List<TeamModel>> call = mDataSource.getApi().loadTeamList(15, offset);
        call.enqueue(new Callback<List<TeamModel>>() {
            @Override
            public void onResponse(Call<List<TeamModel>> call, Response<List<TeamModel>> response) {

                teams.getValue().addAll(response.body());
                teams.setValue(teams.getValue());

            }

            @Override
            public void onFailure(Call<List<TeamModel>> call, Throwable t) {

            }
        });
    }

//    private void loadTeamList() {
//
//        Call<MutableLiveData<List<TeamModel>>> call = mDataSource.loadTeamList(15, 0);
//        call.enqueue(new Callback<MutableLiveData<List<TeamModel>>>() {
//            @Override
//            public void onResponse(Call<MutableLiveData<List<TeamModel>>> call, Response<MutableLiveData<List<TeamModel>>> response) {
//                teams.setValue(response.body().getValue());
//            }
//
//            @Override
//            public void onFailure(Call<MutableLiveData<List<TeamModel>>> call, Throwable t) {
//
//            }
//        });
//    }

}

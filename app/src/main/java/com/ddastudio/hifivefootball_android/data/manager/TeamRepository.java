package com.ddastudio.hifivefootball_android.data.manager;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.ddastudio.hifivefootball_android.BuildConfig;
import com.ddastudio.hifivefootball_android.data.api.TeamsAPI;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class TeamRepository {

    static TeamRepository mInstance;
    Retrofit mRetrofit;
    TeamsAPI mAPI;

    private TeamRepository() {

        initRetrofit();
    }

    public static TeamRepository getInstance() {

        if ( mInstance == null ) {
            synchronized (TeamRepository.class) {
                if ( mInstance == null ) {
                    mInstance = new TeamRepository();
                }
            }
        }

        return mInstance;
    }

    private void initRetrofit() {

        if ( BuildConfig.DEBUG ) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);  // <-- this is the important line!

            mRetrofit = new Retrofit.Builder()
                    //.baseUrl("http://api.hifivesoccer.com:5400")
                    .baseUrl(BuildConfig.ARENA_SERVER_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();

        } else {

            mRetrofit = new Retrofit.Builder()
                    //.baseUrl("http://api.hifivesoccer.com:5400")
                    .baseUrl(BuildConfig.ARENA_SERVER_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        mAPI = mRetrofit.create(TeamsAPI.class);
    }

    /*---------------------------------------------------------------------------------------------*/

    public TeamsAPI getApi() {
        return mAPI;
    }

    public LiveData<List<TeamModel>> loadTeamList(int limit, int offset) {

        final MutableLiveData<List<TeamModel>> data = new MutableLiveData<>();
        Call<List<TeamModel>> call = mAPI.loadTeamList(limit, offset);
        call.enqueue(new Callback<List<TeamModel>>() {
            @Override
            public void onResponse(Call<List<TeamModel>> call, Response<List<TeamModel>> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<TeamModel>> call, Throwable t) {

            }
        });

        return data;
    }
}

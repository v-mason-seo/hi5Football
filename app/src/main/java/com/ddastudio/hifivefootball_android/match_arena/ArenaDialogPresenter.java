package com.ddastudio.hifivefootball_android.match_arena;

import android.support.annotation.NonNull;

import com.ddastudio.hifivefootball_android.data.manager.MatchesManager;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by hongmac on 2018. 3. 21..
 */

public class ArenaDialogPresenter implements BaseContract.Presenter {

    int mode;

    MatchModel matchData;
    ArenaDialogFragment mView;
    MatchesManager mMatchesManager;

    @NonNull
    CompositeDisposable mComposite;

    public ArenaDialogPresenter(int mode, MatchModel matchData) {
        this.mode = mode;
        this.matchData = matchData;
    }

    @Override
    public void attachView(BaseContract.View view) {
        this.mView = (ArenaDialogFragment)view;
        this.mMatchesManager = MatchesManager.getInstance();
        this.mComposite = new CompositeDisposable();
    }

    @Override
    public void detachView() {
        if ( mComposite != null )
            mComposite.clear();
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public MatchModel getMatchData() {
        return matchData;
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadParticipationUsers(Integer matchId) {

        mView.showLoading();

        Flowable<List<UserModel>> observable
                = mMatchesManager.onLoadParticipationUsers(matchId);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .doOnError(e -> mView.showErrorMessage(e.getMessage()))
                        .doOnNext(items -> {})
                        .subscribe(items -> {
                                    mView.onLoadFinishedUserList(items);
                                },
                                e -> mView.hideLoading(),
                                () ->{} )
        );
    }

    public void onLoadParticipationUserCount(Integer matchId) {

        mView.showLoading();

        Flowable<DBResultResponse> observable
                = mMatchesManager.onLoadParticipationUserCount(matchId);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .doOnError(e -> mView.showErrorMessage(e.getMessage()))
                        .doOnNext(count -> {})
                        .subscribe(count -> {
                                    //Toast.makeText(mView.getContext(), "[onLoadParticipationUserCount] count : " + count.getResult(), Toast.LENGTH_LONG).show();
                                },
                                e -> mView.hideLoading(),
                                () ->{} )
        );
    }
}

package com.ddastudio.hifivefootball_android.ui.presenter;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.ddastudio.hifivefootball_android.data.manager.CompetitionsManager;
import com.ddastudio.hifivefootball_android.data.model.footballdata.StandingModel;
import com.ddastudio.hifivefootball_android.team.TeamActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.ddastudio.hifivefootball_android.ui.fragment.LeagueTableFragment;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by hongmac on 2018. 1. 11..
 */

public class LeagueTablePresenter implements BaseContract.Presenter {

    int mCompetitionId;
    int mTeamId;
    int mTeamId2;
    LeagueTableFragment mView;
    CompetitionsManager mCompetitionsManager;

    @NonNull
    CompositeDisposable mComposite;

    public LeagueTablePresenter(int compId, int teamId, int teamid2) {
        this.mCompetitionId = compId;
        this.mTeamId = teamId;
        this.mTeamId2 = teamid2;
    }

    @Override
    public void attachView(BaseContract.View view) {
        this.mView = (LeagueTableFragment)view;
        this.mCompetitionsManager = CompetitionsManager.getInstance();
        this.mComposite = new CompositeDisposable();
    }

    @Override
    public void detachView() {
        if ( mComposite != null )
            mComposite.clear();
    }

    public int getCompetitionId() {
        return mCompetitionId;
    }

    public int getTeamId() {
        return mTeamId;
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 팀 프로필 액티비티 실행
     * @param teamId
     */
    public void openTeamActivity(int teamId) {
        Intent intent = new Intent(mView.getContext(), TeamActivity.class);
        intent.putExtra("ARGS_TEAM_ID", teamId);

        mView.startActivity(intent);
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadLeagueTable() {

        mView.showLoading();

        Flowable<List<StandingModel>> observable
                = mCompetitionsManager.onLoadLeagueTable(mCompetitionId);

        mComposite.add(
                observable
                        .flatMap(items -> Flowable.fromIterable(items))
                        .map(item -> {
                            if ( item.getTeamId() == mTeamId || item.getTeamId() == mTeamId2) {
                                item.setSelected(true);
                            }
                            return item;
                        }).toList().toFlowable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnError(err -> mView.showErrorMessage(err.getMessage()))
                        .subscribe(
                                items -> mView.onLoadFinishedLeagueTable(items),
                                e -> mView.hideLoading(),
                                () -> {}
                        ));
    }

    /*---------------------------------------------------------------------------------------------*/
}

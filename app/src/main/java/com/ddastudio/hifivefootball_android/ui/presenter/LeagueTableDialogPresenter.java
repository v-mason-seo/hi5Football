package com.ddastudio.hifivefootball_android.ui.presenter;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ddastudio.hifivefootball_android.data.manager.CompetitionsManager;
import com.ddastudio.hifivefootball_android.data.manager.FootballDataManager;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.StandingModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.ddastudio.hifivefootball_android.ui.fragment.LeagueTableDialogFragment;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

/**
 * Created by hongmac on 2017. 10. 25..
 */

public class LeagueTableDialogPresenter implements BaseContract.Presenter {

    int mType;
    int mCompetitionId;
    int mMatchId;
    LeagueTableDialogFragment mView;
    FootballDataManager mFootballManager;
    CompetitionsManager mCompetitionsManager;

    @NonNull
    CompositeDisposable mComposite;

    public LeagueTableDialogPresenter(int type) {
        this.mType = type;
    }

    @Override
    public void attachView(BaseContract.View view) {
        this.mView = (LeagueTableDialogFragment)view;
        this.mComposite = new CompositeDisposable();
        this.mFootballManager = FootballDataManager.getInstance();
        this.mCompetitionsManager = CompetitionsManager.getInstance();
    }

    @Override
    public void detachView() {
        if ( mComposite != null )
            mComposite.clear();
    }

    public int getmCompetitionId() {
        return mCompetitionId;
    }

    public int getmMatchId() {
        return mMatchId;
    }

    public void setCompetitionId(int competitionId) {
        this.mCompetitionId = competitionId;
    }

    public void setMatchId(int matchId) {
        this.mMatchId = matchId;
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadStandings() {

        mView.showLoading();

        Flowable<List<StandingModel>> observable
                = mCompetitionsManager.onLoadLeagueTable(mCompetitionId);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> {
                                    mView.onLoadFinished(items);
                                },
                                e -> {
                                    mView.hideLoading();
                                    mView.showErrorMessage("데이터 로드중 오류가 발생했습니다.\n" + e.getMessage());
                                },
                                () -> {}
                        ));
    }

    public void onLoadPlayerList(int offset, int limit) {

        Flowable<List<PlayerModel>> observable
                = mFootballManager.onLoadPlayerList(offset, limit);

        mComposite.add(
                observable
                        .flatMap(playerList -> Flowable.fromIterable(playerList))
                        .map(player -> {
                            player.setItemType(MultipleItem.BOTTOM_PLAYER_LIST);
                            return player; } )
                        .toList().toFlowable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> {
                                    mView.onLoadFinished(items);
                                },
                                e -> mView.showErrorMessage(e.getMessage()),
                                () -> {}
                        ));
    }

    public void onLoadTeamList(int offset, int limit) {

        Flowable<List<TeamModel>> observable
                = mFootballManager.onLoadTeamList(offset, limit);

        mComposite.add(
                observable
                        .flatMap(teamList -> Flowable.fromIterable(teamList))
                        .map(team -> {
                            team.setItemType(MultipleItem.BOTTOM_TEAM_LIST);
                            return team; } )
                        .toList().toFlowable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> {
                                    mView.onLoadFinished(items);
                                },
                                e -> mView.showErrorMessage("[onLoadTeamList]\n" + e.getMessage()),
                                () -> {}
                        ));
    }
}

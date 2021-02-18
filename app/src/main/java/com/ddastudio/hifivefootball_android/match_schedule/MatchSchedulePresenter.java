package com.ddastudio.hifivefootball_android.match_schedule;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.ddastudio.hifivefootball_android.data.manager.CompetitionsManager;
import com.ddastudio.hifivefootball_android.data.manager.FixturesManager;
import com.ddastudio.hifivefootball_android.data.manager.FootballDataManager;
import com.ddastudio.hifivefootball_android.data.manager.MatchesManager;
import com.ddastudio.hifivefootball_android.data.manager.TeamsManager;
import com.ddastudio.hifivefootball_android.data.model.football.CompModel;
import com.ddastudio.hifivefootball_android.data.model.football.CompetitionModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.match_arena.ArenaActivity;
import com.ddastudio.hifivefootball_android.competition.CompetitionActivity;
import com.ddastudio.hifivefootball_android.match_detail.MatchActivity;
import com.ddastudio.hifivefootball_android.team.TeamActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;

import org.parceler.Parcels;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by hongmac on 2017. 10. 13..
 */

public class MatchSchedulePresenter implements BaseContract.Presenter {

    Integer fixtureMode = null;
    Integer competitionId = null;
    Integer teamId = null;
    String fixtureDate;

    MatchScheduleFragment mView;
    FootballDataManager mFootballManager;
    TeamsManager mTeamsManager;
    CompetitionsManager mCompetitionsManager;

    FixturesManager mFixturesManager;
    MatchesManager mMatchesManager;

    @NonNull
    CompositeDisposable mComposite;

    public MatchSchedulePresenter(int mode) {
        competitionId = null;
        teamId = null;
        fixtureMode = mode;
    }

//    public MatchSchedulePresenter(int mode, Integer competitionId, Integer teamId) {
//        this.competitionId = competitionId;
//        this.teamId = teamId;
//    }

    @Override
    public void attachView(BaseContract.View view) {
        this.mView = (MatchScheduleFragment)view;
        this.mTeamsManager = TeamsManager.getInstance();
        this.mCompetitionsManager = CompetitionsManager.getInstance();
        this.mFixturesManager = FixturesManager.getInstance();
        this.mMatchesManager = MatchesManager.getInstance();

        this.mComposite = new CompositeDisposable();
    }

    @Override
    public void detachView() {

        if ( mComposite != null )
            mComposite.clear();
    }

    public int getCompetitionId() {
        return competitionId;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setCompetitionId(int comtitionId) {
        this.competitionId = comtitionId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public Integer getFixtureMode() {
        return fixtureMode;
    }

    public void setFixtureMode(Integer fixtureMode) {
        this.fixtureMode = fixtureMode;
    }

    public String getFixtureDate() {
        return fixtureDate;
    }

    public void setFixtureDate(String fixtureDate) {
        this.fixtureDate = fixtureDate;
    }

    /*---------------------------------------------------------------------------------------------*/

    public void openMatchActivity(MatchModel match) {
        Intent intent = new Intent(mView.getContext(), MatchActivity.class);
        intent.putExtra("ARGS_MATCH", Parcels.wrap(match));

        mView.startActivity(intent);
    }

    public void openCompetitionActivity(CompetitionModel competition) {
        Intent intent = new Intent(mView.getContext(), CompetitionActivity.class);
        intent.putExtra("ARGS_COMPETITION", Parcels.wrap(competition));

        mView.startActivity(intent);
    }

    public void openTeamActivity(int teamId) {
        Intent intent = new Intent(mView.getContext(), TeamActivity.class);
        intent.putExtra("ARGS_TEAM_ID", teamId);

        mView.startActivity(intent);
    }

    public void openArenaActivity(MatchModel match) {

        Intent intent = new Intent(mView.getContext(), ArenaActivity.class);
        intent.putExtra("ARGS_MATCH", Parcels.wrap(match));
        mView.startActivity(intent);
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadNextFixtures(Integer limit, Integer offset) {

        onLoadNextFixtures(limit, offset, teamId);
    }

    public void onLoadNextFixtures(Integer limit, Integer offset, Integer teamId) {

        mView.showLoading();

        Flowable<List<MatchModel>> observable
                = mFixturesManager.onLoadNextFixtures(limit, offset, teamId);

        mComposite.add(
                observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(items -> {})
                        .doOnError(e -> mView.showErrorMessage("매치정보를 불러오는중 오류가 발생했습니다.\n" + e.getMessage()))
                        .subscribe(item -> mView.onLoadFinishedNextFixtures(item),
                                e -> {})
        );
    }

    public void onLoadPreFixtures(Integer limit, Integer offset) {

        onLoadPreFixtures(limit, offset, teamId);
    }

    public void onLoadPreFixtures(Integer limit, Integer offset, Integer teamId) {

        mView.showLoading();

        Flowable<List<MatchModel>> observable
                = mFixturesManager.onLoadPreFixtures(limit, offset, teamId);

        mComposite.add(
                observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(items -> {})
                        .doOnError(e -> mView.showErrorMessage("매치정보를 불러오는중 오류가 발생했습니다.\n" + e.getMessage()))
                        .subscribe(item -> mView.onLoadFinishedPreFixtures(item),
                                e -> {})
        );
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadFixedFixture() {

        onLoadFixedFixture(getFixtureDate(), getFixtureDate());
    }

    public void onLoadFixedFixture(String compList) {

        onLoadFixedFixture(compList, getFixtureDate(), getFixtureDate());
    }

    public void onLoadFixedFixture(String fromdate, String todate) {

        onLoadFixedFixture(null, fromdate, todate);

    }

    public void onLoadFixedFixture(String compList, String fromdate, String todate) {

        mView.showLoading();

        Flowable<List<MatchModel>> observable
                = mMatchesManager.onLoadMatches(compList, fromdate, todate, 0, 1000);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(items -> {})
                        .doOnError(e -> mView.showErrorMessage(e.getMessage()))
                        .subscribe(items -> mView.onLoadFinishedFixedFixture(items),
                                e -> {},
                                () -> {})
        );
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadCompetitions() {

        mView.showLoading();

        Flowable<List<CompModel>> observable
                = mCompetitionsManager.onLoadCompetitions(1);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(items -> {})
                        .doOnError(e -> mView.showErrorMessage(e.getMessage()))
                        .subscribe(
                                items -> {
                                    mView.onLoadFinishedCompetitions(items);
                                },
                                e -> {mView.hideLoading();},
                                () -> {}
                        ));
    }
}

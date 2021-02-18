package com.ddastudio.hifivefootball_android.match_overview;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.data.manager.MatchesManager;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchOverviewModel;
import com.ddastudio.hifivefootball_android.data.model.overview.MatchPredictionsModel;
import com.ddastudio.hifivefootball_android.data.model.overview.MatchRecentFormModel;
import com.ddastudio.hifivefootball_android.match_arena.ArenaActivity;
import com.ddastudio.hifivefootball_android.player_rating.PlayerRatingActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;

import org.parceler.Parcels;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by hongmac on 2017. 12. 13..
 */

public class MatchOverviewPresenter implements BaseContract.Presenter {

    MatchOverviewFragment mView;

    MatchesManager mMatchesManager;

    @NonNull
    CompositeDisposable mComposite;

    MatchModel mMatchData;

    @Override
    public void attachView(BaseContract.View view) {
        this.mView = (MatchOverviewFragment)view;
        this.mMatchesManager = MatchesManager.getInstance();
        this.mComposite = new CompositeDisposable();
    }

//    public void onStart() {
//        this.mComposite = new CompositeDisposable();
//    }
//
//    public void onStop() {
//        if ( mComposite != null )
//            mComposite.clear();
//    }

    @Override
    public void detachView() {
        if ( mComposite != null )
            mComposite.clear();
    }

    public MatchModel getMatchData() {
        return mMatchData;
    }

    public void setMatchData(MatchModel mMatchData) {
        this.mMatchData = mMatchData;
    }

    /*---------------------------------------------------------------------------------------------*/

//    public void openPlayerRatingActivity(MatchModel matchData) {
//        Intent intent = new Intent(mView.getContext(), PlayerRatingActivity.class);
//
//        intent.putExtra("ARGS_MATCH_DATA", Parcels.wrap(matchData));
//        intent.putExtra("ARGS_MATCH_ID", matchData.getMatchId());
//        intent.putExtra("ARGS_MATCH_LOCAL_TEAM_ID", matchData.getHomeTeamName());
//        intent.putExtra("ARGS_MATCH_LOCAL_TEAM_IMAGE_URL", matchData.getHomeTeamEmblemUrl());
//        intent.putExtra("ARGS_MATCH_VISITOR_TEAM_ID", matchData.getAwayTeamId());
//        intent.putExtra("ARGS_MATCH_VISITOR_TEAM_IMAGE_URL", matchData.getAwayTeamEmblemUrl());
//
//        mView.startActivity(intent);
//    }

    public void openArenaActivity(MatchModel match) {

        Intent intent = new Intent(mView.getContext(), ArenaActivity.class);
        intent.putExtra("ARGS_MATCH", Parcels.wrap(match));
        mView.startActivity(intent);
    }

    public void openPlayerRatingActivity() {
        Intent intent = new Intent(mView.getContext(), PlayerRatingActivity.class);

        intent.putExtra("ARGS_MATCH_DATA", Parcels.wrap(getMatchData()));
//        intent.putExtra("ARGS_SELECTED_TEAM_ID", selectedTeamId);
//        intent.putExtra("ARGS_SELECTED_PLAYER_ID", selectedPlayerId);
//        intent.putExtra("ARGS_HOME_LINEUP", Parcels.wrap(localTeamList));
//        intent.putExtra("ARGS_AWAY_LINEUP", Parcels.wrap(visitorTeamList));
//
//        intent.putExtra("ARGS_MATCH_ID", mMatch.getMatchId());
//        intent.putExtra("ARGS_MATCH_LOCAL_TEAM_ID", mMatch.getHomeTeamName());
//        intent.putExtra("ARGS_MATCH_LOCAL_TEAM_IMAGE_URL", mMatch.getHomeTeamEmblemUrl());
//        intent.putExtra("ARGS_MATCH_VISITOR_TEAM_ID", mMatch.getAwayTeamId());
//        intent.putExtra("ARGS_MATCH_VISITOR_TEAM_IMAGE_URL", mMatch.getAwayTeamEmblemUrl());

        mView.startActivity(intent);
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 매치 기본정보 데이터 불러오기
     */
    public void onLoadData() {

        mView.showLoading();

        mComposite.add(
            Flowable.timer(10, TimeUnit.MILLISECONDS)
                    .flatMap(i -> getMatchOverviewObservable())
                    .delay(10, TimeUnit.MILLISECONDS)
                    .flatMap(val -> getMatchPredictionsObservable())
                    .delay(10, TimeUnit.MILLISECONDS)
                    .flatMap(val -> getdMatchRecentFormObservable())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ret -> {},
                            e -> mView.hideLoading(),
                            () -> mView.hideLoading()
                    )
        );
    }


    private Flowable<MatchOverviewModel> getMatchOverviewObservable() {

        Flowable<MatchOverviewModel> observable
                = mMatchesManager.onLoadMatchOverview(mMatchData.getMatchId())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnError(e -> mView.showErrorMessage("매치정보 로드 오류\n"+e.getMessage()))
                                .doOnNext(item -> {
                                    mView.onLoadOverview(item);
                                });


        return observable;
    }

    private Flowable<MatchPredictionsModel> getMatchPredictionsObservable() {

        String token="";

        if (App.getAccountManager().isAuthorized()) {
            token = App.getAccountManager().getHifiveAccessToken();
        }

        Flowable<MatchPredictionsModel> observable
                = mMatchesManager.onLoadMatchPredictions(token, mMatchData.getMatchId())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnError(e -> mView.showErrorMessage("승무패 로드 오류\n"+e.getMessage()))
                                .doOnNext(item -> {
                                    mView.onLoadPredictions(item);
                                });


        return observable;
    }

    public Flowable<MatchRecentFormModel> getdMatchRecentFormObservable() {

        Flowable<MatchRecentFormModel> observable
                = mMatchesManager.onLoadMatchRecentForm(mMatchData.getMatchId())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnError(e -> mView.showErrorMessage("최근전적 로드 오류\n"+e.getMessage()))
                                .doOnNext(item -> {
                                    mView.onLoadRecentForm(item);
                                });

        return observable;
    }

    /*----------------------------------------------------------------------------------------------*/


    public void onLoadMatchOverview() {

        Flowable<MatchOverviewModel> observable
                = mMatchesManager.onLoadMatchOverview(mMatchData.getMatchId());

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .doOnError(e -> mView.showErrorMessage("리그정보 로드 오류\n"+e.getMessage()))
                        .subscribe(item -> {
                                    mView.onLoadOverview(item);
                                },
                                e -> {},
                                () ->{} )
        );
    }

    public void onLoadMatchPredictions() {

        String token="";

        if (App.getAccountManager().isAuthorized()) {
            token = App.getAccountManager().getHifiveAccessToken();
        }

        Flowable<MatchPredictionsModel> observable
                = mMatchesManager.onLoadMatchPredictions(token, mMatchData.getMatchId());

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .doOnError(e -> mView.showErrorMessage(e.getMessage()))
                        .subscribe(item -> {
                                    mView.onLoadPredictions(item);
                                },
                                e -> {},
                                () ->{} )
        );
    }

    public void postMatchPredictions(String predictions) {

        String token;

        if (App.getAccountManager().isAuthorized()) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {
//            Snackbar.make(mView.getWindow().getDecorView(), mView.getResources().getString(R.string.is_not_authorized_info), Snackbar.LENGTH_LONG)
//                    .setAction("이동", v -> openLoginActivity())
//                    .show();
            return;
        }

        Flowable<DBResultResponse> observable
                = mMatchesManager.postMatchPredictions(token, mMatchData.getMatchId(), predictions);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .doOnError(e -> mView.showErrorMessage("매치정보 로드 오류\n"+e.getMessage()))
                        .subscribe(item -> {
                                    //mView.onLoadPredictions(item);
                                },
                                e -> {},
                                () ->{} )
        );
    }

    public void onLoadMatchRecentForm() {

        Flowable<MatchRecentFormModel> observable
                = mMatchesManager.onLoadMatchRecentForm(mMatchData.getMatchId());

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .doOnError(e -> mView.showMessage(e.getMessage()))
                        .subscribe(item -> {
                                    mView.onLoadRecentForm(item);
                                },
                                e -> {},
                                () ->{} )
        );
    }
}

package com.ddastudio.hifivefootball_android.match_summery;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.manager.ArenaManager;
import com.ddastudio.hifivefootball_android.data.manager.MatchesManager;
import com.ddastudio.hifivefootball_android.match_summery.model.CompetitionSectionWrapperModel;
import com.ddastudio.hifivefootball_android.match_summery.model.MatchSectionWrapperModel;
import com.ddastudio.hifivefootball_android.match_summery.model.PlayerSectionWrapperModel;
import com.ddastudio.hifivefootball_android.match_summery.model.SummeryBaseWrapperModel;
import com.ddastudio.hifivefootball_android.match_summery.model.TeamSectionWrapperModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.match_detail.MatchActivity;
import com.ddastudio.hifivefootball_android.match_detail.MatchDetailInfoActivity;
import com.ddastudio.hifivefootball_android.player.PlayerActivity;
import com.ddastudio.hifivefootball_android.team.TeamActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by hongmac on 2017. 12. 26..
 */

public class MainMatchPresenter implements BaseContract.Presenter {

    MainMatchFragment mView;
    MatchesManager mMatchesManager;
    ArenaManager mArenaManager;

    @NonNull
    CompositeDisposable mComposite;

    @Override
    public void attachView(BaseContract.View view) {
        this.mView = (MainMatchFragment)view;
        this.mComposite = new CompositeDisposable();
        this.mMatchesManager = MatchesManager.getInstance();
        this.mArenaManager = ArenaManager.getInstance();

    }

    @Override
    public void detachView() {
        if ( mComposite != null )
            mComposite.clear();
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 매치일정 전체보기 액티비티 실행
     * @param detailInfoType
     */
    public void openMatchDetailInfoActivity(int detailInfoType) {
        Intent intent = new Intent(mView.getContext(), MatchDetailInfoActivity.class);
        intent.putExtra("ARGS_MATCH_DETAIL_INFO_TYPE", detailInfoType);

        mView.startActivity(intent);
    }

    /**
     * 매치 액티비티 실행
     * @param match
     */
    public void openMatchActivity(MatchModel match) {
        Intent intent = new Intent(mView.getContext(), MatchActivity.class);
        intent.putExtra("ARGS_MATCH", Parcels.wrap(match));

        mView.startActivity(intent);
    }

    /**
     * 팀 프로필 액티비티 실행
     * @param teamId
     */
    public void openTeamActivity(int teamId) {
        Intent intent = new Intent(mView.getContext(), TeamActivity.class);
        intent.putExtra("ARGS_TEAM_ID", teamId);

        mView.startActivity(intent);
    }

    /**
     * 플레이어 프로필 액티비티 실행
     * @param playerId
     * @param playerName
     */
    public void openPlayerActivity(int playerId, String playerName) {
        Intent intent = new Intent(mView.getContext(), PlayerActivity.class);
        intent.putExtra("ARGS_PLAYER_ID", playerId);
        intent.putExtra("ARGS_PLAYER_NAME", playerName);

        mView.startActivity(intent);
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadData() {

        mView.showLoading();

        List<SummeryBaseWrapperModel> abcList = new ArrayList<>();
        mComposite.add(
                getMatchSectionObservable().map(items -> abcList.addAll(items))
                    .concatMap(val -> getMatchPlayerSectionObservable()).map(items -> abcList.addAll(items))
                    .concatMap(val -> getMatchTeamSectionObservable()).map(items -> abcList.addAll(items))
                        .concatMap(val -> getCompetitionSectionObservable()).map(items -> abcList.addAll(items))
                    .concatMap(val -> {
                        mView.onLoadFinished(abcList);
                        return Flowable.just(true);
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ret -> {},
                            e -> {},
                            () -> mView.hideLoading()
                    )
        );
    }

    public Flowable<List<MatchSectionWrapperModel>> getMatchSectionObservable() {

        Flowable<List<MatchSectionWrapperModel>> observable
                = mArenaManager.onLoadMatchSection()
                .map(items -> {
                    for(int i=0; i < items.size(); i++) {
                        for (int j=0; j < items.get(i).getMatches().size(); j++) {
                            items.get(i).getMatches().get(j).setItemType(ViewType.MATCH_SECTION);
                        }
                    }
                    return items;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(e -> mView.showErrorMessage("매치정보 로드 오류\n"+e.getMessage()));

        return observable;
    }

    public Flowable<List<TeamSectionWrapperModel>> getMatchTeamSectionObservable() {

        Flowable<List<TeamSectionWrapperModel>> observable
                = mArenaManager.onLoadMatchTeamSection()
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnError(e -> mView.showErrorMessage(e.getMessage()));

        return observable;
    }

    public Flowable<List<PlayerSectionWrapperModel>> getMatchPlayerSectionObservable() {

        Flowable<List<PlayerSectionWrapperModel>> observable
                = mArenaManager.onLoadMatchPlayerSection()
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnError(e -> mView.showErrorMessage("플레이어 로드 오류\n"+e.getMessage()));

        return observable;
    }

    public Flowable<List<CompetitionSectionWrapperModel>> getCompetitionSectionObservable() {

        Flowable<List<CompetitionSectionWrapperModel>> observable
                = mArenaManager.onLoadCompetitionSection()
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnError(e -> mView.showErrorMessage(e.getMessage()));

        return observable;
    }

    public Flowable<List<MatchModel>> getMatchObsevalbe(String fromdate, String todate) {

        Flowable<List<MatchModel>> observable
                = mMatchesManager.onLoadMatches(fromdate, todate, 0, 1000)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(e -> mView.showErrorMessage("경기일정 로드 오류\n"+e.getMessage()));

        return observable;
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadMatche(int matchId) {

        Flowable<MatchModel> observable
                = mMatchesManager.onLoadMatche(matchId);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(items -> {},
                                e -> {},
                                () -> {})
        );
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadMatches(String fromdate, String todate) {

        Flowable<List<MatchModel>> observable
                = mMatchesManager.onLoadMatches(fromdate, todate, 0, 1000);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(items -> {})
                        .doOnError(e -> mView.showErrorMessage(e.getMessage()))
                        .subscribe(items -> {
                                    //Log.i("hong", "onLoadMatches Items Size : " + items.size());
                                    },
                                e -> {},
                                () -> {})
        );
    }



    public void onLoadMatches(int compId, String fromdate, String todate) {

        Flowable<List<MatchModel>> observable
                = mMatchesManager.onLoadMatches(compId, fromdate, todate, 0, 1000);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(items -> {},
                                e -> {},
                                () -> {})
        );
    }
}

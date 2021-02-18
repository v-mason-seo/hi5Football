package com.ddastudio.hifivefootball_android.match_up;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.manager.ArenaManager;
import com.ddastudio.hifivefootball_android.data.model.football.CompetitionModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.match_arena.ArenaActivity;
import com.ddastudio.hifivefootball_android.competition.CompetitionActivity;
import com.ddastudio.hifivefootball_android.match_detail.MatchActivity;
import com.ddastudio.hifivefootball_android.match_detail.MatchDetailInfoActivity;
import com.ddastudio.hifivefootball_android.player_rating.PlayerRatingActivity;
import com.ddastudio.hifivefootball_android.team.TeamActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;

import org.parceler.Parcels;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by hongmac on 2018. 2. 22..
 */

public class MatchUpPresenter implements BaseContract.Presenter {

    int matchType;

    MatchUpFragment mView;
    ArenaManager mArenaManager;
    @NonNull
    CompositeDisposable mComposite;

    public MatchUpPresenter(int matchType) {
        this.matchType = matchType;
    }

    @Override
    public void attachView(BaseContract.View view) {
        mView = (MatchUpFragment)view;
        this.mArenaManager = ArenaManager.getInstance();
        this.mComposite = new CompositeDisposable();
    }

    @Override
    public void detachView() {
        if ( mComposite != null )
            mComposite.clear();
    }

    /*---------------------------------------------------------------------------------------------*/

    public int getMatchType() {
        return matchType;
    }

    public void setMatchType(int matchType) {
        this.matchType = matchType;
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

    /**
     * 매치일정 전체보기 액티비티 실행
     */
    public void openMatchDetailInfoActivity() {
        Intent intent = new Intent(mView.getContext(), MatchDetailInfoActivity.class);

        mView.startActivity(intent);
    }

    public void openPlayerRatingActivity(MatchModel match) {
        Intent intent = new Intent(mView.getContext(), PlayerRatingActivity.class);

        intent.putExtra("ARGS_MATCH_DATA", Parcels.wrap(match));

        mView.startActivity(intent);
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 매치업 데이터를 가져온다.
     * 라이브 매치 - MatchUpFragment.LIVE_MATCH ( live )
     * 최근 매치  - MatchUpFragment.RECENT_MATCH ( lastest )
     * 인기 매치  - MatchUpFragment.POPULAR_MATCH ( hit )
     */
    public void onLoadData() {

        switch (matchType) {
            case MatchUpFragment.LIVE_MATCH:
                onLoadReactionMatchList("live");
                break;

            case MatchUpFragment.RECENT_MATCH:
                onLoadReactionMatchList("lastest");
                break;

            case MatchUpFragment.POPULAR_MATCH:
                onLoadReactionMatchList("hit");
                break;

            case MatchUpFragment.YESTERDAY_MATCH:
                onLoadReactionMatchList("yesterday");
                break;

            case MatchUpFragment.TODAY_MATCH:
                onLoadReactionMatchList("today");
                break;

            case MatchUpFragment.TOMORROW_MATCH:
                onLoadReactionMatchList("tomorrow");
                break;

            case MatchUpFragment.FINISH_RECENT_MATCH:
                onLoadReactionMatchList("recent2");
                break;

            case MatchUpFragment.COME_MATCH:
                onLoadReactionMatchList("come");
                break;

            default:
                break;
        }
    }

    /**
     *
     * @param matchType live, lastest, hit
     */
    public void onLoadReactionMatchList(String matchType) {
        mView.showLoading();

        Flowable<List<MatchModel>> observable
                = mArenaManager.onLoadReactionMatches(matchType)
                                .flatMap(items -> Flowable.fromIterable(items))
                                .map(item -> {

                                    if ( matchType.equals("live")) {
                                        item.setItemType(ViewType.MATCH_UP_LIVE);
                                    } else if ( matchType.equals("lastest")) {
                                        item.setItemType(ViewType.MATCH_UP_RECENT);
                                    } else if ( matchType.equals("hit")) {
                                        item.setItemType(ViewType.MATCH_UP_POPULAR);
                                    } else if ( matchType.equals("recent") || matchType.equals("recent2")) {
                                        item.setItemType(ViewType.MATCH_UP_FINISH_RECENT);
                                    } else {
                                        item.setItemType(ViewType.MATCH_UP_POPULAR);
                                    }

                                    /*if ( matchType.equals("live")) {
                                        item.setItemType(ViewType.MATCH_UP_LIVE);
                                    } else if ( matchType.equals("lastest")) {
                                        item.setItemType(ViewType.MATCH_UP_RECENT);
                                    } else if ( matchType.equals("hit")) {
                                        item.setItemType(ViewType.MATCH_UP_POPULAR);
                                    }*/

                                    return item;
                                }).toList().toFlowable()
                ;

        mComposite.add(
                observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(items -> {})
                        .doOnError(e -> mView.showErrorMessage(e.getMessage()))
                        .subscribe(item -> mView.onLoadFinishMatchList(item),
                                e -> mView.hideLoading())
        );
    }
}

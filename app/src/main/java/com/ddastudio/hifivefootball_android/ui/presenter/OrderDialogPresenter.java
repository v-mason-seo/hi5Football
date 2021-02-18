package com.ddastudio.hifivefootball_android.ui.presenter;

import android.support.annotation.NonNull;

import com.ddastudio.hifivefootball_android.data.manager.CompetitionsManager;
import com.ddastudio.hifivefootball_android.data.manager.FixturesManager;
import com.ddastudio.hifivefootball_android.data.manager.MatchesManager;
import com.ddastudio.hifivefootball_android.data.model.football.CompModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.ui.OrderTest.MatchListDialogFragment;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

/**
 * Created by hongmac on 2017. 12. 30..
 */

public class OrderDialogPresenter implements BaseContract.Presenter {

    MatchListDialogFragment mView;
    CompetitionsManager mCompetitionsManager;
    FixturesManager mFixturesManager;
    MatchesManager mMatchesManager;

    @NonNull
    CompositeDisposable mComposite;

    int selectedCompId = 0;

    @Override
    public void attachView(BaseContract.View view) {
        this.mView = (MatchListDialogFragment)view;
        this.mComposite = new CompositeDisposable();
        this.mCompetitionsManager = CompetitionsManager.getInstance();
        this.mMatchesManager = MatchesManager.getInstance();
        this.mFixturesManager = FixturesManager.getInstance();
    }

    @Override
    public void detachView() {
        if ( mComposite != null )
            mComposite.clear();
    }

    /*---------------------------------------------------------------------------------------------*/

    public int getSelectedCompId() {
        return selectedCompId;
    }

    public void setSelectedCompId(int selectedCompId) {
        this.selectedCompId = selectedCompId;
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadCompetitions() {

        mView.showProgress();

        Flowable<List<CompModel>> observable
                = mCompetitionsManager.onLoadCompetitions();

        mComposite.add(
                observable
                        .flatMap(items -> Flowable.fromIterable(items))
                        .filter(item -> item.isActive())
                        .toList().toFlowable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(items -> {})
                        .doOnError(e -> {})
                        .subscribe(
                                items -> {
                                    mView.onLoadFinishedCompetitions(items);
                                },
                                e -> {mView.hideProgress();},
                                () -> {}
                        ));
    }

//    public void onLoadMatches(int competitionId, String matchDate) {
//
//        mView.showProgress();
//
//        Flowable<List<MatchModel>> observable
//                = mMatchesManager.onLoadMatches(competitionId, matchDate);
//
//        mComposite.add(
//                observable.observeOn(AndroidSchedulers.mainThread())
//                        .doOnNext(items -> Timber.d("[onLoadMatches] items size : %d", items.size()))
//                        .doOnError(e -> Timber.d("[onLoadMatches] error : %s", e.getMessage()))
//                        .subscribe(items -> mView.onLoadFinishedMatches(items),
//                                e -> {mView.hideProgress();},
//                                () -> {})
//        );
//    }

    public void onLoadMatches(int competitionId, String fromdate, String todate) {

        mView.showProgress();

//        String mstchDateJson
//                = DateUtils.getDatesBetweenJson(start, end, "yyyy-MM-dd", "yyyyMMdd");

        Flowable<List<MatchModel>> observable
                = mMatchesManager.onLoadMatches(competitionId, fromdate, todate, 0, 1000);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(items -> Timber.d("[onLoadMatches] items size : %d", items.size()))
                        .doOnError(e -> Timber.d("[onLoadMatches] error : %s", e.getMessage()))
                        .subscribe(items -> mView.onLoadFinishedMatches(items),
                                e -> {mView.hideProgress();},
                                () -> {})
        );
    }
}

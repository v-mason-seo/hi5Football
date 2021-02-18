package com.ddastudio.hifivefootball_android.competition;

import android.support.annotation.NonNull;

import com.ddastudio.hifivefootball_android.data.manager.FootballDataManager;
import com.ddastudio.hifivefootball_android.data.model.footballdata.MatchContainerModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.StandingModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by hongmac on 2017. 10. 28..
 */

public class CompetitionInfoPresenter implements BaseContract.Presenter {

    public static final int NEW_MATCH_DATA = 747;
    public static final int PRE_MATCH_DATA = 748;
    public static final int NEXT_MATCH_DATA = 749;

    int mDataType;
    int mCompetitionId;
    CompetitionInfoFragment mView;
    FootballDataManager mFootballManager;

    @NonNull
    CompositeDisposable mComposite;

    public CompetitionInfoPresenter(int dataType, int competitionId) {
        this.mDataType = dataType;
        this.mCompetitionId = competitionId;
    }

    @Override
    public void attachView(BaseContract.View view) {
        this.mView = (CompetitionInfoFragment)view;
        this.mComposite = new CompositeDisposable();
        this.mFootballManager = FootballDataManager.getInstance();
    }

    @Override
    public void detachView() {
        if ( mComposite != null && !mComposite.isDisposed())
            mComposite.clear();
    }

    public int getDataType() {
        return mDataType;
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadStandings() {

        Flowable<List<StandingModel>> observable
                = mFootballManager.onLoadStandings(mCompetitionId);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> {
                                    mView.onLoadFinished(items);

                                },
                                e -> {
                                    //mView.invisibleRefreshing();
                                    },
                                () -> {}//mView.invisibleRefreshing()
                        ));
    }

    public void onLoadMatches(String matchDate, int dataType) {

        Flowable<MatchContainerModel> observable
                = mFootballManager.onLoadMatches(matchDate, mCompetitionId);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                item -> {
                                    //log.d("hong", "matchlist size : " + items.size());
                                    //mView.onLoadNewMatchFinished(items);
                                    switch (dataType) {
                                        case NEW_MATCH_DATA:
                                            mView.onLoadNewMatchFinished(item);
                                            break;
                                        case PRE_MATCH_DATA:
                                            mView.onLoadPreMatchFinished(item);
                                            break;
                                        case NEXT_MATCH_DATA:
                                            mView.onLoadNextMatchFinished(item);
                                            break;
                                    }
                                },
                                e -> {
                                    //log.d("hong", "[onError] onLoadMatches2 : " + e.getMessage());
                                    //mView.invisibleRefreshing();
                                },
                                () -> {}
                        ));
    }
}

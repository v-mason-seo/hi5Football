package com.ddastudio.hifivefootball_android.match_event;

import android.support.annotation.NonNull;

import com.ddastudio.hifivefootball_android.data.manager.MatchesManager;
import com.ddastudio.hifivefootball_android.data.model.footballdata.MatchEventModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.ddastudio.hifivefootball_android.match_event.MatchTimeLineFragment;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by hongmac on 2017. 10. 27..
 */

public class MatchTimeLinePresenter implements BaseContract.Presenter {

    MatchModel mMatch;
    MatchTimeLineFragment mView;
    MatchesManager mMatchesManager;

    @NonNull
    CompositeDisposable mComposite;

    public MatchTimeLinePresenter(MatchModel match) {

        this.mMatch = match;

    }

    @Override
    public void attachView(BaseContract.View view) {
        this.mView = (MatchTimeLineFragment)view;
        this.mComposite = new CompositeDisposable();
        this.mMatchesManager = MatchesManager.getInstance();
    }

    @Override
    public void detachView() {
        if ( mComposite != null )
            mComposite.clear();
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadMatchEvents() {

        mView.showLoading();

        Flowable<List<MatchEventModel>> observable
                = mMatchesManager.onLoadMatchEvents(mMatch.getMatchId());

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .doOnError(err -> mView.showErrorMessage(err.getMessage()))
                        .doOnNext(items -> {}/*Log.i("hong", "onLoadMatchEvents size : " + items.size())*/)
                        .subscribe(
                                items -> mView.onLoadFinished(items),
                                e -> mView.hideLoading(),
                                () -> {}
                        ));
    }
}

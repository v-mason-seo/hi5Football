package com.ddastudio.hifivefootball_android.nunting_bset;

import android.support.annotation.NonNull;

import com.ddastudio.hifivefootball_android.data.manager.NuntingManager;
import com.ddastudio.hifivefootball_android.nunting_bset.model.SiteBoardModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;


import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class NuntingSiteListPresenter implements BaseContract.Presenter {

    NuntingSiteListFragment mView;
    NuntingManager mDataManager;

    @NonNull
    CompositeDisposable mComposite;

    @Override
    public void attachView(BaseContract.View view) {
        mView = (NuntingSiteListFragment)view;
        this.mComposite = new CompositeDisposable();
        this.mDataManager = NuntingManager.getInstance();
    }

    @Override
    public void detachView() {
        if ( mComposite != null )
            mComposite.clear();
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 사이트 정보를 가져온다.
     */
    public void onLoadSiteList() {

        mView.showLoading();

        Flowable<List<SiteBoardModel>> observerble =  mDataManager.onLoadSiteList();

        mComposite.add(
                observerble.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(items -> {
                                    mView.onLoadFinished(items);
                                },
                                e -> {
                                    mView.showErrorMessage(e.getMessage());
                                    mView.hideLoading();
                                },
                                () -> mView.hideLoading()
                        )
        );
    }

    /*---------------------------------------------------------------------------------------------*/
}

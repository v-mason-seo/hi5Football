package com.ddastudio.hifivefootball_android.nunting_bset;

import android.support.annotation.NonNull;

import com.ddastudio.hifivefootball_android.data.manager.NuntingManager;
import com.ddastudio.hifivefootball_android.nunting_bset.model.PostsItemModel;
import com.ddastudio.hifivefootball_android.nunting_bset.model.SiteBoardModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class NuntingPostListPresenter implements BaseContract.Presenter {

    SiteBoardModel.Boards board;
    SiteBoardModel.Name site;

    NuntingPostListFragment mView;
    NuntingManager mDataManager;

    @NonNull
    CompositeDisposable mComposite;

    @Override
    public void attachView(BaseContract.View view) {
        mView = (NuntingPostListFragment)view;
        this.mComposite = new CompositeDisposable();
        this.mDataManager = NuntingManager.getInstance();
    }

    @Override
    public void detachView() {
        if ( mComposite != null )
            mComposite.clear();
    }

    public SiteBoardModel.Boards getBoard() {
        return board;
    }

    public void setBoard(SiteBoardModel.Boards board) {
        this.board = board;
    }

    public SiteBoardModel.Name getSite() {
        return site;
    }

    public void setSite(SiteBoardModel.Name site) {
        this.site = site;
    }

    /*---------------------------------------------------------------------------------------------*/

    public void loadPostsList(final String siteId, final String boardId, final String yyyyMMddHH) {

        mView.showLoading();

        Flowable<PostsItemModel> observerble
                = mDataManager.onLoadPostsList(siteId, boardId, yyyyMMddHH);

        mComposite.add(
                observerble
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(item -> {
                                    mView.setCurrentPid(item.getPid());
                                    mView.setPrevPid(item.getPrevPid());
                                    mView.onLoadFinished(item.getItems());
                                },
                                e -> {
                                    mView.showErrorMessage(e.getMessage());
                                    mView.hideLoading();
                                },
                                () -> mView.hideLoading()
                        )
        );
    }
}

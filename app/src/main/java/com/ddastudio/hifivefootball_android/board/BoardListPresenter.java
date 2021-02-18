package com.ddastudio.hifivefootball_android.board;

import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.ddastudio.hifivefootball_android.room.AppDatabase;
import com.ddastudio.hifivefootball_android.board.model.BoardMasterModel;
import com.ddastudio.hifivefootball_android.data.manager.SettingsManager;
import com.ddastudio.hifivefootball_android.nunting_bset.NuntingActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class BoardListPresenter implements BaseContract.Presenter {

    BoardListFragment mView;
    SettingsManager mSettingsManager;

    @NonNull
    CompositeDisposable mComposite;

    LiveData<List<BoardMasterModel>> mBoardList;

    @Override
    public void attachView(BaseContract.View view) {

        mView = (BoardListFragment)view;

        this.mSettingsManager = SettingsManager.getInstance();
        this.mComposite = new CompositeDisposable();
    }

    @Override
    public void detachView() {
        if ( mComposite != null )
            mComposite.clear();
    }

    /*---------------------------------------------------------------------------------------------*/

    public void openNuntingActivity() {
        Intent intent = new Intent(mView.getContext(), NuntingActivity.class);
        mView.startActivity(intent);
    }

    /*---------------------------------------------------------------------------------------------*/

    public LiveData<List<BoardMasterModel>> getDBBoardList() {

        mBoardList = AppDatabase.getInstance(mView.getActivity()).boardsDao().getAsyncBoardsToUse();

        return mBoardList;
    }

    /**
     * 게시판 정보를 가져온다.
     * @param isUse
     */
    public void getBoardList(boolean isUse) {

        Flowable<List<BoardMasterModel>> observable
                = mSettingsManager.getBoardList(isUse);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .flatMap(items -> Flowable.fromIterable(items))
                        //.filter(item -> item.getGroup() == 200 || item.getGroup() == 400)
                        .toList()
                        .toFlowable()
                        .subscribe(
                                items -> AppDatabase.getInstance(mView.getActivity()).boardsDao().insertAll(items),
                                e -> mView.hideLoading(),
                                () -> mView.hideLoading()
                        ));
    }
}

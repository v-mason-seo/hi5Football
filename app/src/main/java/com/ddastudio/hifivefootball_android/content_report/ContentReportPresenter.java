package com.ddastudio.hifivefootball_android.content_report;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.manager.ReportRepository;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class ContentReportPresenter implements BaseContract.Presenter {

    ContentReportFragment mView;
    ReportRepository mRepository;
    @NonNull
    CompositeDisposable mComposite;

    ContentHeaderModel mContent;

    public ContentReportPresenter(ContentHeaderModel content) {
        this.mContent = content;
    }

    @Override
    public void attachView(BaseContract.View view) {
        this.mView = (ContentReportFragment)view;
        this.mComposite = new CompositeDisposable();
        this.mRepository = ReportRepository.getInstance();
    }

    @Override
    public void detachView() {
        if ( mComposite != null )
            mComposite.clear();
    }

    public ContentHeaderModel getContent() {
        return mContent;
    }

    public void setContent(ContentHeaderModel mContent) {
        this.mContent = mContent;
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onPostReport(int boardid,
                             String title,
                             String content,
                             int platform) {

        mView.showLoading();
        String token = App.getAccountManager().getHifiveAccessToken();

        Flowable<DBResultResponse> observable
                = mRepository.postReport(token, boardid, title, content, platform);

        mComposite.add(
                observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> {
                                    mView.hideLoading();
                                    mView.onPostFinished();
                                },
                                e -> {
                                    mView.showErrorMessage("[onPostReport]\n" + e.getMessage());
                                    mView.hideLoading();
                                },
                                () -> {}
                        ));
    }
}

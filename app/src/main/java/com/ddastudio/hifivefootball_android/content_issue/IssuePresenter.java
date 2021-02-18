package com.ddastudio.hifivefootball_android.content_issue;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.common.EntityType;
import com.ddastudio.hifivefootball_android.data.manager.BoardsManager;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.IssueModel;
import com.ddastudio.hifivefootball_android.content_viewer.ContentViewerActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.ddastudio.hifivefootball_android.content_list.ContentListFragment;

import org.parceler.Parcels;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by hongmac on 2017. 9. 25..
 */

public class IssuePresenter implements BaseContract.Presenter {

    int mIssueType;
    IssueFragment mView;
    BoardsManager mBoardManager;

    @NonNull CompositeDisposable mComposite;

    public IssuePresenter(int issueType) {
        mIssueType = issueType;
    }

    @Override
    public void attachView(BaseContract.View view) {
        this.mView = (IssueFragment)view;
        mComposite = new CompositeDisposable();
        mBoardManager = BoardsManager.getInstance();
    }

    public void onStart() {
//        if ( mComposite == null || mComposite.isDisposed()) {
//            mComposite = new CompositeDisposable();
//        }
    }

    @Override
    public void detachView() {
        if ( mComposite != null )
            mComposite.clear();
    }

    /*---------------------------------------------------------------------------------------------*/

    public void openContentViewActivity(ContentHeaderModel contentHeaderModel, int position) {
        Intent intent = new Intent(mView.getContext(), ContentViewerActivity.class);
        intent.putExtra("ARGS_CONTENTS_HEADER_MODEL", Parcels.wrap(contentHeaderModel));
        intent.putExtra("ARGS_POSTS_TYPE", EntityType.POSTS_GENERAL);
        intent.putExtra("ARGS_POSITION", position);
        intent.setFlags(/*Intent.FLAG_ACTIVITY_NEW_TASK| */Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //mView.startActivity(intent);
        mView.startActivityForResult(intent, ContentListFragment.REQUEST_CONTENT_VIEWER);
    }

    /*---------------------------------------------------------------------------------------------*/

    public void getIssueBoardContentList(int limit, int offset) {

        String token = App.getAccountManager().getHifiveAccessToken();
        Flowable<List<IssueModel>> observable
                = mBoardManager.getIssueBoardContentList(token, EntityType.BOARD_ISSUE, limit, offset, mIssueType);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> { mView.onLoadFinished(items);
                                },
                                e -> {
                                    mView.showErrorMessage(e.getMessage());
                                    mView.hideLoading();
                                },
                                () -> {}
                        ));
    }
}

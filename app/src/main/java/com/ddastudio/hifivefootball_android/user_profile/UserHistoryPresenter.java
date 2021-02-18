package com.ddastudio.hifivefootball_android.user_profile;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.data.manager.UserManager;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.community.UserCommentModel;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.content_viewer.ContentViewerActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.ddastudio.hifivefootball_android.content_list.ContentListFragment;

import org.parceler.Parcels;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by hongmac on 2017. 9. 15..
 */

public class UserHistoryPresenter implements BaseContract.Presenter {

    UserHistoryFragment mView;
    UserManager mUserManager;
    @NonNull
    CompositeDisposable mComposite;
    int selectMode;

    UserModel mUser;
    public UserHistoryPresenter(UserModel user, int selectMode) {
        mComposite = new CompositeDisposable();
        this.mUser = user;
        this.selectMode = selectMode;
    }

    @Override
    public void attachView(BaseContract.View view) {

        mView = (UserHistoryFragment)view;
        mUserManager = UserManager.getInstance();
    }

    @Override
    public void detachView() {
        if ( mComposite != null )
            mComposite.clear();
    }

    /*---------------------------------------------------------------------------------------------*/

    public int getSelectMode() {
        return selectMode;
    }

    public void setSelectMode(int selectMode) {
        this.selectMode = selectMode;
    }

    /*---------------------------------------------------------------------------------------------*/

    public void openContentViewActivity(ContentHeaderModel contentHeaderModel, int postType, int position) {
        Intent intent = new Intent(mView.getContext(), ContentViewerActivity.class);
        intent.putExtra("ARGS_CONTENTS_HEADER_MODEL", Parcels.wrap(contentHeaderModel));
        intent.putExtra("ARGS_POSTS_TYPE", postType);
        intent.putExtra("ARGS_POSITION", position);
        intent.setFlags(/*Intent.FLAG_ACTIVITY_NEW_TASK| */Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //mView.startActivity(intent);
        mView.startActivityForResult(intent, ContentListFragment.REQUEST_CONTENT_VIEWER);
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadData(int limit, int offset) {

        if ( selectMode == UserHistoryFragment.TYPE_CONTENT) {
            getUserContentList(limit, offset);
        } else if ( selectMode == UserHistoryFragment.TYPE_COMMENT) {
            getUserCommentList(limit, offset);
        } else if ( selectMode == UserHistoryFragment.TYPE_HIFIVE) {
            getUserLikeList(limit, offset);
        } else if ( selectMode == UserHistoryFragment.TYPE_SCRAP) {
            onLoadScrapList(limit, offset);
        }
    }

    public void getUserContentList(int limit, int offset) {

        mView.showLoading();

        Flowable<List<ContentHeaderModel>> observable
                = mUserManager.getUserContentList(mUser.getUsername(), limit, offset);

        mComposite.add(
                observable.flatMap(items -> Flowable.fromIterable(items))
                        .map(item -> {
                            item.setItemType(MultipleItem.USER_CONTENT);
                            return item;
                        }).toList().toFlowable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> {
                                    mView.onLoadFinishedData(items);
                                    },
                                e -> {
                                    mView.hideLoading();
                                    mView.showErrorMessage(e.getMessage());
                                },
                                () -> {}
                        ));
    }


    public void getUserCommentList(int limit, int offset) {

        mView.showLoading();

        Flowable<List<UserCommentModel>> observable
                = mUserManager.getUserCommentList(mUser.getUsername(), limit, offset);

        mComposite.add(
                observable
                        .flatMap(items -> Flowable.fromIterable(items))
                        .map(item -> {
                            item.setItemType(MultipleItem.USER_COMMENT);
                            return item;
                        }).toList().toFlowable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> {
                                    mView.onLoadFinishedData(items);
                                },
                                e -> {
                                    mView.hideLoading();
                                    mView.showErrorMessage(e.getMessage());
                                },
                                () -> {}
                        ));

    }

    public void getUserLikeList(int limit, int offset) {

        mView.showLoading();

        Flowable<List<ContentHeaderModel>> observable
                = mUserManager.getUserLikedList(mUser.getUsername(), limit, offset);

        mComposite.add(
                observable
                        .flatMap(items -> Flowable.fromIterable(items))
                        .map(item -> {
                            item.setItemType(MultipleItem.USER_CONTENT);
                            return item;
                        }).toList().toFlowable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> {
                                    mView.onLoadFinishedData(items);
                                },
                                e -> {
                                    mView.hideLoading();
                                    mView.showErrorMessage(e.getMessage());
                                },
                                () -> {}
                        ));

    }

    public void onLoadScrapList(int limit, int offset) {

        mView.showLoading();

        String token = App.getAccountManager().getHifiveAccessToken();

        Flowable<List<ContentHeaderModel>> observable
                = mUserManager.onLoadScrapList(token, mUser.getUsername(), limit, offset);

        mComposite.add(
                observable
                        .flatMap(items -> Flowable.fromIterable(items))
                        .map(item -> {
                            item.setItemType(MultipleItem.USER_CONTENT);
                            return item;
                        }).toList().toFlowable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> {
                                    mView.onLoadFinishedData(items);
                                },
                                e -> {
                                    mView.hideLoading();
                                    mView.showErrorMessage(e.getMessage());
                                },
                                () -> {}
                        ));

    }

}

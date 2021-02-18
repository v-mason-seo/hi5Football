package com.ddastudio.hifivefootball_android.ui.presenter;

import android.support.annotation.NonNull;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.common.EntityType;
import com.ddastudio.hifivefootball_android.data.manager.CommentsManager;
import com.ddastudio.hifivefootball_android.data.manager.ContentManager;
import com.ddastudio.hifivefootball_android.data.model.community.CommentModel;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.ddastudio.hifivefootball_android.ui.fragment.ItemListDialogFragment;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by hongmac on 2017. 9. 8..
 */

public class ItemListDialogPresenter implements BaseContract.Presenter {

    ItemListDialogFragment mView;
    ContentManager mContentManager;
    CommentsManager mCommentsManager;

    @NonNull
    CompositeDisposable mComposite;

    int mContentId;
    int mListType;

    public ItemListDialogPresenter(int contentId, int listType) {
        this.mComposite = new CompositeDisposable();
        this.mContentId = contentId;
        this.mListType = listType;
    }

    @Override
    public void attachView(BaseContract.View view) {

        this.mView = (ItemListDialogFragment)view;
        this.mContentManager = ContentManager.getInstance();
        this.mCommentsManager = CommentsManager.getInstance();
    }

    @Override
    public void detachView() {
        if ( mComposite != null )
            mComposite.clear();
    }


    /*---------------------------------------------------------------------------------------------*/

    public void onLoadData() {

        if ( mListType == EntityType.TYPE_COMMENT ) {
            getComments(mContentId);
        } else if ( mListType == EntityType.TYPE_LIKE) {
            getLikers(mContentId);
        } else if ( mListType == EntityType.TYPE_SCRAP) {
            getScrpUsers(mContentId);
        }
    }

    /**
     * 댓글 정보를 가져온다. ( 사용한함 )
     * @param id
     */
    public void getComments(int id) {

        Flowable<List<CommentModel>> observable = mCommentsManager.getComments(App.getAccountManager().getHifiveAccessToken(), id);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> {
                                    //mView.onLoadFinished(items);
                                }
                                        ,
                                e -> {},
                                () -> {}
                        ));
    }

    /**
     * 하이파이브한 사용자 정보를 가져온다.
     * @param id
     */
    public void getLikers(int id) {

        Flowable<List<UserModel>> observable = mContentManager.getLikers(id);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> {
                                    mView.onLoadFinished(items);
                                },
                                e -> {},
                                () -> {}
                ));
    }

    /**
     * 스크랩한 사용자 정보를 가져온다.
     * @param id
     */
    public void getScrpUsers(int id) {

        Flowable<List<UserModel>> observable = mContentManager.getScrpUsers(id);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> {
                                    mView.onLoadFinished(items);
                                },
                                e -> {},
                                () -> {}
                        ));
    }
}

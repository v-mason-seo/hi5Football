package com.ddastudio.hifivefootball_android.content_viewer;

import android.content.Intent;
import android.widget.Toast;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.manager.CommentsManager;
import com.ddastudio.hifivefootball_android.data.model.community.CommentModel;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.auth.LoginActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.ddastudio.hifivefootball_android.content_viewer.CommentInputFragment;

import java.util.List;
import java.util.NoSuchElementException;

import es.dmoral.toasty.Toasty;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by hongmac on 2017. 9. 18..
 */

public class CommentInputPresenter implements BaseContract.Presenter {

    CommentInputFragment mView;

    int mType;
    int mContentId;
    CommentsManager mCommentsManager;
    CompositeDisposable composite;
    CommentModel mTargetCommentData;


    public CommentInputPresenter() {

        composite = new CompositeDisposable();
    }

    public CommentInputPresenter(int type, int contentId) {

        this.mType = type;
        this.mContentId = contentId;
        composite = new CompositeDisposable();
    }

    public CommentInputPresenter(int type, CommentModel commentData) {

        this.mType = type;
        this.mTargetCommentData = commentData;
        composite = new CompositeDisposable();
    }

    @Override
    public void attachView(BaseContract.View view) {

        mView = (CommentInputFragment)view;
        mCommentsManager = CommentsManager.getInstance();
    }

    @Override
    public void detachView() {

        if ( composite != null ) {
            composite.clear();
        }
    }

    public int getType() {
        return mType;
    }

    public int getContentId() {
        return mContentId;
    }

    public void setContentId(int mContentId) {
        this.mContentId = mContentId;
    }

    public int getTargetCommentId() {

        if ( mTargetCommentData != null ) {
            return mTargetCommentData.getCommentId();
        }

        return -1;
    }

    public void setTargetCommentData(CommentModel mTargetCommentData) {
        this.mTargetCommentData = mTargetCommentData;
    }

    /*----------------------------------------------------------------------------------------------*/

    public void openLoginActivity() {
        Intent intent = new Intent(mView.getContext(), LoginActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mView.startActivity(intent);
    }

    /*----------------------------------------------------------------------------------------------*/

    public void getComments() {

        mView.showLoading();
        String token = App.getAccountManager().getHifiveAccessToken();

        Flowable<List<CommentModel>> observable
                = mCommentsManager.getComments(token, mContentId);

        composite.add(
                observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> {
                                    mView.onLoadFinishedComments(items);
                                    mView.hideLoading();
                                    },
                                e -> {
                                    if ( !(e instanceof NoSuchElementException)) {
                                        mView.showErrorMessage("댓글 로드 오류\n-> " + e.getMessage());
                                    }
                                    mView.hideLoading();
                                },
                                () -> {

                                }
                        ));
    }

    public void createComment(int parentId,
                              int groupId,
                              int depth,
                              String content,
                              int scrollPosition) {

        String token;

        if (App.getAccountManager().isAuthorized()) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {

            Toasty.warning(mView.getContext(), mView.getResources().getString(R.string.is_not_authorized_info), Toast.LENGTH_LONG).show();
            return;
        }

        mView.showLoading();

        Flowable<DBResultResponse> observable
                = mCommentsManager.createComment(token, mContentId, parentId, groupId, depth, content);

        composite.add(
                observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe( message -> {
                            getComments();
                            mView.hideLoading();
                        },
                        e -> {
                            mView.showErrorMessage("댓글입력 오류\n" + e.getMessage());
                            mView.hideLoading();
                        }
                ));
    }



    public void getGroupComments() {

        String token = "";

        if ( App.getAccountManager().isAuthorized() ) {
            token = App.getAccountManager().getHifiveAccessToken();
        }

        int groupId = mTargetCommentData.getGroupId() == 0
                ? mTargetCommentData.getCommentId() : mTargetCommentData.getGroupId();

        Flowable<List<CommentModel>> observable
                = mCommentsManager.getGroupComments(token, groupId);

        composite.add(
                observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> {
                                    mView.onLoadFinishedGroupComments(items);
                                    mView.hideLoading();
                                },
                                e -> {
                                    mView.showErrorMessage("댓글 로드 오류\n" + e.getMessage());
                                    mView.hideLoading();
                                }
                        ));
    }
}

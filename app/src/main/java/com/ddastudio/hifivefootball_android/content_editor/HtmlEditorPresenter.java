package com.ddastudio.hifivefootball_android.content_editor;

import android.content.Context;
import android.content.Intent;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.common.PostCellType;
import com.ddastudio.hifivefootball_android.data.event.EditorEvent;
import com.ddastudio.hifivefootball_android.data.manager.ContentManager;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.auth.LoginActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.ddastudio.hifivefootball_android.content_editor.HtmlEditorFragment;

import io.reactivex.Flowable;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by hongmac on 2017. 9. 10..
 */

public class HtmlEditorPresenter implements BaseContract.Presenter {

    Context mContext;
    HtmlEditorFragment mView;
    ContentManager mContentManager;
    final CompositeDisposable composite;

    public HtmlEditorPresenter() {
        composite = new CompositeDisposable();
    }

    @Override
    public void attachView(BaseContract.View view) {
        this.mView = (HtmlEditorFragment)view;
        this.mContext = mView.getContext();
        mContentManager = ContentManager.getInstance();
    }

    @Override
    public void detachView() {
        composite.clear();
    }

    /*---------------------------------------------------------------------------------------------*/

    public void openLoginActivity() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //mView.getActivity().startActivityForResult(intent, MainActivity.REQUEST_OPEN_USER_ACTIVITY);
        mView.startActivity(intent);
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 게시글 생성
     * @param boardid
     * @param title
     * @param preview
     * @param content
     * @param arena_id
     * @param user_id
     * @param ip
     * @param tag
     * @param imgs
     * @param allowComment
     */
    public void postContent(String token,
                            int boardid,
                            String title,
                            String preview,
                            String content,
                            Integer arenaid,
                            String tag,
                            @Nullable String imgs,
                            int bodyType,
                            PostCellType cellType,
                            int allowComment) {


        Flowable<DBResultResponse> observable
                = mContentManager.postContent(token, boardid, title, preview, content, arenaid, null, null, tag, imgs, bodyType, cellType.value(), allowComment);


        Disposable disposable
                = observable.subscribe(dbResultModel -> {
                    App.getInstance().bus().send(new EditorEvent.PostContentStatusEvent(EditorEvent.PostContentStatusEvent.STATUS_COMPLETE, boardid));
                },
                e -> {
                    App.getInstance().bus().send(new EditorEvent.PostContentStatusEvent(EditorEvent.PostContentStatusEvent.STATUS_ERROR, boardid));
                },
                () -> { });

        composite.add(disposable);
    }


    public void updateContent(String token,
                              int contentid,
                              String title,
                              String preview,
                              String content,
                              int boardid,
                              String tag,
                              @Nullable String imgs,
                              int allowComment,
                              int bodytype,
                              int user_id) {

//        Flowable<DBResultResponse> observable
//                = mContentManager.updateContent(token, contentid, boardid, title, preview, content, ar);
//
//        Disposable disposable
//                = observable.subscribe(dbResultModel -> {},
//                e -> mView.showErrorMessage(e.getMessage()),
//                () -> {});
//
//        composite.add(disposable);
    }
}

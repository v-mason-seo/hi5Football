package com.ddastudio.hifivefootball_android.my_news;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.common.PostBodyType;
import com.ddastudio.hifivefootball_android.data.manager.ContentManager;
import com.ddastudio.hifivefootball_android.data.manager.NotificationManager;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.community.NotificationModel;
import com.ddastudio.hifivefootball_android.content_viewer.ContentViewerActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;

import org.parceler.Parcels;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by hongmac on 2017. 10. 18..
 */

public class MyNewsPresenter implements BaseContract.Presenter {

    int mType;
    MyNewsFragment mView;
    ContentManager mContentManager;
    NotificationManager mNotificationManager;
    @NonNull
    CompositeDisposable mComposite;

    public MyNewsPresenter() {
        mType = MyNewsFragment.NOTIFICATION_TYPE_UNREAD;
    }

    public MyNewsPresenter(int notificationType) {
        mType = notificationType;
    }

    @Override
    public void attachView(BaseContract.View view) {
        this.mView = (MyNewsFragment)view;
        mComposite = new CompositeDisposable();
        this.mContentManager = ContentManager.getInstance();
        this.mNotificationManager = NotificationManager.getInstance();
    }

    @Override
    public void detachView() {
        if ( mComposite != null )
            mComposite.clear();
    }

    public int getType() {
        return mType;
    }

    /*---------------------------------------------------------------------------------------------*/

    public void openContentViewActivity(ContentHeaderModel contentHeaderModel, int postType, int position) {
        Intent intent = new Intent(mView.getContext(), ContentViewerActivity.class);
        intent.putExtra("ARGS_CONTENTS_HEADER_MODEL", Parcels.wrap(contentHeaderModel));
        intent.putExtra("ARGS_POSTS_TYPE", postType);
        intent.putExtra("ARGS_POSITION", position);
        intent.setFlags(/*Intent.FLAG_ACTIVITY_NEW_TASK| */Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mView.startActivity(intent);
        //mView.startActivityForResult(intent, ContentListFragment.REQUEST_CONTENT_VIEWER);
    }

    /*---------------------------------------------------------------------------------------------*/

    private void openContentActivity(ContentHeaderModel item) {

        int bodyType = item.getBodytype();

        if (PostBodyType.toEnum(bodyType) == PostBodyType.LINK) {
            openLinkActivity(item.getLink());
        } else {
            openContentViewActivity(item);
        }
    }

    public void openLinkActivity(String url) {
        try {
            Uri link = Uri.parse(url);

            Intent intent = new Intent(Intent.ACTION_VIEW, link);
            mView.startActivity(intent);
        } catch (Exception ex ) {
            mView.showErrorMessage("올바르지 않은 링크주소입니다\n" + ex.getMessage());
        }
    }

    public void openContentViewActivity(ContentHeaderModel contentHeaderModel) {

        Intent intent;

        intent = new Intent(mView.getContext(), ContentViewerActivity.class);
        intent.putExtra("ARGS_CONTENTS_HEADER_MODEL", Parcels.wrap(contentHeaderModel));
        intent.setFlags(/*Intent.FLAG_ACTIVITY_NEW_TASK| */Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mView.startActivity(intent);
        //mView.startActivityForResult(intent, ContentListFragment.REQUEST_CONTENT_VIEWER);

    }

    public void confirmAndExecuteActivity(Integer notificationTypeId, Integer contentId, Integer position) {

        String token;

        if ( App.getAccountManager().isAuthorized() ) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {
            mView.showErrorMessage("토큰이 만료되었습니다. 다시 로그인해주세요");
            return;
        }

        //
        // 내소식 읽음 처리
        //
        Flowable<DBResultResponse> observable
                = mNotificationManager.confirmNotification2(token, notificationTypeId, contentId);

        //
        // 데이터 가져오기
        //
        Flowable<ContentHeaderModel> observable1
                = mContentManager.getContent(token, contentId)
                .observeOn(AndroidSchedulers.mainThread());


        mComposite.add(
            observable
                    .filter(res -> ( res != null && res.getResult() > 0))
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(res -> {
                    mView.onFinishedConfirm(position);
                })
                .concatMap(res -> observable1)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(content -> {
                    openContentActivity(content);
                }).subscribe(data -> {
                    mView.hideLoading();
                    },
                e -> {
                    mView.showErrorMessage("[오류] " + e.toString());
                    mView.hideLoading();
                })
        );
    }

    public void onLoadContentData(Integer contentId) {

        String token = "";
        if ( App.getAccountManager().isAuthorized() ) {
            token = App.getAccountManager().getHifiveAccessToken();
        }

        //
        // 데이터 가져오기
        //
        Flowable<ContentHeaderModel> observable1
                = mContentManager.getContent(token, contentId);


        mComposite.add(
                observable1
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(content -> {
                                    openContentActivity(content);
                                    mView.hideLoading();
                                },
                                e -> {
                                    mView.showErrorMessage("[오류] " + e.toString());
                                    mView.hideLoading();
                                })
        );

    }

    public void onLoadNotification(final boolean reset) {

        String token;

        if ( App.getAccountManager().isAuthorized() ) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {
            return;
        }

        mView.showLoading();

        mComposite.clear();
        Flowable<List<NotificationModel>> observable
                = mNotificationManager.getNotification(token, mType == MyNewsFragment.NOTIFICATION_TYPE_UNREAD ? false : true);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .doOnError(e -> mView.showErrorMessage(e.getMessage()))
                        .subscribe(
                                items -> mView.onLoadFinished(items, reset),
                                e -> {
                                    mView.hideLoading();
                                    mView.showErrorMessage("알림메시지 로드중 오류가 발생했습니다.\n" + e.getMessage());
                                },
                                () -> {}
                        ));
    }

    public void confirmNotification(int notificationId, int position) {

        String token;

        if ( App.getAccountManager().isAuthorized() ) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {
            return;
        }

        Flowable<DBResultResponse> observable
                = mNotificationManager.confirmNotification(token, notificationId);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .doOnError(e -> mView.showErrorMessage("알림메시지 읽음처중 오류가 발생했습니다.\n" + e.getMessage()))
                        .subscribe(
                                res -> {
                                    if ( res.getResult() > 0) {
                                        mView.onFinishedConfirm(position);
                                    } else {
                                        mView.showMessage("읽음처리 실패");
                                    }
                                },
                                e -> {
                                    mView.hideLoading();
                                },
                                () -> {}
                        ));
    }

    /**
     * 내소식 읽음 처리
     * @param notificationTypeId
     * @param contentId
     * @param position
     */
    public void confirmNotification2(int notificationTypeId, int contentId, int position) {

        String token;

        if ( App.getAccountManager().isAuthorized() ) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {
            return;
        }

        Flowable<DBResultResponse> observable
                = mNotificationManager.confirmNotification2(token, notificationTypeId, contentId);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .doOnError(e -> mView.showErrorMessage("알림메시지 읽음처중 오류가 발생했습니다.\n" + e.getMessage()))
                        .subscribe(
                                res -> {
                                    if ( res.getResult() > 0) {
                                        mView.onFinishedConfirm(position);
                                    } else {
                                        mView.showMessage("읽음처리 실패");
                                    }
                                },
                                e -> {
                                    mView.hideLoading();
                                },
                                () -> {}
                        ));
    }


}

package com.ddastudio.hifivefootball_android.content_column_news;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.PostBoardType;
import com.ddastudio.hifivefootball_android.data.manager.BoardsManager;
import com.ddastudio.hifivefootball_android.data.manager.ContentManager;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.auth.LoginActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;

import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by hongmac on 2017. 11. 30..
 */

public class ColumnNewsPresenter implements BaseContract.Presenter {

    ColumnNewsFragment mView;
    BoardsManager mManager;
    ContentManager mContentManager;

    @NonNull CompositeDisposable mComposite;

    int BOARD_TYPE;
    int BOARD_HUMOR = 420;
    int BOARD_COLUMN = 350;

    public ColumnNewsPresenter(int type) {
        BOARD_TYPE = type;
    }

    @Override
    public void attachView(BaseContract.View view) {

        mView = (ColumnNewsFragment)view;
        mContentManager = ContentManager.getInstance();
        mManager = BoardsManager.getInstance();

        mComposite = new CompositeDisposable();

    }

    public void onStart() {
//        mComposite = new CompositeDisposable();
    }

    public void onStop() {
//        if ( mComposite != null )
//            mComposite.clear();
    }

    @Override
    public void detachView() {
        if ( mComposite != null )
            mComposite.clear();
    }

    public PostBoardType getBoardType() {
        return PostBoardType.toEnum(BOARD_TYPE);
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 로그인 액티비티 실행
     */
    public void openLoginActivity() {
        Intent intent = new Intent(mView.getContext(), LoginActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mView.startActivity(intent);
    }

    /*---------------------------------------------------------------------------------------------*/

    public void getContentList(int limit, int offset/*, boolean loadMore*/) {

        String token = App.getAccountManager().getHifiveAccessToken();
        Flowable<List<ContentHeaderModel>> observable
                = mManager.getBoardContentList(token, BOARD_TYPE, limit, offset);

        mComposite.add(
                observable.concatMap(list -> Flowable.fromIterable(list))
                        .map(item -> {
                            item.setItemType(BOARD_TYPE);
                            return item;
                        }).toList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> {
                                    mView.onLoadFinished(items);
                                }/*,
                                e -> Timber.d("[getHumorList] error : %s", e.getMessage()),
                                () -> {}*/
                        ));
    }

    public void setContentLike(ContentHeaderModel content, int hifive_count, int position) {

        String token;

        if (App.getAccountManager().isAuthorized()) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {
            Snackbar.make(mView.getActivity().getWindow().getDecorView(), mView.getResources().getString(R.string.is_not_authorized_info), Snackbar.LENGTH_LONG)
                    .setAction("이동", v -> openLoginActivity())
                    .show();

            return;
        }

        if ( App.getAccountManager().isSameUser(content.getUserName())) {
            Toasty.normal(mView.getContext(), "본인의 글에는 하이파이브를 할 수 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }

        mView.updateHifive(content, hifive_count, position);

        Flowable<DBResultResponse> observable
                = mContentManager.setLike(token, content.getContentId(), hifive_count);

        mComposite.add(
                observable.subscribe(
                        result -> {
                            if ( result.getResult() > 0) {

                            } else {
                                // TODO: 2017. 8. 18. 정상적으로 처리가 되지 않으면 메시지 또는 다시 시도 같은 처리로직이 필요함
                            }
                        },
                        e -> {
                            mView.showErrorMessage(e.getMessage());
                        }
                ));
    }
}

package com.ddastudio.hifivefootball_android.user_profile;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.Constants;
import com.ddastudio.hifivefootball_android.data.manager.UserManager;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.glide.Glide4Engine;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.ddastudio.hifivefootball_android.utils.AWSUtil;
import com.ddastudio.hifivefootball_android.utils.DateUtils;
import com.ddastudio.hifivefootball_android.utils.FileUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import id.zelory.compressor.Compressor;
import io.reactivex.Flowable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hongmac on 2017. 9. 25..
 */

public class UserProfilePresenter implements BaseContract.Presenter {

    UserModel user;
    UserProfileActivity mView;
    UserManager mUserManager;
    @NonNull
    CompositeDisposable _composite;

    public UserProfilePresenter(UserModel user) {
        this.user = user;
    }

    @Override
    public void attachView(BaseContract.View view) {
        mView = (UserProfileActivity)view;
        _composite = new CompositeDisposable();
        mUserManager = UserManager.getInstance();
    }

    @Override
    public void detachView() {
        if ( _composite != null )
            _composite.clear();
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     *
     * @param token
     */
    public void onLoadLoginUser(String token) {

        Flowable<UserModel> observable
                = mUserManager.getLoginUser(token);

        _composite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> {
                                    //Log.i("hong", "onLoadStandings size : " + items.size());
                                    //mView.onLoadFinishedMatch(items);
                                    user = items;
                                    mView.updateAvatar(user, true);
                                },
                                e -> mView.showErrorMessage(e.getMessage()),
                                () -> {}
                        ));
    }

    /**
     * 유저(다른사람) 정보 조회
     * @param username
     */
    public void onLoadUser(String username) {

        Flowable<UserModel> observable
                = mUserManager.onLoadUserInfo(username);

        _composite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                items -> {
                                    user = items;
                                    mView.onLoadFinishedUserInfo(user);
                                },
                                e -> mView.showErrorMessage(e.getMessage()),
                                () -> {}
                        ));
    }

    public void updateAvatar(String avatarUrl) {

        String token;

        if (App.getAccountManager().isAuthorized()) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {
            Snackbar.make(mView.getWindow().getDecorView(), mView.getResources().getString(R.string.is_not_authorized_info), Snackbar.LENGTH_LONG)
                    //.setAction("이동", v -> openLoginActivity())
                    .show();

            return;
        }

        Flowable<DBResultResponse> observable
                = mUserManager.updateAvatar(token, avatarUrl);

        _composite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(dbResultResponse -> {
                            if ( dbResultResponse.getResult() > 0 ) {
                                user.setAvatarUrl(avatarUrl);
                                mView.updateAvatar(user, false);
                            }

                            mView.hideLoading();
                        }, e -> {
                            mView.showErrorMessage(e.getMessage());
                            mView.hideLoading();
                        })
        );
    }

    public void updateUserInfo(String nickname,
                               String profile,
                               String avatarUrl) {

        String token;

        if (App.getAccountManager().isAuthorized()) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {
            Snackbar.make(mView.getWindow().getDecorView(), mView.getResources().getString(R.string.is_not_authorized_info), Snackbar.LENGTH_LONG)
                    //.setAction("이동", v -> openLoginActivity())
                    .show();

            return;
        }

        Flowable<DBResultResponse> observable
                = mUserManager.onUpdateUserInfo(token, nickname, profile, avatarUrl);

        _composite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(dbResultResponse -> {
                            if (dbResultResponse.getResult() > 0) {
                                user.setNickname(nickname);
                                user.setProfile(profile);
                                user.setAvatarUrl(avatarUrl);
                                mView.updateAvatar(user, false);
                            }

                            mView.hideLoading();
                        }, e -> {
                            mView.showErrorMessage(e.getMessage());
                            mView.hideLoading();
                        })
        );
    }

    /*---------------------------------------------------------------------------------------------*/

    public void openLocalImagePicker() {

        RxPermissions rxPermissions = new RxPermissions(mView);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {

                            Matisse.from(mView)
                                    .choose(MimeType.ofAll(), false)
                                    .countable(true)
                                    .maxSelectable(1)
                                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                    .thumbnailScale(0.85f)
                                    .imageEngine(new Glide4Engine())
                                    .forResult(UserProfileActivity.REQUEST_CODE_PROFILE_CHOOSE);
                        } else {
                            mView.showErrorMessage(mView.getResources().getString(R.string.permission_request_denied));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showErrorMessage(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    public void imageCompressNUpload(List<Uri> obtainResults) {

        mView.showDialogLoading();

        Compressor imageCompressor
                = new Compressor(mView).setQuality(80).setCompressFormat(Bitmap.CompressFormat.JPEG);

        Disposable disposable
                = Flowable.fromArray(obtainResults)
                .flatMap(uris -> Flowable.fromIterable(uris))
                .subscribeOn(Schedulers.io())
                .map(uri -> FileUtil.getFile(mView.getApplicationContext(), uri))
                .flatMap(file -> imageCompressor.compressToFileAsFlowable(file))
                //.delay(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        file -> uploadS3(file),
                        e -> {
                            mView.hideDialogLoading();
                            mView.showErrorMessage("이미지 업로드중 오류가 발생했습니다\n" + e.getMessage());
                        },
                        () -> {}
                );

        _composite.add(disposable);

    }

    private void uploadS3(File file) {

        TransferUtility transferUtility = AWSUtil.getTransferUtility(mView);
        String fileName = getImageFileName(App.getAccountManager().getUserName());

        try {
            TransferObserver observer = transferUtility.upload(Constants.UPLOAD_PROFILE_ORIGINAL_BUCKET_NAME, fileName, file);

            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {

                    if ( state == TransferState.COMPLETED) {
                        observer.cleanTransferListener();
                        mView.setAvatarFileName(fileName);
                        mView.hideDialogLoading();
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                }

                @Override
                public void onError(int id, Exception ex) {

                    mView.showErrorMessage(ex.getMessage());
                    observer.cleanTransferListener();
                    mView.hideDialogLoading();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            //Log.d("hong", "[uploadS3]" + e.getMessage());
            mView.hideDialogLoading();
        }
    }

    private String getImageFileName(String userName) {

        String dateString = DateUtils.convertDateToString(Calendar.getInstance().getTime(), "yyyyMMdd_HHmmss");
        return "profile_" + userName + "_" + dateString + ".jpg";
    }

}

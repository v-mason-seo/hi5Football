package com.ddastudio.hifivefootball_android.auth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.Constants;
import com.ddastudio.hifivefootball_android.data.event.LoginEvent;
import com.ddastudio.hifivefootball_android.data.event.UserAccountEvent;
import com.ddastudio.hifivefootball_android.data.manager.AuthManager;
import com.ddastudio.hifivefootball_android.data.manager.UserManager;
import com.ddastudio.hifivefootball_android.data.model.CheckEmailModel;
import com.ddastudio.hifivefootball_android.data.model.CheckUserNameModel;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.glide.Glide4Engine;
import com.ddastudio.hifivefootball_android.signin.AccountUser;
import com.ddastudio.hifivefootball_android.terms.TermsConditionsActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.ddastudio.hifivefootball_android.utils.AWSUtil;
import com.ddastudio.hifivefootball_android.utils.FileUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.io.File;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import id.zelory.compressor.Compressor;
import io.reactivex.Flowable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by hongmac on 2017. 9. 6..
 */

public class SignupPresenter implements BaseContract.Presenter {

    SignupActivity mView;
    AuthManager mAuthManager;
    UserManager mUserManager;
    @NonNull
    CompositeDisposable mComposite;

    public SignupPresenter() {

        mComposite = new CompositeDisposable();
    }

    @Override
    public void attachView(BaseContract.View view) {
        this.mView = (SignupActivity)view;
        mAuthManager = AuthManager.getInstance();
        mUserManager = UserManager.getInstance();
    }

    @Override
    public void detachView() {
        if ( mComposite != null )
            mComposite.clear();
    }

    /*---------------------------------------------------------------------------------------------*/

    public void openTermsConditionsActivity() {
        Intent intent = new Intent(mView.getApplicationContext(), TermsConditionsActivity.class);
        mView.startActivity(intent);
    }

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
                                    .forResult(mView.REQUEST_CODE_PROFILE_CHOOSE);
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

    public void imageCompressNUpload(Uri avatarUri) {

        if ( avatarUri == null )
            return;

        Compressor imageCompressor
                = new Compressor(mView)
                    .setMaxWidth(180)
                    .setMaxHeight(240)
                    .setQuality(30)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG);

        Disposable disposable = Flowable.just(avatarUri)
                .filter(uri -> uri != null)
                .subscribeOn(Schedulers.io())
                .map(uri -> FileUtil.getFile(mView.getApplicationContext(), uri))
                .flatMap(file -> imageCompressor.compressToFileAsFlowable(file))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        file -> {
                            //Timber.d("[imageCompressNUpload] call image upload function!");
                            uploadS3(file);
                        },
                        e -> {  mView.hideLoading();
                            //Timber.d("[imageCompressNUpload] error : %s", e.getMessage());
                            // 이미지 업로드중 오류가 발생하더라도 회원가입은 완료된 상태이기 때문에 완료 이벤트를 보낸다.
                            App.getInstance().bus().send(new UserAccountEvent.SignIn());
                            Toasty.error(mView.getApplicationContext(), "프로필 이미지 업로드 중 오류가 발생했습니다.\r\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                            mView.finish();
                            },
                        () -> {}
                );

        mComposite.add(disposable);

    }

    public void uploadS3(File file) {

        //showLoading();
        TransferUtility transferUtility = AWSUtil.getTransferUtility(mView);
        String fileName = getImageFileName(App.getAccountManager().getUserName());

        //Log.i("hong", "file size : " + FileUtil.getReadableFileSize(file.length()));
        //Log.i("hong", "file name : " + fileName);

        try {
            TransferObserver observer = transferUtility.upload(Constants.UPLOAD_PROFILE_ORIGINAL_BUCKET_NAME, fileName, file);

            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {

                    //Log.i("hong", String.format("[onStateChanged] id : %d, state : %s", id, state.toString()));

                    if ( state == TransferState.COMPLETED) {
                        // TODO: 2017. 9. 11. 베이스 url 변수로 저장할 것.
                        //String url = "https://s3.ap-northeast-2.amazonaws.com/hifivesoccer/origin/profile/" + fileName;
                        //App.getInstance().bus().send(new EditorEvent.ActionButtonClickEvent(R.id.action_insert_image, url, mView.getEditorType()));
                        observer.cleanTransferListener();
                        updateAvatar(fileName);
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                }

                @Override
                public void onError(int id, Exception ex) {
                    //Log.i("hong", String.format("[onError] id : %d, error : %s", id, ex.getMessage()));
                    observer.cleanTransferListener();
                    mView.hideLoading();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            //Log.i("hong", "[uploadS3]" + e.getMessage());
        }
    }

    private String getImageFileName(String userName) {

        return userName + "_" + SystemClock.elapsedRealtime() + ".jpg";
    }

    /*---------------------------------------------------------------------------------------------*/

    public void updateAvatar(String avatarUrl) {

        String token;

        if ( App.getAccountManager().isAuthorized()) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {
            // TODO: 2017. 11. 25. 아래와 같이 getWindow().getDecorView로 스낵바를 호출하면 광고와 오버랩된다. coordi~layout를 활용해야 한다.(MainActivity 참고)
            Snackbar.make(mView.getWindow().getDecorView(), mView.getResources().getString(R.string.is_not_authorized_info), Snackbar.LENGTH_LONG)
                    //.setAction("이동", v -> openLoginActivity())
                    .show();

            return;
        }

        Flowable<DBResultResponse> observable
                = mUserManager.updateAvatar(token, avatarUrl);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(dbResultResponse -> {
                            //Timber.d("[dbResultResponse] onNext");
                            App.getAccountManager().setUserAvatar(avatarUrl);
                            App.getInstance().bus().send(new UserAccountEvent.SignIn());
                            mView.hideLoading();
                            mView.finish();
                        }, e -> {
                            App.getInstance().bus().send(new UserAccountEvent.SignIn());
                            Toasty.error(mView.getApplicationContext(), "사용자 정보를 가져오는중 오류가 발생했습니다.\r\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                            mView.hideLoading();
                            mView.finish();
                        })
        );
    }

    public void socialSignup3(String username,
                              String nickname,
                              String profile,
                              String avatar_url) {

        //Timber.i("[socialSignup3] 시작");

        AccountUser accountUser = App.getAccountManager().getAccountUser();
        Flowable<CheckUserNameModel> checkUserNameobservable
                = mAuthManager.checkUserName(username);

        Flowable<DBResultResponse> signupObservable
                = mAuthManager.socialSignup(accountUser.getProvider(),
                                            accountUser.getProviderUserId(),
                                            //"bearer " + accountUser.getAccessToken(),
                                            accountUser.getSnsAccessToken(),
                                            username,
                                            nickname,
                                            profile,
                                            accountUser.getEmail(),
                                            avatar_url);

        checkUserNameobservable
                .concatMap(result -> {
                    if ( result != null && result.getExist() == 0) {
                        return signupObservable
                                .map(token -> {
                                    App.getAccountManager().setHifiveToken(token.getAuthToken());
                                    //Timber.i("[socialSignup2-0]" + token.toString());
                                    return token;
                                })
                                .concatMap(hifiveToken -> {
                                    //Timber.i("[socialSignup2-1]" + hifiveToken.toString());
                                    return hifiveUserObservable(hifiveToken.getAccessToken());
                                })
                                .concatMap(userModel -> {
                                    if ( userModel != null) {
                                        App.getAccountManager().registerHifiveUser(userModel, false);
                                        return Flowable.just("success");
                                    } else {
                                        return Flowable.just("사용자 정보를 불러오지 못했습니다.");
                                    }
                                });
                    } else {
                        return Flowable.just("이미 등록된 유저명입니다.");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        //Timber.d("[socialSignup3] %s", s);

                        if ( s.equals("success")) {
                            if ( mView.avatarUri != null) {
                                // 유저등록 이벤트는 이미지 업로드 완료 후 보낸다.
                                imageCompressNUpload(mView.avatarUri);
                            } else {
                                // 프로필 설정을 하지 않았다면 유저등록 이벤트 전송과 함께 종료
                                mView.hideLoading();
                                App.getInstance().bus().send(new UserAccountEvent.SignIn());
                                mView.finish();
                            }
                        } else {
                            Toasty.info(mView, s, Toast.LENGTH_LONG).show();
                            mView.hideLoading();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        mView.showErrorMessage("[socialSignup3]" + t.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });


//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new DisposableSubscriber<UserModel>() {
//                    @Override
//                    public void onNext(UserModel userModel) {
//                        Timber.d("[socialSignup2] onNext, userModel : %s", userModel.toString());
//
//                        if ( mView.avatarUri != null) {
//                            // 유저등록 이벤트는 이미지 업로드 완료 후 보낸다.
//                            App.getAccountManager().registerHifiveUser(userModel, false);
//                            imageCompressNUpload(mView.avatarUri);
//                        } else {
//                            // 프로필 설정을 하지 않았다면 유저등록 이벤트 전송과 함께 종료
//                            App.getAccountManager().registerHifiveUser(userModel, true);
//                            mView.hideLoading();
//                            mView.finish();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable t) {
//                        Timber.d("[socialSignup2] error : %s", t.getMessage());
//                        App.getInstance().bus().send(new UserAccountEvent.SignError("회원가입에 실패했습니다.\r\n" + t.getMessage()));
//                        mView.finish();
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Timber.d("[socialSignup2] onComplete");
//                    }
//                });
    }


        private Flowable<UserModel> hifiveUserObservable(String token) {

        //Timber.d("[hifiveUserObservable] hifive token : %s", token);

        Flowable<UserModel> observable
                = mUserManager.getLoginUser(token);
                //= mUserManager.getLoginUser("bearer " + token);

        return observable;
    }

//    public void socialSignup(SnsInfoModel provider,
//                             String username,
//                             String nickname,
//                             String avatar_url) {
//
//        Flowable<AuthToken> observable
//                = mAuthManager.socialSignup(provider.getProvider(), provider.getProviderUserId(), "bearer " + provider.getAccessToken(), username, nickname, provider.getEmail(), avatar_url);
//
//        mComposite.add(
//                observable.observeOn(AndroidSchedulers.mainThread())
//                        .subscribe( authToken -> {
//                            Log.i("hong", "socialSignup onNext");
//                            App.getInstance().setAuthToken(authToken);
//                            App.getInstance().setSnsInfo(provider);
//                            getLoginUser();
//                            //mView.finishActivity(true);
//                            //1. 서버에서 받은 토큰 저장
//                            //2. App에 SnsInfoModel 데이터 저장
//                        },
//                        e -> {
//                            Log.i("hong", "socialSignup error : " + e.getMessage());
//                            mView.finishActivity(false);
//                        },
//                        () -> Log.i("hong", "socialSignup complete")));
//
//    }

    public void getLoginUser() {

        String token;

        if (App.getAccountManager().isAuthorized()) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {
            Snackbar.make(mView.getWindow().getDecorView(), mView.getResources().getString(R.string.is_not_authorized_info), Snackbar.LENGTH_LONG)
                    //.setAction("이동", v -> openLoginActivity())
                    .show();

            return;
        }

        Flowable<UserModel> observable
                = mUserManager.getLoginUser(token);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                item -> {
                                    Toasty.success(mView.getApplicationContext(), mView.getResources().getString(R.string.complete_load_user_info), Toast.LENGTH_LONG, true).show();
                                    //App.getInstance().setUser(item);
                                    App.getAccountManager().registerHifiveUser(item);
                                    App.getInstance().bus().send(new LoginEvent.LoginStatusEvent(true, mView.getClass().getSimpleName()));
                                    mView.finishActivity(true);
                                },
                                e -> {
                                    mView.showErrorMessage(mView.getResources().getString(R.string.fail_load_user_info) + e.getMessage());
                                    mView.finishActivity(true);
                                },
                                () -> { }
                        ));
    }

    /**
     * 기존에 존재하는 이메일인지 체크
     * @param email
     */
    public void checkEmail(String email) {

        Flowable<CheckEmailModel> observable
                = mAuthManager.checkEmail(email);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                item -> {
                                    if ( item.getExist() == 0 ) {
                                        mView.setCheckEmailResult(true);
                                    } else {
                                        mView.showErrorMessage("이미 존재하는 이메일 또는 올바르지 않는 이메일입니다.");
                                    }
                                },
                                e -> mView.showErrorMessage(e.getMessage()),
                                () -> {}
                        ));
    }

    /**
     * 기존에 존재하는 유저명인지 체크
     * @param userName
     */
    public void checkUserName(String userName) {

        if ( userName.length() < 4 ) {
            mView.showMessage("최소 4자 이상 입력해주세요");
            return;
        }

        // TODO: 2018. 3. 13. 함수로 분리하자, 텍스트 와쳐에서도 같은 로직을 사용함.
        final Pattern sPattern
                = Pattern.compile("^[a-zA-Z0-9]+$");

        boolean isValid = sPattern.matcher(userName).matches();

        if ( !isValid) {
            mView.showMessage("영문 또는 숫자만 입력해주세요");
            return;
        }

        Flowable<CheckUserNameModel> observable
                = mAuthManager.checkUserName(userName);

        mComposite.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                item -> {
                                    if ( item.getExist() == 0 ) {
                                        mView.setCheckUserNameResult(true);
                                    } else {
                                        mView.showErrorMessage("이미 존재하는 사용자 이름 또는 올바르지 않는 사용자 이름입니다.");
                                    }
                                },
                                e -> mView.showErrorMessage("이미 존재하는 사용자 이름 또는 올바르지 않는 사용자 이름입니다.\n" + e.getMessage()),
                                () -> {}
                        ));
    }
}

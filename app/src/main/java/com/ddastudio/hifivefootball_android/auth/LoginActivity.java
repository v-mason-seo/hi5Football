package com.ddastudio.hifivefootball_android.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.event.UserAccountEvent;
import com.ddastudio.hifivefootball_android.ui.base.BaseActivity;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.AuthService;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnLongClick;
import es.dmoral.toasty.Toasty;

/**
 * A login screen that offers login via email/password.
 * 카카오 릴리즈 해시키 얻기
 *  1. 구글 사이닝키를 이용하지 않는다면
 *     keytool -exportcert -alias ddastudiokey -keystore /Users/hongmac/AndroidStudioProjects/AndroidKey/ddastudio_android.jks | openssl sha1 -binary | openssl base64
 *     ddastudiokey : 앱 릴리즈시 키 별칭  -->  keytool -list -keystore /Users/imcreator/MY_KEY.jks 외와같이 별칭을 조회할 수 있다.
 *     /Users/hongmac/AndroidStudioProjects/AndroidKey/ddastudio_android.jks : 키자 존재하는 경로와 파일명
 *     => keytool -exportcert -alias androiddebugkey -keystore C:\Users\admin.android\debug.keystore -storepass android -keypass android
 *
 *  2. 구글 사이닝키를 이용한다면
 *     구글콘솔 접속 -> 출시관리 -> 앱서명 -> 앱 서명 인증서.SHA-1 인증서 지문 복사 ( SHA1: 이부분은 빼고 )
 *     다음으로 터미널에서 아래 명령어를 입력하여 해시키 값을 얻어온다.
 *     => echo 51:3E:57:51:59:0F:A1:28:70:2E:A9:4A:7E:1E:F7:FD:FC:43:AC:DA | xxd -r -p | openssl base64
 *
 *  디버그키와 릴리지키 각각 존재하기 때문에 두 개 모두 등록해줘야 한다.
 */
public class LoginActivity extends BaseActivity {

    @BindView(R.id.main_toolbar) Toolbar mToolbar;
    @BindView(R.id.login_progress) ProgressBar progressBar;
    @BindView(R.id.naver_signin) com.nhn.android.naverlogin.ui.view.OAuthLoginButton mNaverButton;
    @BindView(R.id.kakao_signin) com.kakao.usermgmt.LoginButton mKakaoButton;
    @BindString(R.string.naver_client_name) String mNaverClientName;

    LoginPresenter mPresenter;
    LoginActivity myInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_login)
                .setSwipeBackView(R.layout.swipeback);
        ButterKnife.bind(this);

        mPresenter = new LoginPresenter();
        mPresenter.attachView(this);
        myInstance = this;

        initToolbar();
        initNaver();
        initKakaoLogin();
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        //Session.getCurrentSession().removeCallback(callback);
        super.onDestroy();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.swipeback_stack_to_front,
                R.anim.swipeback_stack_right_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Timber.d("[onActivityResult]-0 reauestCode : %d, resultCode : %d, data : %s", requestCode, resultCode, data.toString());
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            //Timber.d("[onActivityResult]-1 reauestCode : %d, resultCode : %d, data : %s", requestCode, resultCode, data.toString());
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    /*───-----------------------------------────────────────────────────────*/

    @Override
    protected void onEvent(Object event) {
        super.onEvent(event);

        if (event instanceof UserAccountEvent.SignIn) {
            //Timber.d("UserAccountEvent.SignIn");
            // 로그인이 완료되면 액비비티를 종료한다.
            finish();
        }
    }

    @Override
    public void showLoading() {
        super.showLoading();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void showErrorMessage(String message) {
        Toasty.error(getApplicationContext(), message, Toast.LENGTH_LONG, true).show();
    }

    @Override
    public void showMessage(String message) {
        super.showMessage(message);
        Toasty.normal(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        //   /Users/hongmac/AndroidStudioProjects/AndroidKey/ddastudio_android.jks

        /*
        *      keytool -exportcert -alias <release_key_alias> -keystore <release_keystore_path> | openssl sha1 -binary | openssl base64
        *      keytool -exportcert -alias ddastudiokey -keystore /Users/hongmac/AndroidStudioProjects/AndroidKey/ddastudio_android.jks | openssl sha1 -binary | openssl base64
        *
        *      KYU8OU7k6NFYcRMjHTl6sLfYdZc=
        *      9SILG5YJlBE1qC4s7M9Y0Ffn57o=
        *
        *      /Users/hongmac/AndroidStudioProjects/AndroidKey/ddastudio_android.jks
        * */
    }

    /*───-----------------------------------────────────────────────────────*/


    /**
     * 툴바 초기화
     */
    private void initToolbar() {

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("");
    }

    /*------------------------------------------------------*/

    // *** Naver ***


    private void initNaver() {
//        mPresenter.initNaverOAuth(mNaverClientId, mNaverClientSecret, mNaverClientName);
        mPresenter.initNaverOAuth(mNaverClientName);
    }

    public void addNaverHandler(OAuthLoginHandler oAuthLoginHandler) {
        mNaverButton.setOAuthLoginHandler(oAuthLoginHandler);
    }

    /*------------------------------------------------------*/

    // *** Google ****

    private void initGoogleLogin() {
    }


    /*------------------------------------------------------*/

    // *** Kakao ***

    private void initKakaoLogin() {
        /*callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        //Session.getCurrentSession().checkAndImplicitOpen();*/
        mPresenter.initKaKaoOAuth();
    }

    private void onKakaoSignup() {

        UserManagement.requestSignup(new ApiResponseCallback<Long>() {
            @Override
            public void onNotSignedUp() {
            }

            @Override
            public void onSuccess(Long result) {
                //Timber.i("[onKakaoSignup] onSuccess");
                //Timber.i("[onKakaoSignup] kakao access token : %s", Session.getCurrentSession().getTokenInfo().getAccessToken());
                //Timber.i("[onKakaoSignup] kakao refresh token : %s", Session.getCurrentSession().getTokenInfo().getRefreshToken());
                requestMe();
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                final String message = "UsermgmtResponseCallback : failure : " + errorResult;
                com.kakao.util.helper.log.Logger.w(message);
                //KakaoToast.makeToast(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                //Timber.i("[onKakaoSignup] message : %s", message);
                //Timber.i("[onKakaoSignup] -onFailure kakao access token : %s", Session.getCurrentSession().getTokenInfo().getAccessToken());
                //Timber.i("[onKakaoSignup] -onFailure kakao refresh token : %s", Session.getCurrentSession().getTokenInfo().getRefreshToken());
                //finish();
                requestMe();
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
            }
        }, null);
    }

    /**
     * 사용자의 상태를 알아 보기 위해 me API 호출을 한다.
     */
    protected void requestMe() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    //KakaoToast.makeToast(getApplicationContext(), getString(R.string.error_message_for_service_unavailable), Toast.LENGTH_SHORT).show();
                    //Timber.i("[requestMe] error message : %s", "error_message_for_service_unavailable");
                    finish();
                } else {
                    //redirectLoginActivity();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                //Timber.d("[requestMe] onSessionClosed");
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                //Logger.i("UserProfile : " + userProfile);
                //Timber.i("[requestMe] onSuccess, user profile : " + userProfile);
                //Timber.i("[requestMe] kakao access token : %s", Session.getCurrentSession().getTokenInfo().getAccessToken());
                //Timber.i("[requestMe] kakao refresh token : %s", Session.getCurrentSession().getTokenInfo().getRefreshToken());
                //redirectMainActivity();
                requestAccessTokenInfo();
            }

            @Override
            public void onNotSignedUp() {
                //Timber.d("[requestMe] onNotSignedUp");
                //showSignup();
            }
        });
    }

    private void requestAccessTokenInfo() {

        AuthService.requestAccessTokenInfo(new ApiResponseCallback<AccessTokenInfoResponse>() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                //redirectLoginActivity(self);
                //Timber.d("[requestAccessTokenInfo] onSessionClosed message : %s", errorResult.getErrorMessage());
            }

            @Override
            public void onNotSignedUp() {
                // not happened
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                //Timber.d("[requestAccessTokenInfo] onFailure message : %s", errorResult.getErrorMessage());
            }

            @Override
            public void onSuccess(AccessTokenInfoResponse accessTokenInfoResponse) {

                long userId = accessTokenInfoResponse.getUserId();
                //Timber.i("[requestAccessTokenInfo] onSuccess this access token is for userId=" + userId);

                long expiresInMilis = accessTokenInfoResponse.getExpiresInMillis();
                //Timber.i("[requestAccessTokenInfo] this access token expires after " + expiresInMilis + " milliseconds.");
                //Timber.i("[requestAccessTokenInfo] kakao access token : %s", Session.getCurrentSession().getTokenInfo().getAccessToken());
                //Timber.i("[requestAccessTokenInfo] kakao refresh token : %s", Session.getCurrentSession().getTokenInfo().getRefreshToken());
            }
        });
    }


    /*------------------------------------------------------*/

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            //Timber.i("[onSessionOpened]");
            //Timber.i("[onSessionOpened] kakao access token : %s", Session.getCurrentSession().getTokenInfo().getAccessToken());
            //Timber.i("[onSessionOpened] kakao refresh token : %s", Session.getCurrentSession().getTokenInfo().getRefreshToken());

            //kakao access token : fyo0ye895e8QgxBxmDHcfVYziI5boLhiJ44WmQopdkgAAAFf-MG5Eg
            //kakao refresh token : qI4S7m90gj_1AiI_Xvad_ieUQZ1_aDhMtTt3bgoqAuYAAAFf2U4IqA
            onKakaoSignup();
            //redirectSignupActivity();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                //Timber.d(exception);
            }
        }
    }

    protected void redirectSignupActivity() {
        final Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    @OnLongClick(R.id.v_empty)
    public boolean onEmptyLongClick(View view) {

        showMessage("AAAA");

        MaterialDialog dialog =
                new MaterialDialog.Builder(this)
                        .title(R.string.dialog_edit_profile_title)
                        .customView(R.layout.dialog_custom_email_password_item, true)
                        .positiveText(R.string.positive)
                        .negativeText(R.string.negative)
                        .cancelListener(
                                (dialog1) -> {
                                    showMessage("onCancel");
                                }
                        )
                        .onNegative(
                                (dialog1, which) -> {
                                    showMessage("onNegative");
                                }
                        )
                        .onPositive(
                                (dialog1, which) -> {
                                    showMessage("onPositive");
                                })
                        .build();

        EditText etEmail = dialog.getCustomView().findViewById(R.id.et_email);
        EditText etPassword = dialog.getCustomView().findViewById(R.id.et_password);

        dialog.show();

        return true;
    }
}


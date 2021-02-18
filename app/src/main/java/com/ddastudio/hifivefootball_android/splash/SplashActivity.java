package com.ddastudio.hifivefootball_android.splash;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.ui.base.BaseActivity;

import butterknife.BindString;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class SplashActivity extends BaseActivity {

    @BindString(R.string.naver_client_name) String mNaverClientName;
    //@BindString(R.string.naver_client_id) String mNaverClientId;
    //@BindString(R.string.naver_client_secret) String mNaverClientSecret;

    SplashPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        mPresenter = new SplashPresenter();
        mPresenter.attachView(this);

        //mPresenter.getBoardList(true);
        mPresenter.onLoadHifiveSettings();




        // 쇼설로그인을 하기 위한 기본 정보를 로드한다.
        //App.getSignInManager().loadSecret();

        // TODO: 2017. 11. 16. 로그인 테스트를 위해 임시로 주석처리함
//        if ( App.getInstance().isAutoLogin()) {
//            Log.i("hong", "main login savedInstanceState is null : " + savedInstanceState == null ? "null" : "not null");
//            mPresenter.initNaverOAuth(mNaverClientId, mNaverClientSecret, mNaverClientName);
//            mPresenter.executeAutoLogin();
//        } else {
//            mPresenter.openMainActivity();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( requestCode == SplashPresenter.LOGIN_REQUEST) {
//          if ( resultCode == RESULT_OK ) {
//
//          }
            mPresenter.openMainActivity();
            finish();
        }
        //super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void showErrorMessage(String message) {
        Toasty.error(this, message, Toast.LENGTH_LONG, true).show();
    }

    public void onLoadFinished(String errMessage) {

        if ( !TextUtils.isEmpty(errMessage)) {
            showErrorMessage("기본정보를 가져오는 중 오류가 발생했습니다.\n" + errMessage);
        }

        boolean isMember = App.getAccountManager().isHifiveMember();
        if ( isMember ) {
            mPresenter.refreshHifiveAccessToken();
            //mPresenter.openMainActivity();
        } else {
            mPresenter.openMainActivity();
        }
    }

}

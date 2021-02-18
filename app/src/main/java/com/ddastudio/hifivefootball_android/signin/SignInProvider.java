package com.ddastudio.hifivefootball_android.signin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * OAuth 로그인 기능을 제공하는 provider의 뼈대 객체.
 *
 * Created by hongmac on 2017. 11. 16..
 */

abstract public class SignInProvider<T> implements Constants {

    protected Context context;

    protected SignInProvider(Context context) {
        this.context = context;
    }

    /**
     * 로그인 요청. SignInManager에서 호출함.
     */
    abstract public void requestSignIn(Activity activity, Secret secret);

    /**
     * 로그인 완료. 마지막에 UserManager.registerUser() 호출해서 회원정보에 저장해주어야 함.
     */
    abstract protected void onSignInComplete(T result);

    /**
     * 연동해제 요청
     * @param activity
     * @param secret
     */
    abstract public void requestSignOut(Activity activity, Secret secret);

    /**
     * 연동해제 완료 - 소셜로그인 현동해제 및 하이파이브 풋볼 회원 탈퇴
     * @param result
     */
    abstract protected void onSignOutComplete(T result);



    /**
     * BaseActivity.onActivityResult()로부터 delegate.
     * 구글/페이스북 로그인에서 이용함.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
}

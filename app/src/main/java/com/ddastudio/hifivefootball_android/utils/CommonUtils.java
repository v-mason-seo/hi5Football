package com.ddastudio.hifivefootball_android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.ddastudio.hifivefootball_android.App;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import static android.os.Build.VERSION.SDK_INT;
import static com.kakao.util.helper.Utility.getPackageInfo;
import static eu.davidea.flexibleadapter.utils.FlexibleUtils.hasMarshmallow;
import static eu.davidea.flexibleadapter.utils.FlexibleUtils.hasNougat;

/**
 * Created by hongmac on 2017. 9. 5..
 */

public class CommonUtils {

    /**
     * 해쉬키 얻기
     * 카카오 로그인 때문에 추가함 하지만 사용은 안한다. 터미널에서 해쉬값을 얻을 수 있음
     * LoginActivity 주석을 참고할것.
     * @param context
     * @return
     */
    public static String getKeyHash(final Context context) {
        PackageInfo packageInfo = getPackageInfo(context, PackageManager.GET_SIGNATURES);
        if (packageInfo == null)
            return null;

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
                //Log.w("hong", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
        return null;
    }

    static Random random = new Random();

    public static int randomBetween(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    public static String getDeviceId(Context context) {

        String deviceID = android.provider.Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return deviceID;
    }

    /*public static Drawable getDrawable(Context context, int resid) {

        return ContextCompat.getDrawable(context, resid);
//        final int version = SDK_INT;
//        if ( version >= Build.VERSION_CODES.LOLLIPOP) {
//            return context.getDrawable(resid);
//        } else {
//            return context.getResources().getDrawable(resid);
//        }
    }*/

    public static int getColor(Context context, int resid) {

        final int version = SDK_INT;
        if ( version >= Build.VERSION_CODES.M) {
            return context.getColor(resid);
        } else {
            return context.getResources().getColor(resid);
        }
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtmlCompat(String text) {

        if (hasNougat()) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(text);
        }
    }

    @SuppressWarnings("deprecation")
    public static void textAppearanceCompat(TextView textView, int resId) {
        if (hasMarshmallow()) {
            textView.setTextAppearance(resId);
        } else {
            textView.setTextAppearance(textView.getContext(), resId);
        }
    }

    public static int dpToPx(@NonNull final Context context, final float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    public static Context getContext() {
        return App.getInstance().getApplicationContext();
    }

    public static int dp(int dp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext()
                .getResources()
                .getDisplayMetrics()) + 0.5F);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static void hideKeyboard(Activity activity, View view) {
        // 키보드 내리기
        View keyboardView = activity.getCurrentFocus();
        if (keyboardView != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

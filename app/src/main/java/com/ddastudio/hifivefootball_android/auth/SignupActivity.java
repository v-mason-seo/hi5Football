package com.ddastudio.hifivefootball_android.auth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.event.SelectedItemEvent;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.signin.AccountUser;
import com.ddastudio.hifivefootball_android.ui.base.BaseActivity;
import com.ddastudio.hifivefootball_android.ui.fragment.LeagueTableDialogFragment;
import com.ddastudio.hifivefootball_android.utils.FileUtil;
import com.google.android.flexbox.FlexboxLayout;
import com.zhihu.matisse.Matisse;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

// TODO: 2017. 11. 23. 개선1. user_login.deleted (1) 이 되어있어도 회원가입이 된다
// TODO: 2017. 11. 23. 개선2. 로그인시 user_login.refresh_token 값이 저장된다. 하지만 signup 실행시에는 토큰 값이 저장이 안된다. 

public class SignupActivity extends BaseActivity     {

    public static final int REQUEST_CODE_PROFILE_CHOOSE = 123;

    @BindView(R.id.signup_toolbar) Toolbar mToolbar;
    @BindView(R.id.avatar) ImageView ivAvatar;
    @BindView(R.id.user_email) EditText etUserEmail;
    @BindView(R.id.check_username_button) Button mCheckUserNameButton;
    @BindView(R.id.user_name) TextInputEditText etUserName;
    @BindView(R.id.nick_name) TextInputEditText etNickName;
    @BindView(R.id.tv_profile) TextInputEditText etProfile;
    @BindView(R.id.fbx_team_tag) FlexboxLayout fbxTeam;
    @BindView(R.id.fbx_player_tag) FlexboxLayout fbxPlayer;
    @BindView(R.id.signup_button) Button btnSignup;
    @BindView(R.id.username_text_input_layout) TextInputLayout userNameLabel;
    @BindView(R.id.email_text_input_layout) TextInputLayout emailLabel;
    @BindView(R.id.signup_loading) LottieAnimationView loadingView;
    @BindView(R.id.check_terms_contditions) CheckBox checkTermsConditions;
    @BindView(R.id.show_terms_conditions) Button buttonTermsConditions;
    @BindString(R.string.activity_title_signup) String mTitle;

    boolean checkUserNameResult = false;
    boolean checkEmailResult = true; // 이메일 체크는 현재 사용하지 않음.
    public Uri avatarUri;
    String mAvatarUrl;
    SignupPresenter mPresenter;
    ChipCloud mTeamChipCloud;
    ChipCloud mPlayerChipCloud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        getWindow().setBackgroundDrawableResource(R.drawable.background_6);

        mPresenter = new SignupPresenter();
        mPresenter.attachView(this);

        initToolbar();
        initTextInputLayout();
        initFlexboxLayout();
        initData();
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

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PROFILE_CHOOSE && resultCode == RESULT_OK) {

            // 이미지 선택 -> 이미지 압축 -> 이미지 S3 업로드
            List<Uri> obtainResults = Matisse.obtainResult(data);
            avatarUri = obtainResults.get(0);

            GlideApp.with(getApplicationContext())
                    .load(obtainResults.get(0))
                    .centerCrop()
                    .transform(new CropCircleTransformation())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_face)
                    .into(ivAvatar);
        }
    }

    @OnClick({R.id.avatar, R.id.signup_button, R.id.check_username_button, R.id.check_email_button, R.id.show_terms_conditions})
    public void onClickButton(View view) {

        switch (view.getId()) {
            case R.id.show_terms_conditions:
                mPresenter.openTermsConditionsActivity();
                break;
            case R.id.avatar:
                mPresenter.openLocalImagePicker();
                break;
            case R.id.check_username_button:
                mPresenter.checkUserName(etUserName.getText().toString());
                break;
            case R.id.check_email_button:
                mPresenter.checkEmail(etUserEmail.getText().toString());
                break;
            case R.id.signup_button:

                //Timber.d("[signup_button] ----------------------------------");
                if ( checkData() ) {

                    showLoading();
                    // TODO: 2018. 3. 13. 프로필 입력 텍스트 에이트 뷰 추가해서 서버도 같이 수정하자.
                    mPresenter.socialSignup3(etUserName.getText().toString(),
                            etNickName.getText().toString(),
                            etProfile.getText().toString(),
                            mAvatarUrl);
                }

                break;
        }
    }

    @Override
    protected void onEvent(Object event) {
        super.onEvent(event);

        if (event instanceof SelectedItemEvent.TeamItem) {

            if ( mTeamChipCloud != null ) {
                List<TeamModel> items = ((SelectedItemEvent.TeamItem) event).getItems();
                for( int i=0; i < items.size(); i++) {
                    mTeamChipCloud.addChip(items.get(i));
                }
            }

        } else if ( event instanceof SelectedItemEvent.PlayerItem ) {

            if ( mPlayerChipCloud != null ) {
                List<PlayerModel> items = ((SelectedItemEvent.PlayerItem) event).getItems();
                for( int i=0; i < items.size(); i++) {
                    mPlayerChipCloud.addChip(items.get(i));
                }
            }
        }
    }

    @Override
    public void showMessage(String message) {
        super.showMessage(message);
        Toasty.normal(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showErrorMessage(String errMessage) {
        super.showErrorMessage(errMessage);
        Toasty.error(getApplicationContext(), errMessage, Toast.LENGTH_LONG, true).show();
    }

    /*------------------------------------------------------*/

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void initData() {

        checkUserNameResult = false;
        checkEmailResult = false;

        AccountUser accountUser = App.getAccountManager().getAccountUser();

        if ( accountUser != null ) {

            /**
             * *** 사용자 프로필 ***
             * 1. 소셜 프로필을 다운로드 받는다.
             * 2. avatarUri값을 설정한다. ( 프로필 이미지를 따로 설정하지 않으면 소셜 프로필을 올린다.)
             * 3. 이미지뷰에 파일을 바인딩 한다.
             */
            GlideApp.with(getApplicationContext()).load(accountUser.getAvatar())
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            Bitmap bitMap = drawableToBitmap(resource);
                            File file = FileUtil.saveBitmapToJpeg(getApplicationContext(), bitMap, "temp_profile_" + SystemClock.elapsedRealtime());
                            avatarUri = Uri.fromFile(file);
                            GlideApp.with(SignupActivity.this)
                                    .load(file)
                                    .centerCrop()
                                    .transform(new CropCircleTransformation())
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .placeholder(ContextCompat.getDrawable(SignupActivity.this, R.drawable.ic_face))
                                    .into(ivAvatar);
                        }
                    });

            etUserEmail.setText(accountUser.getEmail());
            etUserName.setText(accountUser.getNickName());
            etNickName.setText(accountUser.getNickName());
            mAvatarUrl = accountUser.getAvatar();
        }
    }

    /**
     * 툴바 초기화
     */
    private void initToolbar() {

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("");
    }

    public InputFilter filterAlphaNumber = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    private void initTextInputLayout() {

        userNameLabel.setErrorEnabled(true);
        userNameLabel.setError("영문 또는 숫자만 가능합니다.");

        etUserEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if ( !Patterns.EMAIL_ADDRESS.matcher("").matches()) {
                    //emailLabel.setError("이메일 형식이 올바르지 않습니다.");
                    //etUserEmail.setEnabled(true);
                } else {
                    //emailLabel.setError(null);
                }
            }
        });


        //etUserName.setFilters(new InputFilter[]{filterAlphaNumber});
        etUserName.addTextChangedListener(new TextWatcher() {

            private final Pattern sPattern
                    = Pattern.compile("^[a-zA-Z0-9]+$");

            private boolean isValid(CharSequence s) {
                return sPattern.matcher(s).matches();
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!isValid(editable))
                {
                    userNameLabel.setError("영어 또는 숫자만 입력 가능합니다.");
                } else {
                    userNameLabel.setError(null);
                }
            }
        });
    }

    private void initFlexboxLayout() {

        ChipCloudConfig drawableWithCloseConfig = new ChipCloudConfig()
                .selectMode(ChipCloud.SelectMode.multi)
                .checkedChipColor(Color.parseColor("#ddaa00"))
                .checkedTextColor(Color.parseColor("#ffffff"))
                .uncheckedChipColor(Color.parseColor("#e0e0e0"))
                .uncheckedTextColor(Color.parseColor("#000000"))
                .showClose(Color.parseColor("#a6a6a6"), 500);

        mTeamChipCloud = new ChipCloud(this, fbxTeam, drawableWithCloseConfig);
        mPlayerChipCloud = new ChipCloud(this, fbxPlayer, drawableWithCloseConfig);

        fbxTeam.setOnClickListener(v -> {
            LeagueTableDialogFragment dialogFragment
                    = LeagueTableDialogFragment.newInstance(MultipleItem.BOTTOM_TEAM_LIST);
            dialogFragment.show(getSupportFragmentManager(), "BottomTeamList");
        });

        fbxPlayer.setOnClickListener(v -> {
            LeagueTableDialogFragment dialogFragment
                    = LeagueTableDialogFragment.newInstance(MultipleItem.BOTTOM_PLAYER_LIST);
            dialogFragment.show(getSupportFragmentManager(), "BottomPlayerList");
        });
    }

    /**
     * 회원가입 전 입력된 데이터 확인작업
     * @return
     */
    private boolean checkData() {

        if ( etUserName.getText().toString().length() < 4 ) {
            showMessage("최소 4자 이상 입력해주세요");
            return false;
        }

        if ( userNameLabel.getError() != null ) {
            Toasty.normal(getApplicationContext(), "유저명을 올바르게 입력해주세요", Toast.LENGTH_LONG).show();
            return false;
        }

        if ( !checkTermsConditions.isChecked()) {
            Toasty.normal(getApplicationContext(), "이용약관에 동의를 해주세요", Toast.LENGTH_LONG).show();
            return false;
        }

//        if ( checkUserNameResult == false ) {
//            Toasty.normal(getApplicationContext(), "이미 존재하는 사용자 이름 또는 올바르지 않는 사용자 이름입니다.", Toast.LENGTH_LONG).show();
//            return false;
//        }
//
//        if ( checkEmailResult == false ) {
//            Toasty.normal(getApplicationContext(), "이미 존재하는 이메일 또는 올바르지 않는 이메일입니다.", Toast.LENGTH_LONG).show();
//            return false;
//        }

        return true;
    }

    /*------------------------------------------------------*/

    public void finishActivity(boolean isLogin) {

        Intent returnIntent = new Intent();
        //returnIntent.putExtra("ARGS_RETURN_IS_LOGIN", isLogin);
        setResult(isLogin == true ? Activity.RESULT_OK : Activity.RESULT_CANCELED);
        finish();
    }

    public void setCheckUserNameResult(boolean checkUserNameResult) {
        this.checkUserNameResult = checkUserNameResult;
        Toasty.info(getApplicationContext(), "사용 가능한 유저명입니다", Toast.LENGTH_SHORT).show();
    }

    public void setCheckEmailResult(boolean checkEmailResult) {
        this.checkEmailResult = checkEmailResult;
    }

    /**
     * 로딩 보이기
     */
    public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
        loadingView.playAnimation();
    }

    /**
     * 로딩 감추기
     */
    public void hideLoading() {
        loadingView.setVisibility(View.INVISIBLE);
        loadingView.cancelAnimation();
    }

    public void updateAvatar(String avaraUrl) {
        GlideApp.with(this)
                .load(avaraUrl)
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_person)
                .into(ivAvatar);
    }
}

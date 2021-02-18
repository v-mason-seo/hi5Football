package com.ddastudio.hifivefootball_android.user_profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.ui.base.BaseActivity;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.zhihu.matisse.Matisse;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class UserProfileActivity extends BaseActivity {

    public static final int REQUEST_CODE_PROFILE_CHOOSE = 123;

    @BindView(R.id.user_profile_tabs) SmartTabLayout mTabLayout;
    @BindView(R.id.toolbar)  Toolbar mToolbar;
    @BindView(R.id.avatar) ImageView avatar;
    @BindView(R.id.tv_user_name) TextView tvUserName;
    @BindView(R.id.tv_user_profile) TextView tvUserProfile;
    @BindView(R.id.profile_loading) LottieAnimationView mLoading;
    @BindView(R.id.user_profile_viewpager) ViewPager mViewPager;
    @BindArray(R.array.user_profile_tabs_array) String[] mVpTitles;
    EditText etNickName;
    EditText etProfileText;
    ImageView ivDialogAvatar;
    TextView tvAvatarFileName;
    ProgressBar diaglogProgress;

    Menu mMenu;
    ActionBar mActionBar;
    UserProfilePresenter mPresenter;
    UserProfileVpAdapter mVpAdapter;

    private int mPagerPosition;
    private int mPagerOffsetPixels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_user_profile);
        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_user_profile)
                .setSwipeBackView(R.layout.swipeback)
                .setOnInterceptMoveEventListener((v, dx, x, y) -> {
                    if (v == mViewPager) {
                        return !(mPagerPosition == 0 && mPagerOffsetPixels == 0)
                                || dx < 0;
                    }
                    return false;
                });

        ButterKnife.bind(this);

        UserModel user = Parcels.unwrap(getIntent().getParcelableExtra("ARGS_USER"));
        mPresenter = new UserProfilePresenter(user);
        mPresenter.attachView(this);

        initializeToolbar();

        if ( App.getAccountManager().isSameUser(mPresenter.getUser().getUsername()) ) {
            user = App.getAccountManager().getHifiveUser();
            mPresenter.setUser(user);
            updateAvatar(user, true);
            initViewPager(user);
        } else {
            mPresenter.onLoadUser(user.getUsername());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PROFILE_CHOOSE && resultCode == RESULT_OK) {

            // 이미지 선택 -> 이미지 압축 -> 이미지 S3 업로드
            List<Uri> obtainResults = Matisse.obtainResult(data);
            mPresenter.imageCompressNUpload(obtainResults);
        } else {
            hideLoading();
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.swipeback_stack_to_front,
                R.anim.swipeback_stack_right_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    /**
     * OptionMenu가 최초로 생성될 때만 호출 된다.
     * 그 이후에는 OptionMenu가 화면에 나타날 경우
     * onPrepareOptionsMenu()만 호출 된다.
     * Activity가 onDestory()될 때 OptionMenu도 Destory 된다.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        this.mMenu = menu;

        return true;
    }

    /**
     * 스크린에 맞는 OptionMenu를 화면에 표시하기 위해 준비할 때 호출 된다.
     * 옵션메뉴가 화면에 나타날 때마다 호출 된다.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if ( App.getAccountManager().isSameUser(mPresenter.getUser().getUsername()) ) {
            // TODO: 2018. 3. 13. 현재는 수정 기능이 없다. 
            //menu.findItem(R.id.action_edit_profile).setVisible(true);
            menu.findItem(R.id.action_edit_profile).setVisible(true);
        } else {
            menu.findItem(R.id.action_edit_profile).setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_edit_profile:
                Toast.makeText(getApplicationContext(), "수정하기 클릭", Toast.LENGTH_SHORT).show();
                openEditProfileDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /*---------------------------------------------------------------------------------------------*/

    @Override
    protected void onEvent(Object event) {
        super.onEvent(event);
    }

    /**
     * 로딩 보이기
     */
    @Override
    public void showLoading() {
        mLoading.setVisibility(View.VISIBLE);
        mLoading.playAnimation();
    }

    /**
     * 로딩 감추기
     */
    @Override
    public void hideLoading() {
        mLoading.setVisibility(View.INVISIBLE);
        mLoading.cancelAnimation();
    }

    public void showDialogLoading() {
        if ( diaglogProgress != null ) {
            diaglogProgress.setVisibility(View.VISIBLE);
        }
    }

    public void hideDialogLoading() {
        if ( diaglogProgress != null ) {
            diaglogProgress.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void showMessage(String message) {
        super.showMessage(message);
    }

    public void showErrorMessage(String message) {
        Toasty.error(getApplicationContext(), message, Toast.LENGTH_LONG, true).show();
    }

    @OnClick(R.id.avatar)
    public void onAvatarClick() {

        if ( App.getAccountManager().isSameUser(mPresenter.getUser().getUsername()) ) {
            //mPresenter.openLocalImagePicker();
            openEditProfileDialog();
        } else {
            // TODO: 2017. 9. 25. 원본 이미지 보여주기
        }
    }

    private void openEditProfileDialog() {
        hideAd();

        MaterialDialog dialog =
                new MaterialDialog.Builder(this)
                        .title(R.string.dialog_edit_profile_title)
                        .customView(R.layout.dialog_custom_user_profile_item, true)
                        .positiveText(R.string.positive)
                        .negativeText(R.string.negative)
                        .cancelListener(
                                (dialog1) -> {
                                    showMessage("onCancel");
                                    showAd();
                                }
                        )
                        .onNegative(
                                (dialog1, which) -> {
                                    showMessage("onNegative");
                                    showAd();
                                }
                        )
                        .onPositive(
                                (dialog1, which) -> {
                                    //Timber.i("[Profile] nickName : %s, profileText : %s, avatarFileName : %s", etNickName.getText().toString(), etProfileText.getText().toString(), mPresenter.getUser().getAvatarFileName());
                                    mPresenter.updateUserInfo(etNickName.getText().toString(),
                                            etProfileText.getText().toString(),
                                            tvAvatarFileName.getText().toString());
                                    showMessage("onPositive");
                                    showAd();
                                })
                        .build();

        etNickName = dialog.getCustomView().findViewById(R.id.et_nickname);
        etProfileText = dialog.getCustomView().findViewById(R.id.et_profile_text);
        ivDialogAvatar = dialog.getCustomView().findViewById(R.id.iv_avatar);
        tvAvatarFileName = dialog.getCustomView().findViewById(R.id.tv_avatar_file_name);
        diaglogProgress = dialog.getCustomView().findViewById(R.id.dialog_progress);

        ivDialogAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( App.getAccountManager().isSameUser(mPresenter.getUser().getUsername()) ) {
                    mPresenter.openLocalImagePicker();
                }
            }
        });

        tvAvatarFileName.setText(mPresenter.getUser().getAvatarFileName());
        etNickName.setText(mPresenter.getUser().getNickname());
        etProfileText.setText(mPresenter.getUser().getProfile());

        GlideApp.with(getApplicationContext())
                .load(mPresenter.getUser().getAvatarUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_person_grey_vector)
                .into(ivDialogAvatar);

        dialog.show();
    }

    public void showAd() {

        //mAdView.setVisibility(View.VISIBLE);
    }

    public void hideAd() {
        //mAdView.setVisibility(View.GONE);
    }

    /*---------------------------------------------------------------------------------------------*/

    private void initializeToolbar() {

        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 뷰페이저 초기화
     * @param user
     */
    private void initViewPager(UserModel user) {

        mVpAdapter = new UserProfileVpAdapter(getSupportFragmentManager(), mVpTitles, user);
        mViewPager.setAdapter(mVpAdapter);
        mTabLayout.setViewPager(mViewPager);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mPagerPosition = position;
                mPagerOffsetPixels = positionOffsetPixels;
            }

        });
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadFinishedUserInfo(UserModel user) {
        updateAvatar(user, true);
        initViewPager(user);
    }

    public void setAvatarFileName(String fileName) {

        if ( tvAvatarFileName != null ) {
            tvAvatarFileName.setText(fileName);

            if ( ivDialogAvatar != null ) {

                String url = mPresenter.getUser().getAvatrOriginBaseUrl() + fileName;

                GlideApp.with(getApplicationContext())
                        .load(url)
                        .centerCrop()
                        .transform(new CropCircleTransformation())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_person_grey_vector)
                        .into(ivDialogAvatar);
            }
        }
    }

    /**
     *
     * @param user
     * @param isCdn 프로필 사진을 변경 후 바로 cdn 주소를 호출하면 캐쉬된 이미지 때문에 이미지가 나오지 않는다
     *              따라서 프로필 사진 업데이트 후에는 원본 이미지를 보여주고 그렇지 않을 경우는 cdn 주소를 호출한다.
     */
    public void updateAvatar(UserModel user, boolean isCdn) {

        tvUserName.setText(user.getNickNameAndUserName());

        if (TextUtils.isEmpty(user.getProfile())) {
            if ( App.getAccountManager().isSameUser(mPresenter.getUser().getUsername()) ) {
                tvUserProfile.setText("프로필을 등록해주세요...");
            } else {
                tvUserProfile.setText("프로필이 등록되어 있지 않습니다");
            }
        } else {
            tvUserProfile.setText(user.getProfile());
        }

        /*
        toolbarLayout.setTitle(user.getNickNameAndUserName());

        if (TextUtils.isEmpty(user.getProfile())) {
            if ( App.getAccountManager().isSameUser(mPresenter.getUser().getUsername()) ) {
                toolbarLayout.setSubtitle("프로필을 등록해주세요...");
            } else {
                toolbarLayout.setSubtitle("프로필이 등록되어 있지 않습니다");
            }
        } else {
            toolbarLayout.setSubtitle(user.getProfile());
        }
        */

        String avatarUrl;

        // TODO: 2018. 3. 29. 리사이즈 이미지 보여주는 건 다시 확인해보자 - 안되는경우가 많다 ㅠ
//        if ( isCdn ) {
//            avatarUrl = user.getAvatarMediumUrl();
//        } else {
//            avatarUrl = user.getAvatarOriginUrl();
//        }

        avatarUrl = user.getAvatarOriginUrl();

        GlideApp.with(this)
                .load(avatarUrl)
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_person)
                .into(avatar);
    }


}

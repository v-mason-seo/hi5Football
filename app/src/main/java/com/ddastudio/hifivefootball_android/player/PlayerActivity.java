package com.ddastudio.hifivefootball_android.player;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.ui.base.BaseActivity;
import com.ddastudio.hifivefootball_android.utils.CountryFlagUtils;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class PlayerActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.player_tabs) SmartTabLayout mTabLayout;
    @BindView(R.id.player_viewpager) ViewPager mViewPager;
    @BindView(R.id.player_avatar) ImageView ivPlayerAvatar;
    @BindView(R.id.player_name) TextView tvPlayerName;
    @BindView(R.id.iv_national) ImageView ivNational;
    @BindView(R.id.tv_national_name) TextView tvNationalName;
    @BindView(R.id.iv_emblem) ImageView ivEmblem;
    @BindView(R.id.tv_team_name) TextView tvTeamName;
    @BindArray(R.array.player_tabs_array) String[] mVpTitles;

    // 선수 글쓰기 요청 ID
    public static final int REQUEST_EDITOR = 20000;

    int mPagerPosition;
    int mPagerOffsetPixels;
    PlayerPresenter mPresenter;
    PlayerVpAdapter mVpAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.PlayerTheme);
        super.onCreate(savedInstanceState);

        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_player)
                .setSwipeBackView(R.layout.swipeback)
                .setOnInterceptMoveEventListener((v, dx, x, y) -> {
                    if (v == mViewPager) {
                        return !(mPagerPosition == 0 && mPagerOffsetPixels == 0)
                                || dx < 0;
                    }

                    return false;
                });
        ButterKnife.bind(this);

        int playerId = getIntent().getIntExtra("ARGS_PLAYER_ID", 0);
        String playerName = getIntent().getStringExtra("ARGS_PLAYER_NAME");

        mPresenter = new PlayerPresenter(playerId);
        mPresenter.attachView(this);
        mPresenter.onLoadPlayer();

        initToolbar();
        initData(playerName);
        initViewPager(playerId, playerName);
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
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

    /*───-----------------------------------────────────────────────────────*/

    @Override
    protected void onEvent(Object event) {
        super.onEvent(event);
    }

    @Override
    public void showLoading() {
        super.showLoading();
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
    }

    @Override
    public void showMessage(String message) {
        super.showMessage(message);
    }

    @Override
    public void showErrorMessage(String errMessage) {
        super.showErrorMessage(errMessage);
    }


    /*───-----------------------------------────────────────────────────────*/

    @OnClick(R.id.fab_post_content)
    public void onPostContentFabClick() {
        mPresenter.openTextEditor();
    }

    /*───-----------------------------------────────────────────────────────*/

    private void initToolbar() {

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
    }

    public void setData(PlayerModel playerData) {

        tvPlayerName.setText(playerData.getPlayerName());
        GlideApp.with(getApplicationContext())
                .load(playerData.getPlayerLargeImageUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_person)
                .into(ivPlayerAvatar);

        tvTeamName.setText(playerData.getTeamName());
        GlideApp.with(getApplicationContext())
                .load(playerData.getEmblemUrl())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_empty_emblem_vector_1)
                .into(ivEmblem);

        tvNationalName.setText(playerData.getNationality());
        int resImageId = CountryFlagUtils.getInstance().getResId(playerData.getNationality(), this);
        if ( resImageId > 0 ) {
            ivNational.setImageResource(resImageId);
        }
    }

    private void initData(String playerName) {

        tvPlayerName.setText(playerName);
    }

    private void initViewPager(int playerId, String playerName) {

        mVpAdapter = new PlayerVpAdapter(getSupportFragmentManager(), mVpTitles, playerId, playerName);
        mViewPager.setAdapter(mVpAdapter);
        mTabLayout.setViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mPagerPosition = position;
                mPagerOffsetPixels = positionOffsetPixels;
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}

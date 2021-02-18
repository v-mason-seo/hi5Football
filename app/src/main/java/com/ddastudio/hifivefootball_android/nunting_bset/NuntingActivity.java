package com.ddastudio.hifivefootball_android.nunting_bset;

import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Toast;

import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.event.SelectedSiteEvent;
import com.ddastudio.hifivefootball_android.ui.base.BaseActivity;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import butterknife.BindArray;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class NuntingActivity extends BaseActivity {

    @BindView(R.id.tabs)
    SmartTabLayout mTabs;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.viewpager)
    ViewPager mViewpager;
    @BindString(R.string.app_name) String appName;
    @BindArray(R.array.nunting_tabs_array) String[] mVpTitles;

    Menu mMenu;
    ActionBar mActionBar;
    NuntingVpAdapter mVPAdapter;
    NuntingPresenter mPresenter;

    int mPagerPosition;
    int mPagerOffsetPixels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_nunting);
        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_nunting)
                .setSwipeBackView(R.layout.swipeback)
                .setOnInterceptMoveEventListener((v, dx, x, y) -> {
                    if (v == mViewpager) {
                        return !(mPagerPosition == 0 && mPagerOffsetPixels == 0)
                                || dx < 0;
                    }

                    return false;
                });
        ButterKnife.bind(this);
        ButterKnife.bind(this);

        initializeToolbar();
        initializeTitle();
        initializeViewpPager();
    }

    /*---------------------------------------------------------------------------------------------*/

    @Override
    protected void onEvent(Object event) {
        super.onEvent(event);
        if ( event instanceof SelectedSiteEvent) {

            String siteName = ((SelectedSiteEvent) event).getSiteInfo().getName().getNm();
            setTitle(siteName);
            if ( mViewpager != null ) {
                mViewpager.setCurrentItem(1);
            }
        }
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
        Toasty.normal(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorMessage(String errMessage) {
        super.showErrorMessage(errMessage);
        Toasty.error(getApplicationContext(), errMessage, Toast.LENGTH_LONG).show();
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 툴바 초기화
     */
    private void initializeToolbar() {
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_vector);
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 타이틀 초기화
     */
    private void initializeTitle() {

        setTitle(appName);
    }

    /**
     * 뷰페이저 초기화
     * 사이트 리스트를 동적으로 생성한다.
     */
    public void initializeViewpPager() {

        mVPAdapter = new NuntingVpAdapter(getSupportFragmentManager(), mVpTitles);
        mViewpager.setOffscreenPageLimit(3);
        mViewpager.setAdapter(mVPAdapter);
        mTabs.setViewPager(mViewpager);

        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mPagerPosition = position;
                mPagerOffsetPixels = positionOffsetPixels;
            }

            @Override
            public void onPageSelected(int position) {

                if ( position == 0 ) {
                    setTitle(appName);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}

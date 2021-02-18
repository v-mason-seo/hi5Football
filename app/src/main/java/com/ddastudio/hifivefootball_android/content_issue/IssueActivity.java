package com.ddastudio.hifivefootball_android.content_issue;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.PostBoardType;
import com.ddastudio.hifivefootball_android.common.PostEditorType;
import com.ddastudio.hifivefootball_android.content_editor.EditorActivity;
import com.ddastudio.hifivefootball_android.data.event.ContentListEvent;
import com.ddastudio.hifivefootball_android.main.MainActivity;
import com.ddastudio.hifivefootball_android.auth.LoginActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseActivity;
import com.ddastudio.hifivefootball_android.utils.CommonUtils;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IssueActivity extends BaseActivity {

    @BindView(R.id.main_content) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.issue_toolbar) Toolbar mToolbar;
    @BindView(R.id.issue_tabs) SmartTabLayout mTabLayout;
    @BindView(R.id.issue_viewpager) ViewPager mViewPager;
    @BindArray(R.array.issue_tabs_array) String[] mVpTitles;

    IssueContainerVpAdapter mVpAdapter;
    private int mPagerPosition;
    private int mPagerOffsetPixels;

    public static final int REQUEST_ISSUE_EDITOR = 20001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_issue);
        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_issue)
                .setSwipeBackView(R.layout.swipeback)
                .setOnInterceptMoveEventListener((v, dx, x, y) -> {
                    if (v == mViewPager) {
                        return !(mPagerPosition == 0 && mPagerOffsetPixels == 0)
                                || dx < 0;
                    }

                    return false;
                });
        ButterKnife.bind(this);

        initToolbar();
        initViewPager();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.swipeback_stack_to_front,
                R.anim.swipeback_stack_right_out);
    }

    @OnClick(R.id.fab_post_issue)
    public void onFabClick() {

        if ( !App.getAccountManager().isAuthorized() ) {
            Snackbar.make(mCoordinatorLayout, "로그인 후 이용가능합니다", Snackbar.LENGTH_LONG)
                    .setAction("이동", v -> startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), MainActivity.REQUEST_LOGIN))
                    .show();
            return;
        }

        Intent intent = new Intent(getApplicationContext(), EditorActivity.class);
        intent.putExtra("ARGS_INSERT_OR_UPDATE", PostEditorType.INSERT.value());
        intent.putExtra("ARGS_BOARD_TYPE", PostBoardType.ISSUE.value());
        startActivityForResult(intent, REQUEST_ISSUE_EDITOR);
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
        //super.onActivityResult(requestCode, resultCode, data);
        if ( resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {

            case REQUEST_ISSUE_EDITOR:

                // 어떤 게시판에 글을 작성했는지 -> 해당 게시판으로 이동해서 새로고침을 한다.
                if ( data != null ) {
                    // 이슈 글 관련 글을 작성했다면 Open 탭으로 이동 후 데이터 새로고침을 한다.
                    mViewPager.setCurrentItem(0);
                    _rxBus.send(new ContentListEvent.RefreshListEvent(PostBoardType.ISSUE));
                }
                break;
        }
    }

    /*───-----------------------------------────────────────────────────────*/

    /**
     * 툴바 초기화
     */
    private void initToolbar() {

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("건의/개선");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(CommonUtils.getColor(getApplicationContext(), R.color.issue_status_bar));
        }
    }

    private void initViewPager() {

        mVpAdapter = new IssueContainerVpAdapter(getSupportFragmentManager(), mVpTitles);
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

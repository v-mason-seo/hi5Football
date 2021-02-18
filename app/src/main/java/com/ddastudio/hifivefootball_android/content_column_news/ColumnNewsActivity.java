package com.ddastudio.hifivefootball_android.content_column_news;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.ddastudio.hifivefootball_android.BuildConfig;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.ui.base.BaseActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ddastudio.hifivefootball_android.content_column_news.ColumnNewsFragment;
import com.ddastudio.hifivefootball_android.utils.CommonUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ColumnNewsActivity extends BaseActivity {

    public final static int HUMOR = 420;
    public final static int COLUMN = 350;
    public final static int NEWS = 300;

    @BindView(R.id.humor_toolbar) Toolbar mToolbar;
    @BindView(R.id.fragment_container) FrameLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_humor);
        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_column_news)
                .setSwipeBackView(R.layout.swipeback);

        ButterKnife.bind(this);

        initToolbar();

        int fragmentType = getIntent().getIntExtra("ARGS_FRAGMENT_TYPE", HUMOR);
        BaseFragment baseFragment = ColumnNewsFragment.newInstance(fragmentType);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, baseFragment);
        fragmentTransaction.commit();
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


    /*--------------------------------------------------------------------------*/

    /**
     * 툴바 초기화
     */
    private void initToolbar() {

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int fragmentType = getIntent().getIntExtra("ARGS_FRAGMENT_TYPE", HUMOR);

        if ( fragmentType == HUMOR ) {
            setTitle("유머게시판");
        } else if (fragmentType == COLUMN ) {
            setTitle("칼럼");
        } else if ( fragmentType == NEWS ) {
            setTitle("축구소식");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(CommonUtils.getColor(getApplicationContext(), R.color.issue_status_bar));
        }
    }
}

package com.ddastudio.hifivefootball_android.match_detail;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.BuildConfig;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.event.FixtureEvent;
import com.ddastudio.hifivefootball_android.data.manager.CompetitionsManager;
import com.ddastudio.hifivefootball_android.data.model.football.CompModel;
import com.ddastudio.hifivefootball_android.data.model.football.CompetitionModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseActivity;
import com.ddastudio.hifivefootball_android.ui.vpadapter.FixtureVpAdapter;
import com.ddastudio.hifivefootball_android.ui.widget.Filter.FilterData;
import com.ddastudio.hifivefootball_android.ui.widget.Filter.FilterEntity;
import com.ddastudio.hifivefootball_android.ui.widget.Filter.FilterTwoEntity;
import com.ddastudio.hifivefootball_android.ui.widget.Filter.FilterView;
import com.ddastudio.hifivefootball_android.ui.widget.Filter.ModelUtil;
import com.ddastudio.hifivefootball_android.utils.CommonUtils;
import com.ddastudio.hifivefootball_android.utils.TimeUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class MatchDetailInfoActivity extends BaseActivity {

    @BindView(R.id.humor_toolbar) Toolbar mToolbar;
    @BindView(R.id.main_viewpager) ViewPager mViewPager;
    @BindView(R.id.real_filterView) FilterView realFilterView;

    String mSelectedCompList;
    FixtureVpAdapter mVpAdapter;
    FilterData filterData;
    CompetitionsManager mCompetitionsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail_info);
        ButterKnife.bind(this);
        this.mCompetitionsManager = CompetitionsManager.getInstance();

        initToolbar();
        initViewPager();
        initFilterView();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if (realFilterView.isShowing()) {
            realFilterView.resetAllStatus();
        } else {
            super.onBackPressed();
        }
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

    /*--------------------------------------------------------------------------*/

    /**
     * 툴바 초기화
     */
    private void initToolbar() {

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("경기일정");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(CommonUtils.getColor(getApplicationContext(), R.color.issue_status_bar));
        }
    }

    private void initViewPager() {

        mVpAdapter = new FixtureVpAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mVpAdapter);
        mViewPager.setCurrentItem(TimeUtils.getPositionForDay(Calendar.getInstance()));
    }

    private void initFilterView() {

        /**
         * 필터에서 선택한 리그 정보를 담고 있다.
         * 선택된 값과 필터에서 받아온 값을 비교하여 다를 경우만 이벤트를 보내 새로고침을 한다.
         */
        mSelectedCompList = "";
        filterData = new FilterData();

        onLoadCompetitions();

        filterData.setCategory(ModelUtil.getCategoryData());
        filterData.setSorts(ModelUtil.getSortData());
        realFilterView.setFilterData(this, filterData);

        realFilterView.setOnFilterHideListener(new FilterView.OnFilterHideListener() {
            @Override
            public void onFilterHide(int position) {

                String json = realFilterView.getSelectedCompetitions();
                if (!mSelectedCompList.equals(json)) {
                    mSelectedCompList = json;
                    App.getInstance().bus().send(new FixtureEvent.RefreshFixtureEvent(json));
                }
            }
        });

        // 필터 클릭
        realFilterView.setOnFilterClickListener(new FilterView.OnFilterClickListener() {
            @Override
            public void onFilterClick(int position) {
                realFilterView.show(position);
            }
        });

        realFilterView.setOnItemFilterClickListener(new FilterView.OnItemFilterClickListener() {
            @Override
            public void onItemFilterClick(FilterEntity entity) {
            }
        });

        realFilterView.setChipCheckedChange(new FilterView.OnChipCheckedChange() {
            @Override
            public void onChipCheckedChange(int index, boolean checked, boolean userClick) {

            }
        });
    }

    /*--------------------------------------------------------------------------*/

    private void onLoadFinishedCompetitions(List<CompModel> items) {
        filterData.setCompetitions(items);
    }

    public void onLoadCompetitions() {

        showLoading();

        Flowable<List<CompModel>> observable
                = mCompetitionsManager.onLoadCompetitions(1);

        _disposables.add(
                observable.observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(items -> {})
                        .doOnError(e -> showErrorMessage(e.getMessage()))
                        .subscribe(
                                items -> {
                                    onLoadFinishedCompetitions(items);
                                },
                                e -> hideLoading(),
                                () -> {}
                        ));
    }
}

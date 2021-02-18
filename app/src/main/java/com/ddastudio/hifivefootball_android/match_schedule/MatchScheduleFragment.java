package com.ddastudio.hifivefootball_android.match_schedule;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.event.FixtureEvent;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.football.CompModel;
import com.ddastudio.hifivefootball_android.data.model.football.CompetitionModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchDateModel;
import com.ddastudio.hifivefootball_android.ui.fragment.LeagueTableDialogFragment;
import com.ddastudio.hifivefootball_android.ui.rvadapter.ScheduleRvAdapter;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ddastudio.hifivefootball_android.ui.utils.CustomLoadMoreView;
import com.ddastudio.hifivefootball_android.ui.widget.Filter.FilterData;
import com.ddastudio.hifivefootball_android.ui.widget.Filter.FilterEntity;
import com.ddastudio.hifivefootball_android.ui.widget.Filter.FilterView;
import com.ddastudio.hifivefootball_android.ui.widget.Filter.ModelUtil;
import com.ddastudio.hifivefootball_android.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * 전체 경기일정을 관리하는 프래그먼트
 */
public class MatchScheduleFragment extends BaseFragment {

    /**
     * 위 아래로 스크롤
     */
    final public static int SCROLL_FIXTURE_MODE = 0;
    /**
     * 해당 날짜 경기일정 전체 조회 - 스크롤 없음.
     */
    final public static int FIXED_FIXTURE_MODE = 1;

    @BindView(R.id.progress) ProgressBar progress;
    @BindView(R.id.real_filterView) FilterView realFilterView;
    @BindView(R.id.schedule_list) RecyclerView rvList;

    int mOffset = 0;
    int mPreOffset = 0;
    int mLimit = 20;
    String mSelectedCompList;

    FilterData filterData;
    ScheduleRvAdapter mRvAdapter;
    MatchSchedulePresenter mPresenter;

    public MatchScheduleFragment() {
        // Required empty public constructor
    }

    public static MatchScheduleFragment newInstance() {
        MatchScheduleFragment fragment = new MatchScheduleFragment();
        Bundle args = new Bundle();
        args.putInt("ARGS_FIXTURE_MODE", SCROLL_FIXTURE_MODE);
        fragment.setArguments(args);

        return fragment;
    }

    public static MatchScheduleFragment newInstance(int competitionId) {
        MatchScheduleFragment fragment = new MatchScheduleFragment();
        Bundle args = new Bundle();
        args.putInt("ARGS_FIXTURE_MODE", SCROLL_FIXTURE_MODE);
        args.putInt("ARGS_COMPETITION_ID", competitionId);
        fragment.setArguments(args);

        return fragment;
    }

    public static MatchScheduleFragment newInstance(int competitionId, int teamId) {
        MatchScheduleFragment fragmentFirst = new MatchScheduleFragment();

        Bundle args = new Bundle();
        args.putInt("ARGS_FIXTURE_MODE", SCROLL_FIXTURE_MODE);
        args.putInt("ARGS_COMPETITION_ID", competitionId);
        args.putInt("ARGS_TEAM_ID", teamId);
        fragmentFirst.setArguments(args);

        return fragmentFirst;
    }

    //-----------------------------------------------------------------------

    public static MatchScheduleFragment newInstance(long fixtureDate) {
        MatchScheduleFragment fragment = new MatchScheduleFragment();
        Bundle args = new Bundle();
        args.putInt("ARGS_FIXTURE_MODE", FIXED_FIXTURE_MODE);
        args.putLong("ARGS_FIXTURE_DATE", fixtureDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Integer competitionId = null;
        Integer teamId = null;
        String fixtureDate;

        if ( getArguments() != null ) {

            int fixtureMode = getArguments().getInt("ARGS_FIXTURE_MODE");
            mPresenter = new MatchSchedulePresenter(fixtureMode);

            if ( fixtureMode == SCROLL_FIXTURE_MODE ) {
                //
                // 위 아래로 스크롤 해서 경기일정 조회
                //
                competitionId = getArguments().getInt("ARGS_COMPETITION_ID");
                teamId = getArguments().getInt("ARGS_TEAM_ID");
                mPresenter.setCompetitionId(competitionId);
                mPresenter.setTeamId(teamId);

            } else if ( fixtureMode == FIXED_FIXTURE_MODE ) {
                //
                // 날짜 단위로 경기 일정 조회
                //
                final long millis = getArguments().getLong("ARGS_FIXTURE_DATE");
                if (millis > 0) {
                    fixtureDate = TimeUtils.getFormattedDate(millis, "yyyyMMdd");
                    mPresenter.setFixtureDate(fixtureDate);
                }
            }

        } else {
            mPresenter = new MatchSchedulePresenter(SCROLL_FIXTURE_MODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_match_schedule, container, false);
        _unbinder = ButterKnife.bind(this, view);

        //
        // onCreate -> onCreateView 위치이동
        // 다른 페이지도 갔다가 왔을 때  onCreate() 호출이 되지 않는다. 따라서 초기화 되지 않은 값으로 데이터를 조회하게 되서
        // 데이터가 나오지 않는 문제가 발생한다.
        mLimit = 20;
        mOffset = 0;
        mPreOffset = 0;

        mPresenter.attachView(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initFilterView();
        initRecyclerView();

        // 데이터 조회
        if ( mPresenter.getFixtureMode() == SCROLL_FIXTURE_MODE ) {
            mPresenter.onLoadNextFixtures(mLimit, mOffset);
        } else if ( mPresenter.getFixtureMode() == FIXED_FIXTURE_MODE ) {
            mPresenter.onLoadFixedFixture();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.detachView();
    }

    /*---------------------------------------------------------------------------------------------*/

    @Override
    protected void onEvent(Object event) {
        super.onEvent(event);

        if ( event instanceof FixtureEvent.RefreshFixtureEvent) {

            // 우선 FIXED_FIXTURE_MODE 에서만 필터기능을 사용함.
            // FIXED_FIXTURE_MODE 는 MatchDetailInfoActivity 에서 사용한다.
            if ( mPresenter.getFixtureMode() != FIXED_FIXTURE_MODE ) {
                return;
            }

            if ( mRvAdapter != null ) {
                mRvAdapter.getData().clear();
                mRvAdapter.notifyDataSetChanged();
                String compList = ((FixtureEvent.RefreshFixtureEvent) event).getCompList();
                mPresenter.onLoadFixedFixture(compList);
            }
        }
    }

    @Override
    public void showLoading() {
        super.showLoading();
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
        progress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showMessage(String message) {
        super.showMessage(message);
        Toasty.normal(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorMessage(String errMessage) {
        super.showErrorMessage(errMessage);
        Toasty.error(getContext(), errMessage, Toast.LENGTH_LONG).show();
    }

    /*---------------------------------------------------------------------------------------------*/

    private void initFilterView() {

        /**
         * 필터에서 선택한 리그 정보를 담고 있다.
         * 선택된 값과 필터에서 받아온 값을 비교하여 다를 경우만 이벤트를 보내 새로고침을 한다.
         */
        mSelectedCompList = "";
        filterData = new FilterData();

        mPresenter.onLoadCompetitions();

        filterData.setCategory(ModelUtil.getCategoryData());
        filterData.setSorts(ModelUtil.getSortData());
        realFilterView.setFilterData(getActivity(), filterData);

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

    /**
     * RecyclerView 초기화
     */
    private void initRecyclerView() {

        List<MultiItemEntity> itemList = new ArrayList<>();

        View emptyView = getActivity().getLayoutInflater().inflate(R.layout.recycler_empty_view, (ViewGroup) rvList.getParent(), false);
        mRvAdapter = new ScheduleRvAdapter(itemList);
        mRvAdapter.setLoadMoreView(new CustomLoadMoreView());
        //mRvAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        //mRvAdapter.isFirstOnly(true);
        //mRvAdapter.setNotDoAnimationCount(3);
        mRvAdapter.setEmptyView(emptyView);

        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.setAdapter(mRvAdapter);

        //
        // SCROLL_FIXTURE_MODE 모드에서만 위, 아래로 스크롤시 데이터를 불러온다.
        // FIXED_FIXTURE_MODE 모드에서는 날짜 단위로 데이터를 가져오기 때문에 위 아래 스크롤시 데이터를 가져올 필요가 없다.
        //
        if ( mPresenter.getFixtureMode() == SCROLL_FIXTURE_MODE ) {

            mRvAdapter.setOnLoadMoreListener( () -> {
                    mPresenter.onLoadNextFixtures(mLimit, mOffset);
                }
            );

            mRvAdapter.setUpFetchListener(() -> {
                    if ( mRvAdapter.isUpFetching() == true )
                        return;

                    mRvAdapter.setUpFetching(true);
                    mPresenter.onLoadPreFixtures(mLimit, mPreOffset);
                }
            );

        } else if ( mPresenter.getFixtureMode() == FIXED_FIXTURE_MODE ) {
            mPresenter.onLoadFixedFixture();
        }

        mRvAdapter.setOnItemClickListener(((adapter, view, position) -> {

            if ( adapter.getItemViewType(position) == MultipleItem.ARENA_SCHEDULE ) {
                MatchModel item = (MatchModel)mRvAdapter.getItem(position);
                mPresenter.openMatchActivity(item);

            } else if ( adapter.getItemViewType(position) == MultipleItem.ARENA_COMPETITON) {
                CompetitionModel competition = (CompetitionModel)mRvAdapter.getItem(position);
                mPresenter.openCompetitionActivity(competition);

            }
        }));

        mRvAdapter.setOnItemChildClickListener( ((adapter, view, position) -> {

            switch (view.getId()) {

                case R.id.btn_show_standing:
                    CompModel comp = (CompModel)mRvAdapter.getItem(position);
                    onRvClickStanding(comp.getCompetitionId());
                    break;

                case R.id.hometeam_image:
                    MatchModel home = (MatchModel)mRvAdapter.getItem(position);
                    onRvClickTeam(home.getHomeTeamId());
                    break;

                case R.id.awayteam_image:
                    MatchModel away = (MatchModel)mRvAdapter.getItem(position);
                    onRvClickTeam(away.getAwayTeamId());
                    break;

                case R.id.btn_enter_arena:
                    onRvClickEnterArena((MatchModel)mRvAdapter.getItem(position));
                    break;
            }

        }));
    }

    /**
     * 리그 순위표 보기 클릭
     * @param competitionId
     */
    private void onRvClickStanding(int competitionId) {

        LeagueTableDialogFragment dialogFragment
                = LeagueTableDialogFragment.newInstance(MultipleItem.BOTTOM_STANDING, competitionId);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "LeagueTable");
    }

    /**
     * 팀 버튼 클릭
     * @param teamId
     */
    private void onRvClickTeam(int teamId) {
        mPresenter.openTeamActivity(teamId);
    }

    /**
     * 아레나 채팅방 이동 클릭
     * @param match
     */
    private void onRvClickEnterArena(MatchModel match) {
        mPresenter.openArenaActivity(match);
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 과거일자 경기일정 정보를 가져온다. ( 위로 스클롤시 )
     * @param items
     */
    public void onLoadFinishedPreFixtures(List<MatchModel> items) {

        if ( items.size() > 0) {

            mPreOffset += items.size();
            String preMatchDate = "";
            CompModel preComp = null;

            for (int i = 0; i < items.size(); i++) {

                if ( i == 0 ) {
                    preComp = items.get(i).getCompetition();
                    preMatchDate = items.get(i).getMatchDate();
                }

                if ( !preMatchDate.equals(items.get(i).getMatchDate()) ) {
                    mRvAdapter.addData(0, preComp);
                    mRvAdapter.addData(0,new MatchDateModel(preMatchDate));
                } else {
                    if ( preComp.getCompetitionId() != items.get(i).getCompetitionId()) {
                        mRvAdapter.addData(0, preComp);
                    }
                }

                mRvAdapter.addData(0, items.get(i));
                preComp = items.get(i).getCompetition();
                preMatchDate = items.get(i).getMatchDate();
            }

        } else {

            if ( mRvAdapter.getData().size() > 0 ) {

                MultiItemEntity item = mRvAdapter.getItem(0);
                if ( item instanceof MatchModel ) {
                    MatchModel matchData = (MatchModel)item;
                    mRvAdapter.addData(0, matchData.getCompetition());
                    mRvAdapter.addData(0,new MatchDateModel(matchData.getMatchDate()));
                }
            }

            // 더이상 조회할 데이터가 없다면 끝.
            mRvAdapter.setUpFetchEnable(false);
        }
        mRvAdapter.setUpFetching(false);
        hideLoading();
    }

    /**
     * 미래일자 경기일정 정보를 가져온다. ( 아래로 스크롤시 )
     * @param items
     */
    public void onLoadFinishedNextFixtures(List<MatchModel> items) {

        if ( items.size() > 0 ) {

            mOffset += items.size();
            String preMatchDate = "";
            CompModel preComp = null;

            if ( mRvAdapter.getData().size() > 0 ) {

                int lastPosition = mRvAdapter.getData().size()-1;
                MultiItemEntity data = mRvAdapter.getData().get(lastPosition);

                if ( data instanceof MatchModel) {
                    MatchModel match = (MatchModel)data;
                    preMatchDate = match.getMatchDate();
                    preComp = match.getCompetition();
                }
            }

            for (int i=0; i < items.size(); i++) {

                if ( !items.get(i).getMatchDate().equals(preMatchDate)) {
                    MultipleItem testData = new MultipleItem(MultipleItem.ARENA_LAST_SCHEDULE);
                    mRvAdapter.addData(testData);
                    MatchDateModel matchHeader = new MatchDateModel(items.get(i).getMatchDate());
                    mRvAdapter.addData(matchHeader);
                    preMatchDate = items.get(i).getMatchDate();
                    preComp = null;
                }

                if ( preComp == null || items.get(i).getCompetitionId() != preComp.getCompetitionId()) {

                    CompModel currentComp = items.get(i).getCompetition();
                    mRvAdapter.addData(currentComp);
                    preComp = items.get(i).getCompetition();
                }

                mRvAdapter.addData(items.get(i));
            }

            mRvAdapter.loadMoreComplete();
            // 스크롤 위로 올렸을 때 이전 일자 데이터 가져오기
            mRvAdapter.setUpFetchEnable(true);
            //mRvAdapter.setUpFetching(true);
        } else {
            mRvAdapter.loadMoreEnd(true);
        }

        hideLoading();
    }

    public void onLoadFinishedFixedFixture(List<MatchModel> items) {

        Collections.sort(items);

        if ( items.size() > 0 ) {
            int preCompetition = -1;

            for (int i=0; i < items.size(); i++) {

                if ( items.get(i).getCompetitionId() != preCompetition) {
                    //SimpleCompetitionModel competitionHeader = new SimpleCompetitionModel(items.get(i).getCompetitionName(), items.get(i).getCompetitionImageId());
                    mRvAdapter.addData(items.get(i).getCompetition());
                    preCompetition = items.get(i).getCompetitionId();
                }

                mRvAdapter.addData(items.get(i));
            }

            MultipleItem testData = new MultipleItem(MultipleItem.ARENA_LAST_SCHEDULE);
            mRvAdapter.addData(testData);

        }

        hideLoading();
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadFinishedCompetitions(List<CompModel> items) {
        filterData.setCompetitions(items);
    }


}

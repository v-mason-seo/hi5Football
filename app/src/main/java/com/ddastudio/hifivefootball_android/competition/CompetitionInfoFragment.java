package com.ddastudio.hifivefootball_android.competition;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.footballdata.MatchContainerModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ddastudio.hifivefootball_android.utils.DateUtils;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompetitionInfoFragment extends BaseFragment {

    public static final int  TYPE_MATCHES = 1240;
    public static final int TYPE_STANDINGS = 1241;

    //@BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.comp_info_list) RecyclerView rvCompInfoList;

    CompetitionInfoPresenter mPresenter;
    CompetitionInfoRvAdapter mRvAdapter;

    String mMatchNextDate;
    String mMatchPreDate;

    public CompetitionInfoFragment() {
        // Required empty public constructor
    }

    public static CompetitionInfoFragment newInstance(int dataType, int competitionId) {
        CompetitionInfoFragment fragmentFirst = new CompetitionInfoFragment();
        Bundle args = new Bundle();
        args.putInt("ARGS_COMPETITION_ID", competitionId);
        args.putInt("ARGS_DATA_TYPE", dataType);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_competition_info, container, false);
        _unbinder = ButterKnife.bind(this, view);

        int compId = getArguments().getInt("ARGS_COMPETITION_ID");
        int dataType = getArguments().getInt("ARGS_DATA_TYPE");

        mPresenter = new CompetitionInfoPresenter(dataType, compId);
        mPresenter.attachView(this);

        initRecyclerView();
        refreshData();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /*---------------------------------------------------------------------------------------------*/

//    private void initSwipeRefresh() {
//
//        swipeRefreshLayout.setColorSchemeResources(
//                R.color.progress_color1,
//                R.color.progress_color2,
//                R.color.progress_color3,
//                R.color.progress_color4 );
//
//        swipeRefreshLayout.setOnRefreshListener( () -> refreshData());
//    }

    private void initRecyclerView() {

        List<MultiItemEntity> itemList = new ArrayList<>();

        View emptyView = getActivity().getLayoutInflater().inflate(R.layout.recycler_empty_view, (ViewGroup) rvCompInfoList.getParent(), false);
        mRvAdapter = new CompetitionInfoRvAdapter(itemList);
        mRvAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mRvAdapter.isFirstOnly(true);
        mRvAdapter.setNotDoAnimationCount(3);
        mRvAdapter.setEmptyView(emptyView);

        rvCompInfoList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCompInfoList.addItemDecoration(new FlexibleItemDecoration(getContext()).withDefaultDivider().withDrawOver(true));
        rvCompInfoList.setAdapter(mRvAdapter);

        if ( mPresenter.getDataType() == TYPE_STANDINGS) {
            mRvAdapter.addHeaderView(getLeagueTableHeaderView());
        }

        // Up fetch
        mRvAdapter.setUpFetchListener(() ->
                rvCompInfoList.postDelayed(() -> {

                    if ( mRvAdapter.isUpFetching() == true )
                        return;

                    mRvAdapter.setUpFetching(true);
                    mPresenter.onLoadMatches(mMatchPreDate, CompetitionInfoPresenter.PRE_MATCH_DATA);
                }, 300)
        );

        // Load more
        mRvAdapter.setOnLoadMoreListener(() ->
                        rvCompInfoList.postDelayed(() -> {
                            mPresenter.onLoadMatches(mMatchNextDate, CompetitionInfoPresenter.NEXT_MATCH_DATA);
                        }, 300)
                , rvCompInfoList);

    }

    public void refreshData() {

        if ( mRvAdapter != null && mRvAdapter.getData().size() > 0 ) {
            mRvAdapter.getData().clear();
        }

        if ( mPresenter.getDataType() == TYPE_STANDINGS ) {
            mPresenter.onLoadStandings();
        } else if ( mPresenter.getDataType() == TYPE_MATCHES ) {

            Date today = Calendar.getInstance().getTime();
            mMatchNextDate = DateUtils.convertDateToString(today, "yyyy-MM-dd");

            mPresenter.onLoadMatches(mMatchNextDate, CompetitionInfoPresenter.NEW_MATCH_DATA);
        }
    }

    public void onLoadFinished(List<? extends MultiItemEntity> items) {

        mRvAdapter.getData().clear();
        mRvAdapter.addData(items);
        mRvAdapter.loadMoreEnd(true);
    }

    public void onLoadNewMatchFinished(MatchContainerModel matchContainer) {

        if ( matchContainer != null ) {
            mMatchNextDate = matchContainer.getNextFixtureDate();
            mMatchPreDate = matchContainer.getPreFixtureDate();
            mRvAdapter.addMatchContainer(matchContainer);
        }

        mRvAdapter.setEnableLoadMore(true);
        mRvAdapter.setUpFetchEnable(true);
        mRvAdapter.setStartUpFetchPosition(1);
    }

    public void onLoadPreMatchFinished(MatchContainerModel matchContainer) {

        if ( matchContainer != null ) {
            mMatchPreDate = matchContainer.getPreFixtureDate();
            mRvAdapter.addMatchContainer(0, matchContainer);
        }

        mRvAdapter.setUpFetching(false);

        if ( mMatchPreDate == null )
            mRvAdapter.setUpFetchEnable(false);
    }

    public void onLoadNextMatchFinished(MatchContainerModel matchContainer) {

        if ( matchContainer != null ) {
            mMatchNextDate = matchContainer.getNextFixtureDate();
            mRvAdapter.addMatchContainer(matchContainer);

            mRvAdapter.loadMoreComplete();
            if ( mMatchNextDate == null ) {
                mRvAdapter.loadMoreEnd(true);
            }

        }  else {
            mRvAdapter.loadMoreEnd(true);
        }
    }

//    public void invisibleRefreshing() {
//
//        if ( swipeRefreshLayout.isRefreshing() ) {
//            swipeRefreshLayout.setRefreshing(false);
//        }
//    }

    private View getLeagueTableHeaderView() {
        View view = getActivity().getLayoutInflater()
                .inflate(R.layout.row_league_table_item, (ViewGroup) rvCompInfoList.getParent(), false);

        view.setBackgroundResource(R.drawable.backgroud_gradient_grey);


        return view;
    }
}

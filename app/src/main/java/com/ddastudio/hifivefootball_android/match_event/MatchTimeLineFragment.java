package com.ddastudio.hifivefootball_android.match_event;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class MatchTimeLineFragment extends BaseFragment {

    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.match_event_list) RecyclerView rvMatchEvents;
    View emptyView;

    MatchTimeLinePresenter mPresenter;
    MatchTimeLineRvAdapter mRvAdapter;

    public MatchTimeLineFragment() {
        // Required empty public constructor
    }

    public static MatchTimeLineFragment newInstance(MatchModel matchData) {
        MatchTimeLineFragment fragmentFirst = new MatchTimeLineFragment();
        Bundle args = new Bundle();
        args.putParcelable("ARGS_MATCH", Parcels.wrap(matchData));
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MatchModel match = Parcels.unwrap(getArguments().getParcelable("ARGS_MATCH"));
        mPresenter = new MatchTimeLinePresenter(match);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_match_time_line, container, false);
        _unbinder = ButterKnife.bind(this, view);
        mPresenter.attachView(this);

        initRefresh();
        initRecyclerView();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPresenter.onLoadMatchEvents();
    }

    @Override
    public void onDestroyView() {
        mPresenter.detachView();
        super.onDestroyView();
    }

    @Override
    public void showLoading() {
        super.showLoading();
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
        swipeRefreshLayout.setRefreshing(false);
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

    /**
     * SwipeRefreshLayout 초기화
     */
    public void initRefresh() {

        swipeRefreshLayout.setColorSchemeResources(
                R.color.progress_color1,
                R.color.progress_color2,
                R.color.progress_color3,
                R.color.progress_color4 );

        swipeRefreshLayout.setOnRefreshListener( () -> {

            if ( mRvAdapter != null && mRvAdapter.getData().size() > 0) {
                mRvAdapter.getData().clear();
            }

            mPresenter.onLoadMatchEvents();
        });
    }

    private void initRecyclerView() {

        emptyView = getActivity().getLayoutInflater().inflate(R.layout.recycler_empty_view, (ViewGroup) rvMatchEvents.getParent(), false);
        mRvAdapter = new MatchTimeLineRvAdapter(new ArrayList<>());
//        mRvAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
//        mRvAdapter.isFirstOnly(true);
//        mRvAdapter.setNotDoAnimationCount(3);

        rvMatchEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMatchEvents.setAdapter(mRvAdapter);
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadFinished(List<? extends MultiItemEntity> items) {

        if ( items == null || items.size() == 0 ) {
            mRvAdapter.setEmptyView(emptyView);
            hideLoading();
            return;
        }

        mRvAdapter.getData().clear();
        mRvAdapter.addData(items);

        hideLoading();
    }

}

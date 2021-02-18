package com.ddastudio.hifivefootball_android.match_summery;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.match_summery.model.SummeryBaseWrapperModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ddastudio.hifivefootball_android.utils.ItemClickSupport;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainMatchFragment extends BaseFragment {

    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.main_match) RecyclerView rvMainMatch;
    View emptyFooterView;

    MainMatchPresenter mPresenter;
    MatchSummeryViewModel mViewModel;
    SummeryContainerRvAdapter mContainerRvAdapter;

    public MainMatchFragment() {
        // Required empty public constructor
    }

    public static MainMatchFragment newInstance() {
        MainMatchFragment fragmentFirst = new MainMatchFragment();
        return fragmentFirst;
    }

    //
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = new MainMatchPresenter();
        mPresenter.attachView(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_match, container, false);
        _unbinder = ButterKnife.bind(this, view);

        initEmptyView();
        initRefresh();
        initContainerRecyclerView();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(MatchSummeryViewModel.class);
        mViewModel.getSummeryList().observe(this, summeryList -> {

            if ( summeryList == null || summeryList.size() == 0) {
                mPresenter.onLoadData();
            } else {
                mContainerRvAdapter.onNewData(summeryList);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.detachView();
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 메시지를 보여준다.
     * @param message
     */
    public void showMessage(String message) {
        Toasty.normal(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 에러 메시지를 보여준다.
     * @param errMessage
     */
    public void showErrorMessage(String errMessage ) {
        Toasty.error(getContext(), errMessage, Toast.LENGTH_LONG).show();
    }

    /**
     * 로딩화면 보이기
     */
    public void showLoading() {
        swipeRefreshLayout.setRefreshing(true);
    }

    /**
     * 로딩화면 감추기
     */
    public void hideLoading() {

        if ( swipeRefreshLayout == null )
            return;

        swipeRefreshLayout.setRefreshing(false);
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
            mViewModel.clearData();
            //mPresenter.onLoadData();
        });
    }

    private void initEmptyView() {
        // 라사이클뷰 하단에 빈공간을 삽입한다. - fab 중복을 피하기 위해서
        emptyFooterView = getActivity().getLayoutInflater().inflate(R.layout.row_empty_normal_footer_view, (ViewGroup)rvMainMatch.getParent(), false);
    }

    /**
     *
     * https://github.com/bleeding182/recyclerviewItemDecorations
     */
    private void initContainerRecyclerView() {

        ItemClickSupport.addTo(rvMainMatch).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

            }
        });

        rvMainMatch.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMainMatch.setHasFixedSize(true);

        mContainerRvAdapter = new SummeryContainerRvAdapter(getContext());
        rvMainMatch.setAdapter(mContainerRvAdapter);
    }

    /*---------------------------------------------------------------------------------------------*/

    public void onLoadFinished(List<SummeryBaseWrapperModel> items) {
        //mContainerRvAdapter.addItems(items);
        mViewModel.addItems(items);
    }

}

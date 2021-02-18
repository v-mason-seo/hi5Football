package com.ddastudio.hifivefootball_android.nunting_bset;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.NuntingViewType;
import com.ddastudio.hifivefootball_android.data.event.SelectedSiteEvent;
import com.ddastudio.hifivefootball_android.nunting_bset.model.SiteBoardModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ddastudio.hifivefootball_android.ui.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class NuntingSiteListFragment extends BaseFragment {

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rv_list)
    RecyclerView rvList;

    NuntingSiteListRvAdapter mRvAdapter;
    NuntingSiteListPresenter mPresenter;

    public NuntingSiteListFragment() {
        // Required empty public constructor
    }


    public static NuntingSiteListFragment newInstance() {
        NuntingSiteListFragment fragment = new NuntingSiteListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nunting_site_list, container, false);
        _unbinder = ButterKnife.bind(this, view);

        mPresenter = new NuntingSiteListPresenter();
        mPresenter.attachView(this);

        initRefresh();
        initRecyclerView();

        mPresenter.onLoadSiteList();

        return view;
    }

    /*---------------------------------------------------------------------------------------------*/


    public void initRefresh() {

        swipeRefreshLayout.setColorSchemeResources(
                R.color.progress_color1,
                R.color.progress_color2,
                R.color.progress_color3,
                R.color.progress_color4 );

        swipeRefreshLayout.setOnRefreshListener( () -> {
            mPresenter.onLoadSiteList();
        });
    }

    private void initRecyclerView() {

        mRvAdapter = new NuntingSiteListRvAdapter(new ArrayList<>());
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        rvList.setAdapter(mRvAdapter);

        mRvAdapter.setOnItemClickListener((adapter, view, position) -> {

            switch (adapter.getItemViewType(position)) {
                case NuntingViewType.SITE_LIST:
                    SiteBoardModel site = (SiteBoardModel)mRvAdapter.getData().get(position);
                    App.getInstance().bus().send(new SelectedSiteEvent(site));
                    break;
            }
        });
    }

    public void onLoadFinished(List<SiteBoardModel> items) {

        if ( mRvAdapter != null ) {
            mRvAdapter.getData().clear();
            mRvAdapter.addData(items);
        }
    }

}

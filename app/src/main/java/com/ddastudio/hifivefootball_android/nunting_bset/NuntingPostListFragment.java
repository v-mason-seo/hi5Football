package com.ddastudio.hifivefootball_android.nunting_bset;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.NuntingViewType;
import com.ddastudio.hifivefootball_android.data.event.StringEvent;
import com.ddastudio.hifivefootball_android.nunting_bset.model.PostsHeaderModel;
import com.ddastudio.hifivefootball_android.nunting_bset.model.PostsListModel;
import com.ddastudio.hifivefootball_android.nunting_bset.model.SiteBoardModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ddastudio.hifivefootball_android.ui.utils.CustomLoadMoreView;
import com.ddastudio.hifivefootball_android.ui.utils.SimpleDividerItemDecoration;
import com.ddastudio.hifivefootball_android.utils.DateUtils;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NuntingPostListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NuntingPostListFragment extends BaseFragment {

    @BindView(R.id.rvPostsList)
    RecyclerView rvPostsList;
    @BindView(R.id.swPostsList)
    SwipeRefreshLayout swipeRefreshLayout;

    Calendar baseCal;
    NuntingPostListPresenter mPresenter;
    NuntingPostListRvAdapter mRvAdapter;

    String mCurrentPid = "";
    String mPrevPid = "";

    public NuntingPostListFragment() {
        // Required empty public constructor
    }


    public static NuntingPostListFragment newInstance(SiteBoardModel.Name site, SiteBoardModel.Boards board) {
        NuntingPostListFragment fragment = new NuntingPostListFragment();
        Bundle args = new Bundle();
        args.putParcelable("ARGS_SITE", Parcels.wrap(site));
        args.putParcelable("ARGS_BOARD", Parcels.wrap(board));

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
        View view = inflater.inflate(R.layout.fragment_nunting_post_list, container, false);
        ButterKnife.bind(this, view);

        SiteBoardModel.Boards board = null;
        SiteBoardModel.Name site = null;
        site = Parcels.unwrap(getArguments().getParcelable("ARGS_SITE"));
        board = Parcels.unwrap(getArguments().getParcelable("ARGS_BOARD"));

        mPresenter = new NuntingPostListPresenter();
        mPresenter.attachView(this);
        mPresenter.setBoard(board);
        mPresenter.setSite(site);

        initRecyclerView();
        initializeSwipeRefresh();

        baseCal = Calendar.getInstance();
        mCurrentPid = DateUtils.convertDateToString(baseCal.getTime(), "yyyyMMddHH");
        mPrevPid = "0000000000";
        mPresenter.loadPostsList(site.getId(), board.getBid(), mCurrentPid);

        return view;
    }

    /*---------------------------------------------------------------------------------------------*/

    @Override
    protected void onEvent(Object event) {
        super.onEvent(event);
        if ( event instanceof StringEvent) {

            switch (((StringEvent) event).getEventType()) {
                case StringEvent.VERTICAL_TOP:
                    if ( rvPostsList != null) {
                        rvPostsList.getLayoutManager().scrollToPosition(0);
                    }
                    break;
                case StringEvent.POSTSLIST_REFRESH:
                    refreshData();
                    break;
            }
        }
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

    public void onLoadFinished(List<PostsListModel> items) {

        if ( mRvAdapter != null ) {
            if ( items != null && items.size() > 0) {
                mRvAdapter.addData(new PostsHeaderModel(mCurrentPid));
                mRvAdapter.addData(items);
                mRvAdapter.loadMoreComplete();
            } else {
                mRvAdapter.loadMoreEnd();
            }

        }
    }

    /*---------------------------------------------------------------------------------------------*/

    private void initializeSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.progress_color1,
                R.color.progress_color2,
                R.color.progress_color3,
                R.color.progress_color4 );

        swipeRefreshLayout.setOnRefreshListener(() -> refreshData());
    }

    private void refreshData() {

        if ( mRvAdapter != null ) {
            if ( mRvAdapter.getData() != null && mRvAdapter.getData().size() > 0 ) {
                mRvAdapter.getData().clear();
                mRvAdapter.notifyDataSetChanged();
            }

            baseCal = Calendar.getInstance();
            mCurrentPid = DateUtils.convertDateToString(baseCal.getTime(), "yyyyMMddHH");
                    mPrevPid = "0000000000";
            mPresenter.loadPostsList(mPresenter.getSite().getId(), mPresenter.getBoard().getBid(), mCurrentPid);
        }
    }

    private void initRecyclerView() {

        mRvAdapter = new NuntingPostListRvAdapter(new ArrayList<>());
        mRvAdapter.setLoadMoreView(new CustomLoadMoreView());
        mRvAdapter.setEnableLoadMore(true);
        mRvAdapter.setOnLoadMoreListener( () -> {
            mPresenter.loadPostsList(mPresenter.getSite().getId(), mPresenter.getBoard().getBid(), mPrevPid);
        });

        rvPostsList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPostsList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        rvPostsList.setAdapter(mRvAdapter);

        mRvAdapter.setOnItemClickListener((adapter, view, position) -> {

            if ( adapter.getItemViewType(position) == NuntingViewType.POSTS) {
                PostsListModel item = (PostsListModel)mRvAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), NuntingContentViewerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("ARGS_SITE", Parcels.wrap(mPresenter.getSite()));
                intent.putExtra("ARGS_BOARD", Parcels.wrap(mPresenter.getBoard()));
                intent.putExtra("ARGS_POSTS_MODEL", item);
                startActivity(intent);
            }
        });
    }

    public void setCurrentPid(String value) {
        this.mCurrentPid = value;
    }

    public void setPrevPid(String value) {
        this.mPrevPid = value;
    }

}

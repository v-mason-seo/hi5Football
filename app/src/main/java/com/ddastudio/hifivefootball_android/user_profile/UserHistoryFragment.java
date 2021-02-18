package com.ddastudio.hifivefootball_android.user_profile;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.EntityType;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ddastudio.hifivefootball_android.ui.utils.SimpleDividerItemDecoration;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserHistoryFragment extends BaseFragment implements BaseContract.View {

    public final static int TYPE_CONTENT = 0;
    public final static int TYPE_COMMENT = 1;
    public final static int TYPE_HIFIVE = 2;
    public final static int TYPE_SCRAP = 3;

    View emptyView;
    UserHistoryPresenter mPresenter;
    UserHistoryRvAdapter mRvAdapter;

    int mLimit;
    int mCurrentCounter = 0;

    @BindView(R.id.swipeLayout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;

    public static UserHistoryFragment newInstance(UserModel user, int mode) {

        UserHistoryFragment fragmentFirst = new UserHistoryFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("ARGS_USER", Parcels.wrap(user));
        bundle.putInt("ARGS_SELECT_MODE", mode);
        fragmentFirst.setArguments(bundle);
        return fragmentFirst;
    }

    public UserHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_history, container, false);
        _unbinder = ButterKnife.bind(this, view);

        int selectMode = getArguments().getInt("ARGS_SELECT_MODE");
        UserModel user = Parcels.unwrap(getArguments().getParcelable("ARGS_USER"));
        mPresenter = new UserHistoryPresenter(user, selectMode);
        mPresenter.attachView(this);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        mLimit = Integer.valueOf(pref.getString("limit_count", "20"));

        initSwipRefreshLayout();
        initRecyclerView();

        mPresenter.onLoadData(mLimit, 0);

        return view;
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
    }

    /*---------------------------------------------------------------------------------------------*/


    private void initSwipRefreshLayout() {

        swipeRefreshLayout.setColorSchemeResources(
                R.color.progress_color1,
                R.color.progress_color2,
                R.color.progress_color3,
                R.color.progress_color4 );

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentCounter = 0;
                mRvAdapter.getData().clear();
                mPresenter.onLoadData(mLimit, 0);
            }
        });
    }

    private void initRecyclerView() {

        emptyView = getActivity().getLayoutInflater().inflate(R.layout.recycler_empty_view, (ViewGroup) mRecyclerView.getParent(), false);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        List<MultiItemEntity> itemList = new ArrayList<>();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext(), 46));
//        mRecyclerView.addItemDecoration(new FlexibleItemDecoration(getContext())
//                //.withDivider(R.drawable.div_thin)
//                .withDefaultDivider()
//                .withDrawOver(true));

        mRvAdapter = new UserHistoryRvAdapter(itemList);
        mRvAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mRvAdapter.isFirstOnly(true);
        mRvAdapter.setNotDoAnimationCount(3);
        mRvAdapter.setEmptyView(emptyView);
        mRvAdapter.setEnableLoadMore(true);
        mRvAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                int offset = mRvAdapter.getLoadMoreViewPosition();
                mPresenter.onLoadData(mLimit, offset);
            }
        });
        mRecyclerView.setAdapter(mRvAdapter);

        // --- ItemClick ---

        mRvAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                MultiItemEntity item = mRvAdapter.getItem(position);
                switch (adapter.getItemViewType(position)) {
                    case MultipleItem.USER_CONTENT:
                        mPresenter.openContentViewActivity((ContentHeaderModel)item, EntityType.POSTS_GENERAL, position);
                        break;
                    case MultipleItem.USER_COMMENT:
                        break;
                }
            }
        });
    }

    public void onLoadFinishedData(List<? extends MultiItemEntity> items) {

        if ( items.size() > 0 ) {
            mRvAdapter.loadMoreComplete();
        } else {
            mRvAdapter.loadMoreEnd();
        }

        mRvAdapter.addData(items);
        mCurrentCounter = mRvAdapter.getData().size();
        hideLoading();
    }
}

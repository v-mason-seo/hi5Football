package com.ddastudio.hifivefootball_android.content_issue;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.EntityType;
import com.ddastudio.hifivefootball_android.data.event.ContentListEvent;
import com.ddastudio.hifivefootball_android.data.model.IssueModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ddastudio.hifivefootball_android.ui.utils.CustomLoadMoreView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class IssueFragment extends BaseFragment {

    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.issue_list) RecyclerView rvList;

    int mLimit;
    IssuePresenter mPresenter;
    IssueRvAdapter mRvAdapter;

    public IssueFragment() {
        // Required empty public constructor
    }

    public static IssueFragment newInstance(int issueType) {

        IssueFragment fragment = new IssueFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("ARGS_ISSUE_TYPE", issueType);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_issue, container, false);
        _unbinder = ButterKnife.bind(this, view);

        initRefresh();
        initRecyclerView();

        return view;
    }

    /**
     *
     * @param view onCreateView() 에서 리턴해준 View
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int issueType = getArguments().getInt("ARGS_ISSUE_TYPE", EntityType.ISSUE_TYPE_OPEN);
        // 한번에 조회할 데이터 개수
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        mLimit = Integer.valueOf(pref.getString("limit_count", "20"));

        mPresenter = new IssuePresenter(issueType);
        mPresenter.attachView(this);

        mPresenter.getIssueBoardContentList(mLimit, 0);
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

    @Override
    protected void onEvent(Object event) {
        super.onEvent(event);

        //Timber.i("[onEvent] issue event : %s", event.toString());

        if ( event instanceof ContentListEvent.RefreshListEvent) {
            refreshData();
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
        Toasty.error(getContext(), errMessage, Toast.LENGTH_SHORT).show();
    }

    /*---------------------------------------------------------------------------------------------*/

    public void initRefresh() {

        swipeRefreshLayout.setColorSchemeResources(
                R.color.progress_color1,
                R.color.progress_color2,
                R.color.progress_color3,
                R.color.progress_color4 );

        swipeRefreshLayout.setOnRefreshListener( () -> refreshData());
    }

    private void initRecyclerView() {

        List<IssueModel> itemList = new ArrayList<>();

        mRvAdapter = new IssueRvAdapter(itemList);
        mRvAdapter.setLoadMoreView(new CustomLoadMoreView());
//        mRvAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
//        mRvAdapter.isFirstOnly(true);
//        mRvAdapter.setNotDoAnimationCount(3);
        mRvAdapter.setEnableLoadMore(true);
        mRvAdapter.addHeaderView(getHeaderView());

        mRvAdapter.setOnLoadMoreListener( () -> {
            mPresenter.getIssueBoardContentList(mLimit, mRvAdapter.getLoadMoreViewPosition());
        });

        // --- ItemClick ---
        mRvAdapter.setOnItemClickListener((adapter, view, position) -> {
            IssueModel item = (IssueModel)mRvAdapter.getItem(position);
            mPresenter.openContentViewActivity(item.getContent(), position);
        });

        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.addItemDecoration(new FlexibleItemDecoration(getContext()).withDefaultDivider().withDrawOver(true));
        rvList.setAdapter(mRvAdapter);
    }

    private View getHeaderView() {
        View view = getActivity().getLayoutInflater()
                .inflate(R.layout.recycler_scrollable_usecase_item, (ViewGroup) rvList.getParent(), false);

        TextView tvTitle = view.findViewById(R.id.title);
        TextView tvSubTitle = view.findViewById(R.id.subtitle);
        String title = getResources().getString(R.string.issue_header_title);
        String noti = getResources().getString(R.string.issue_header_description);
        tvTitle.setText(title);
        tvSubTitle.setText(Html.fromHtml(noti));

        ImageView dismiss = view.findViewById(R.id.dismiss_icon);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( mRvAdapter != null ) {
                    mRvAdapter.removeHeaderView(view);
                }
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toasty.info(getContext(), "Header click", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public void refreshData() {

        mRvAdapter.getData().clear();
        mPresenter.getIssueBoardContentList(mLimit, 0);
    }

    public void onLoadFinished(List<IssueModel> items) {

        if ( items.size() > 0 ) {
            mRvAdapter.loadMoreComplete();
        } else {
            mRvAdapter.loadMoreEnd(true);
            mRvAdapter.enableLoadMoreEndClick(true);
        }

        mRvAdapter.addData(items);
        swipeRefreshLayout.setRefreshing(false);
    }
}

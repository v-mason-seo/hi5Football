package com.ddastudio.hifivefootball_android.my_news;


import android.os.Bundle;
import android.os.SystemClock;
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

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.community.NotificationModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ddastudio.hifivefootball_android.ui.utils.CustomLoadMoreView;
import com.ddastudio.hifivefootball_android.ui.utils.SimpleDividerItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 * 나의 소식정보를 보여주는 프래그먼트
 * 화면이 보일때마다 데이터를 리프레쉬 해줘야 한다.
 */
public class MyNewsFragment extends BaseFragment {

    // 읽지 않은 소식
    public final static int NOTIFICATION_TYPE_UNREAD = 4564;
    // 읽은 소식
    public final static int NOTIFICATION_TYPE_READ = 4565;

    private static final String LIST_STATE = "listState";

    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.my_news_list) RecyclerView rvList;
    View emptyFooterView;

    long mLastClickTime = 0;
    boolean isViewShown = false;
    NotificationRvAdapter mRvAdapter;
    MyNewsPresenter mPresenter;

    public MyNewsFragment() {
        // Required empty public constructor
    }

    public static MyNewsFragment newInstance(int notificationType) {
        MyNewsFragment fragmentFirst = new MyNewsFragment();
        Bundle args = new Bundle();
        args.putInt("ARGS_NOTIFICATION_TYPE", notificationType);
        fragmentFirst.setArguments(args);

        return fragmentFirst;
    }

    /**
     * 프래그먼트 최초 호출시 onCreateView() 보다 먼저 호출되기 때문에 isViewShown 변수가 필요함.
     * https://stackoverflow.com/questions/24161160/setuservisiblehint-called-before-oncreateview-in-fragment
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);

        if (getView() != null) {
            isViewShown = true;
            refreshData();
        } else {
            isViewShown = false;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int notificationType = getArguments().getInt("ARGS_NOTIFICATION_TYPE");
        mPresenter = new MyNewsPresenter(notificationType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_news, container, false);
        _unbinder = ButterKnife.bind(this, view);
        mPresenter.attachView(this);

        initEmptyView();
        initRefresh();
        initRecyclerView();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        // 프래그먼트 최초 호출시는 onViewCreated에서 데이터 조회를 하고
        // 이후부터는 setUserVisibleHint() 함수에서 데이터 조회를 한다.
        //
        if ( !isViewShown) {
            refreshData();
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        /*if (savedInstanceState != null) {
            listState = savedInstanceState.getParcelable(LIST_STATE);
        }*/
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        /*if ( linearLayoutManager != null ) {
            listState = linearLayoutManager.onSaveInstanceState();
            outState.putParcelable(LIST_STATE, listState);
        }*/
    }

    @Override
    public void onDestroyView() {
        mPresenter.detachView();
        super.onDestroyView();
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
        Toasty.error(getContext(), errMessage, Toast.LENGTH_LONG).show();
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

    private void initEmptyView() {
        // 라사이클뷰 하단에 빈공간을 삽입한다. - fab 중복을 피하기 위해서
        emptyFooterView = getActivity().getLayoutInflater().inflate(R.layout.row_empty_normal_footer_view, (ViewGroup)rvList.getParent(), false);
    }

    private void initRecyclerView() {

        mRvAdapter = new NotificationRvAdapter(R.layout.row_notification_item, mPresenter.getType());
        mRvAdapter.addFooterView(emptyFooterView);
        mRvAdapter.setLoadMoreView(new CustomLoadMoreView());
        mRvAdapter.addHeaderView(getHeaderView());

        // --- ItemClick ---
        mRvAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                switch (view.getId()) {
                    case R.id.notification_container:
                        onRvClickRow((NotificationModel)adapter.getItem(position), position);
                        break;

                    case R.id.notification_confirm:
                        onRvClickNotiConfirm(position);
                        break;
                }
            }
        });

        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.addItemDecoration(new SimpleDividerItemDecoration(getContext(), 60));
        rvList.setAdapter(mRvAdapter);
    }

    /**
     * 아이템 로우 클릭
     * 해당 컨턴츠로 이동하고 읽음 처리한다.
     * @param item
     * @param position
     */
    private void onRvClickRow(NotificationModel item, int position) {

        // 버튼 연속 클릭 방지
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        if ( item.getConfirm() > 0 ) {
            //
            // Contents 데이터를 읽고 뷰어 액티비티 실행
            //
            if ( item != null && item.validate()) {
                mPresenter.onLoadContentData(item.getTarget().getContent_id());
            }
        } else {
            //
            // 1. 내소식 읽음 처리
            // 2. Contents 데이터를 읽고 뷰어 액티비티 실행
            //
            if ( item != null && item.validate()) {
                mPresenter.confirmAndExecuteActivity(item.getNotificationTypeId(), item.getTarget().getContent_id(), position);
            } else {
                mPresenter.confirmNotification(item.getNotificationId(), position);
            }

        }
    }


    /**
     *
     * @param position
     */
    private void onRvClickNotiConfirm(int position) {
        NotificationModel confirm = mRvAdapter.getItem(position);
        if ( confirm !=  null ) {
            mPresenter.confirmNotification(confirm.getNotificationId(), position);
        }
    }


    private View getHeaderView() {
        View view = getActivity().getLayoutInflater()
                .inflate(R.layout.recycler_scrollable_usecase_item, (ViewGroup) rvList.getParent(), false);

        TextView tvTitle = view.findViewById(R.id.title);
        TextView tvSubTitle = view.findViewById(R.id.subtitle);

        String title;
        String noti;
        if (App.getAccountManager().isAuthorized()) {
            if ( mPresenter.getType() == MyNewsFragment.NOTIFICATION_TYPE_UNREAD) {
                title = "새알림";
                noti = "새로 등록된 소식입니다.";
            } else {
                title = "확인한 알림";
                noti = "이미 확인한 소식입니다.";
            }
        } else {
            title = getResources().getString(R.string.notification_header_title);
            noti = getResources().getString(R.string.notification_header_description);
        }

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

                Toasty.info(getContext(), "Header click", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public void refreshData() {

        mRvAdapter.getData().clear();
        mPresenter.onLoadNotification(true);
    }

    /**
     * 알림 읽음 처리
     * @param position
     */
    public void onFinishedConfirm(int position) {

        if ( mRvAdapter != null ) {
            if ( mRvAdapter.getData().size() > position) {
                mRvAdapter.remove(position);
            }

        }
    }

    public void onLoadFinished(List<NotificationModel> items, boolean clear) {

//        if ( items.size() > 0 ) {
//            mRvAdapter.loadMoreComplete();
//        } else {
//            mRvAdapter.loadMoreEnd();
//            mRvAdapter.enableLoadMoreEndClick(true);
//        }

        if ( clear ) {
            mRvAdapter.setNewData(items);
        } else {
            mRvAdapter.addData(items);
        }

        /*// We're coming from a config change, so the state needs to be restored.
        if (listState != null) {
            linearLayoutManager.onRestoreInstanceState(listState);
            listState = null;
        }*/

        hideLoading();
    }
}

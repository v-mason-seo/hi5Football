package com.ddastudio.hifivefootball_android.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.ddastudio.hifivefootball_android.ui.presenter.ItemListDialogPresenter;
import com.ddastudio.hifivefootball_android.ui.rvadapter.ItemListRvAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration;

public class ItemListDialogFragment extends BottomSheetDialogFragment
        implements BaseContract.View {

    ItemListDialogPresenter mPresenter;
    ItemListRvAdapter mRvAdapter;
    Unbinder unbinder;
    @BindView(R.id.item_list) RecyclerView rvItemList;
    View emptyFooterView;

    // TODO: Customize parameters
    public static ItemListDialogFragment newInstance(int contentId, int type) {
        final ItemListDialogFragment fragment = new ItemListDialogFragment();
        final Bundle args = new Bundle();
        args.putInt("ARGS_CONTENT_ID", contentId);
        args.putInt("ARGS_TYPE", type);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_item_list_dialog, container, false);
        unbinder = ButterKnife.bind(this, view);

        if ( getArguments() == null ) {
            dismiss();
        }

        int content_id = getArguments().getInt("ARGS_CONTENT_ID");
        int data_type = getArguments().getInt("ARGS_TYPE");
        mPresenter = new ItemListDialogPresenter(content_id, data_type);
        mPresenter.attachView(this);

        initEmptyView();
        initRecyclerView();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onLoadData();
    }

    @Override
    public void onDestroyView() {
        if ( unbinder != null ) {
            unbinder.unbind();
        }
        mPresenter.detachView();

        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        //mListener = null;
        super.onDetach();
    }

    /*---------------------------------------------------------------------------------------------*/

    /*----------------------------------------
     * 엠프티뷰 생성
     *----------------------------------------*/
    private void initEmptyView() {

        // 라사이클뷰 하단에 빈공간을 삽입한다. - fab 중복을 피하기 위해서
        emptyFooterView = getActivity().getLayoutInflater().inflate(R.layout.row_empty_footer_view, (ViewGroup)rvItemList.getParent(), false);

    }

    private void initRecyclerView() {

        List<MultiItemEntity> itemList = new ArrayList<>();

        View emptyView = getActivity().getLayoutInflater().inflate(R.layout.recycler_empty_view, (ViewGroup) rvItemList.getParent(), false);
        mRvAdapter = new ItemListRvAdapter(itemList);
        mRvAdapter.addFooterView(emptyFooterView);
        mRvAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mRvAdapter.isFirstOnly(true);
        mRvAdapter.setNotDoAnimationCount(3);
        mRvAdapter.setEmptyView(emptyView);
        mRvAdapter.setEnableLoadMore(true);


        rvItemList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvItemList.addItemDecoration(new FlexibleItemDecoration(getContext()).withDefaultDivider().withDrawOver(true));
        rvItemList.setAdapter(mRvAdapter);
    }

    public void onLoadFinished(List<? extends MultiItemEntity> items) {

        mRvAdapter.getData().clear();
        mRvAdapter.addData(items);
    }


    /*---------------------------------------------------------------------------------------------*/
}

package com.ddastudio.hifivefootball_android.match_chat;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.match_chat.utils.KeyboardLayout;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class MatchChatFragment extends BaseFragment {

    @BindView(R.id.rv_list) RecyclerView rvList;

    View emptyFooterView;

    MatchChatRvAdapter mRvAdapter;

    public MatchChatFragment() {
        // Required empty public constructor
    }

    public static MatchChatFragment newInstance() {
        MatchChatFragment fragment = new MatchChatFragment();
        //Bundle args = new Bundle();
        //args.putParcelable("ARGS_MATCH", Parcels.wrap(matchData));
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_match_chat, container, false);
        _unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initEmptyView();
        initRecyclerView();

        mRvAdapter.addData(new MatchChatModel("== START == "));
    }

    /*---------------------------------------------------------------------------------------------*/

    @Override
    protected void onEvent(Object event) {
        super.onEvent(event);

        if ( event instanceof MatchChatModel) {

            if ( mRvAdapter != null ) {
                MatchChatModel matchChatModel = (MatchChatModel)event;
                mRvAdapter.addData(matchChatModel);
                // 아이템이 추가되고 맨 아래로 스크롤을 이동한다.
                rvList.scrollToPosition(mRvAdapter.getData().size() - 1);
            }
        }
    }

    @Override
    public void showLoading() {
        super.showLoading();
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
    }

    @Override
    public void showMessage(String message) {
        Toasty.normal(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showErrorMessage(String errMessage) {
        Toasty.error(getContext(), errMessage, Toast.LENGTH_LONG).show();
    }

    /*---------------------------------------------------------------------------------------------*/

    private void initEmptyView() {
        // 라사이클뷰 하단에 빈공간을 삽입한다. - fab 중복을 피하기 위해서
        emptyFooterView = getActivity().getLayoutInflater().inflate(R.layout.row_empty_footer_view, (ViewGroup)rvList.getParent(), false);
    }

    private void initRecyclerView() {

        mRvAdapter = new MatchChatRvAdapter(new ArrayList<>());
        //mRvAdapter.addFooterView(emptyFooterView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(true);
        rvList.setLayoutManager(linearLayoutManager);

        rvList.setAdapter(mRvAdapter);
    }
}

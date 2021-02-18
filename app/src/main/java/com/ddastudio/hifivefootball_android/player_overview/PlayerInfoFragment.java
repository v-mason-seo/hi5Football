package com.ddastudio.hifivefootball_android.player_overview;


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
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.PlayerStatisticsModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerInfoFragment extends BaseFragment {

    public static int TYPE_BASIC_INFO = 1350;
    public static int TYPE_STATISTICS = 1351;

    @BindView(R.id.player_info_list) RecyclerView rvPlayerInfo;

    PlayerInfoRvAdapter mRvAdapter;
    PlayerInfoPresenter mPresenter;

    public PlayerInfoFragment() {
        // Required empty public constructor
    }

    public static PlayerInfoFragment newInstance(int dataType, int playerId, String playerName) {
        PlayerInfoFragment fragmentFirst = new PlayerInfoFragment();
        Bundle args = new Bundle();
        args.putInt("ARGS_DATA_TYPE", dataType);
        args.putInt("ARGS_PLAYER_ID", playerId);
        args.putString("ARGS_PLAYER_NAME", playerName);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player_info, container, false);
        _unbinder = ButterKnife.bind(this, view);

        int dataType = getArguments().getInt("ARGS_DATA_TYPE");
        int playerId = getArguments().getInt("ARGS_PLAYER_ID");
        String playerName = getArguments().getString("ARGS_PLAYER_NAME");

        mPresenter = new PlayerInfoPresenter(dataType, playerId, playerName);
        mPresenter.attachView(this);

        initRecyclerView();
        mPresenter.onLoadPlayer();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /*---------------------------------------------------------------------------------------------*/

    private void initRecyclerView() {

        List<MultiItemEntity> itemList = new ArrayList<>();

        //View emptyView = getActivity().getLayoutInflater().inflate(R.layout.recycler_empty_view, (ViewGroup) rvPlayerInfo.getParent(), false);
        mRvAdapter = new PlayerInfoRvAdapter(itemList);
        mRvAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mRvAdapter.isFirstOnly(true);
        mRvAdapter.setNotDoAnimationCount(3);
        //mRvAdapter.setEmptyView(emptyView);

        rvPlayerInfo.setLayoutManager(new LinearLayoutManager(getContext()));
        //rvPlayerInfo.addItemDecoration(new FlexibleItemDecoration(getContext()).withDefaultDivider().withDrawOver(true));
        rvPlayerInfo.setAdapter(mRvAdapter);

    }

    public void onLoadFinished(PlayerModel items) {

        mRvAdapter.getData().clear();
        mRvAdapter.addData(items);
    }

    public void onLoadFinished2(List<PlayerStatisticsModel> items) {

        mRvAdapter.getData().clear();
        mRvAdapter.addData(items);
    }

}

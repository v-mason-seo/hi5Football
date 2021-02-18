package com.ddastudio.hifivefootball_android.team_overview;


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
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamStatItemModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeamInfoFragment extends BaseFragment {

    public static int TYPE_BASIC_INFO = 1450;
    public static int TYPE_STATISTICS = 1451;

    @BindView(R.id.team_info_list) RecyclerView rvTeamInfo;

    int mDataType;
    TeamModel mTeamData;
    TeamInfoRvAdapter mRvAdapter;


    public static TeamInfoFragment newInstance(int dataType, TeamModel teamData) {
        TeamInfoFragment fragmentFirst = new TeamInfoFragment();
        Bundle args = new Bundle();
        args.putInt("ARGS_DATA_TYPE", dataType);
        args.putParcelable("ARGS_TEAM_DATA", Parcels.wrap(teamData));
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public TeamInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_team_info, container, false);
        _unbinder = ButterKnife.bind(this, view);

        mDataType = getArguments().getInt("ARGS_DATA_TYPE");
        mTeamData = Parcels.unwrap(getArguments().getParcelable("ARGS_TEAM_DATA"));

        initRecyclerView();
        setTeamData();

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
        mRvAdapter = new TeamInfoRvAdapter(itemList);
        mRvAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mRvAdapter.isFirstOnly(true);
        mRvAdapter.setNotDoAnimationCount(3);
        //mRvAdapter.setEmptyView(emptyView);

        rvTeamInfo.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTeamInfo.addItemDecoration(new FlexibleItemDecoration(getContext()).withDefaultDivider().withDrawOver(true));
        rvTeamInfo.setAdapter(mRvAdapter);

    }

    private void setTeamData() {

        if ( mTeamData == null )
            return;

        if ( mDataType == TYPE_BASIC_INFO ) {
            mRvAdapter.addData(mTeamData);
        } else if ( mDataType == TYPE_STATISTICS ) {

            if ( mTeamData.getStatistics().size() > 0 ) {
                List<TeamStatItemModel> statList = mTeamData.getStatistics().get(0).generatorTeamStatItemList();
                mRvAdapter.addData(statList);
            }
        }
    }

}

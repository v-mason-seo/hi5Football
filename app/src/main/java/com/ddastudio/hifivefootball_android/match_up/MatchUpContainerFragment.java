package com.ddastudio.hifivefootball_android.match_up;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class MatchUpContainerFragment extends BaseFragment {

    @BindView(R.id.tabs) SmartTabLayout mTabLayout;
    @BindView(R.id.viewpager) ViewPager mViewPager;
    @BindArray(R.array.match_up_container_tabs_array) String[] mVpTitles;

    MatchUpVpAdapter mVpAdapter;

    public MatchUpContainerFragment() {
        // Required empty public constructor
    }

    public static MatchUpContainerFragment newInstance() {
        MatchUpContainerFragment fragmentFirst = new MatchUpContainerFragment();
        return fragmentFirst;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_match_up_container, container, false);
        _unbinder = ButterKnife.bind(this, view);

        initViewPager();

        return view;
    }

    /*---------------------------------------------------------------------------------------------*/

    private void initViewPager() {

        mVpAdapter = new MatchUpVpAdapter(getChildFragmentManager(), mVpTitles);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mVpAdapter);
        mTabLayout.setViewPager(mViewPager);
    }
}

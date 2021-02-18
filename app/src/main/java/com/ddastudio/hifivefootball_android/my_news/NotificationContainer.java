package com.ddastudio.hifivefootball_android.my_news;


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
public class NotificationContainer extends BaseFragment {

    @BindView(R.id.tabs_notification_container)
    SmartTabLayout mTabLayout;

    @BindView(R.id.vp_notification_container)
    ViewPager mViewPager;

    @BindArray(R.array.notification_tabs_array) String[] mVpTitles;

    NotificationVpAdapter mVpAdapter;

    public NotificationContainer() {
        // Required empty public constructor
    }

    public static NotificationContainer newInstance() {
        NotificationContainer fragmentFirst = new NotificationContainer();
        //Bundle args = new Bundle();
        //args.putInt("ARGS_BOARD_TYPE", boardType);
        //fragmentFirst.setArguments(args);
        return fragmentFirst;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_container, container, false);
        _unbinder = ButterKnife.bind(this, view);

        initViewPager();

        return view;
    }

    /*---------------------------------------------------------------------------------------------*/

    private void initViewPager() {

        mVpAdapter = new NotificationVpAdapter(getChildFragmentManager(), mVpTitles);
        mViewPager.setAdapter(mVpAdapter);
        mTabLayout.setViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}

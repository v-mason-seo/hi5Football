package com.ddastudio.hifivefootball_android.nunting_bset;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.event.SelectedSiteEvent;
import com.ddastudio.hifivefootball_android.nunting_bset.model.SiteBoardModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NuntingPostsContainerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NuntingPostsContainerFragment extends BaseFragment {

    @BindView(R.id.tabPostsContainer)
    SmartTabLayout mTabs;
    @BindView(R.id.vpPostsContainer)
    ViewPager mViewpager;

    NuntingPostContainerVpAdapter mVpAdapter;

    public NuntingPostsContainerFragment() {
        // Required empty public constructor
    }


    public static NuntingPostsContainerFragment newInstance() {
        NuntingPostsContainerFragment fragment = new NuntingPostsContainerFragment();
        Bundle args = new Bundle();

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
        View view =  inflater.inflate(R.layout.fragment_nunting_posts_container, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    /*---------------------------------------------------------------------------------------------*/

    @Override
    protected void onEvent(Object event) {
        super.onEvent(event);

        if ( event instanceof SelectedSiteEvent) {
            initViewPager(((SelectedSiteEvent) event).getSiteInfo());
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
        super.showMessage(message);
        Toasty.normal(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorMessage(String errMessage) {
        super.showErrorMessage(errMessage);
        Toasty.error(getContext(), errMessage, Toast.LENGTH_LONG).show();
    }

    /*---------------------------------------------------------------------------------------------*/

    private void initViewPager(SiteBoardModel siteInfo) {

        mVpAdapter = new NuntingPostContainerVpAdapter(getChildFragmentManager(), siteInfo);
        mViewpager.setAdapter(mVpAdapter);
        mTabs.setViewPager(mViewpager);
    }

}

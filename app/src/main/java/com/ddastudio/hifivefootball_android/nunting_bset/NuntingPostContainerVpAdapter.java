package com.ddastudio.hifivefootball_android.nunting_bset;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ddastudio.hifivefootball_android.nunting_bset.model.SiteBoardModel;

public class NuntingPostContainerVpAdapter extends FragmentStatePagerAdapter {

    SiteBoardModel mSiteBoard;

    public NuntingPostContainerVpAdapter(FragmentManager fm, SiteBoardModel siteInfoModel) {
        super(fm);
        mSiteBoard = siteInfoModel;
    }

    @Override
    public Fragment getItem(int position) {
        return NuntingPostListFragment.newInstance(mSiteBoard.getName(), mSiteBoard.getBoards().get(position));
    }

    @Override
    public int getCount() {
        return mSiteBoard.getBoards().size();
    }

    @Override
    public CharSequence getPageTitle(int position) {

        //return mItems.get(position).getBoardName();
        return mSiteBoard.getBoards().get(position).getBnm();
    }
}

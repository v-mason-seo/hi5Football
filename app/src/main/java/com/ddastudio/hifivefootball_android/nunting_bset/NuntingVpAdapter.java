package com.ddastudio.hifivefootball_android.nunting_bset;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ddastudio.hifivefootball_android.nunting_bset.NuntingPostsContainerFragment;
import com.ddastudio.hifivefootball_android.nunting_bset.NuntingSiteListFragment;

public class NuntingVpAdapter extends FragmentStatePagerAdapter {

    String[] mTabs;

    public NuntingVpAdapter(FragmentManager fm, String[] tabs) {
        super(fm);
        this.mTabs = tabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return NuntingSiteListFragment.newInstance();
            case 1:
                return NuntingPostsContainerFragment.newInstance();
        }

        return null;
    }

    @Override
    public int getCount() {
        return mTabs.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs[position];
    }
}

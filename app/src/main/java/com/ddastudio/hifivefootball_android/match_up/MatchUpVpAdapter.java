package com.ddastudio.hifivefootball_android.match_up;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ddastudio.hifivefootball_android.match_up.MatchUpFragment;

/**
 * Created by hongmac on 2018. 2. 22..
 */

public class MatchUpVpAdapter extends FragmentPagerAdapter {

    String[] mTabs;

    public MatchUpVpAdapter(FragmentManager fm, String[] tabs) {
        super(fm);
        this.mTabs = tabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MatchUpFragment.newInstance(MatchUpFragment.FINISH_RECENT_MATCH);
            case 1:
                return MatchUpFragment.newInstance(MatchUpFragment.LIVE_MATCH);
            case 2:
                return MatchUpFragment.newInstance(MatchUpFragment.COME_MATCH);
//            case 2:
//                return MatchUpFragment.newInstance(MatchUpFragment.TOMORROW_MATCH);
//            case 3:
//                return MatchUpFragment.newInstance(MatchUpFragment.POPULAR_MATCH);
            default:
                return null;
        }
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

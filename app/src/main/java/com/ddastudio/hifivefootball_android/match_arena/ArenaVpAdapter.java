package com.ddastudio.hifivefootball_android.match_arena;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.match_lineup.LineupGridFragment;

/**
 * Created by hongmac on 2018. 1. 25..
 */

public class ArenaVpAdapter extends FragmentPagerAdapter {

    String[] mTabs;
    MatchModel mMatch;

    public ArenaVpAdapter(FragmentManager fm, String[] tabs, MatchModel match) {
        super(fm);
        this.mTabs = tabs;
        this.mMatch = match;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ArenaChatFragment.newInstance(mMatch);
            case 1:
                return LineupGridFragment.newInstance(mMatch);
                //return LineupFragment.newInstance(mMatch);
//            case 2:
//                return LineupGridFragment.newInstance(mMatch);
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

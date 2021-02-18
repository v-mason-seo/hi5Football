package com.ddastudio.hifivefootball_android.competition;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ddastudio.hifivefootball_android.data.model.football.CompetitionModel;

/**
 * Created by hongmac on 2017. 10. 27..
 */

public class CompetitionVpAdapter extends FragmentPagerAdapter {

    String[] mTabs;
    CompetitionModel mCompData;

    public CompetitionVpAdapter(FragmentManager fm, String[] tabs, CompetitionModel competition) {
        super(fm);
        this.mTabs = tabs;
        this.mCompData = competition;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return CompetitionInfoFragment.newInstance(CompetitionInfoFragment.TYPE_MATCHES, mCompData.getCompetitionFaId());
            case 1:
                return CompetitionInfoFragment.newInstance(CompetitionInfoFragment.TYPE_STANDINGS, mCompData.getCompetitionFaId());
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
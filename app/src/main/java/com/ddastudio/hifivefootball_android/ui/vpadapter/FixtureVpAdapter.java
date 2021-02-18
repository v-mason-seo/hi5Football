package com.ddastudio.hifivefootball_android.ui.vpadapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


import com.ddastudio.hifivefootball_android.match_schedule.MatchScheduleFragment;
import com.ddastudio.hifivefootball_android.utils.TimeUtils;

import java.util.Calendar;

/**
 * Created by hongmac on 2018. 2. 3..
 */

public class FixtureVpAdapter extends FragmentStatePagerAdapter {

    public FixtureVpAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        long timeForPosition = TimeUtils.getDayForPosition(position).getTimeInMillis();
        return MatchScheduleFragment.newInstance(timeForPosition);
    }

    @Override
    public int getCount() {
        return TimeUtils.DAYS_OF_TIME;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Calendar cal = TimeUtils.getDayForPosition(position);
        return TimeUtils.getFormattedDate(cal.getTimeInMillis());
    }


}

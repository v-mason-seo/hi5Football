package com.ddastudio.hifivefootball_android.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ddastudio.hifivefootball_android.common.PostBoardType;
import com.ddastudio.hifivefootball_android.content_list.ContentListContainerFragment;
import com.ddastudio.hifivefootball_android.match_summery.MainMatchFragment;
import com.ddastudio.hifivefootball_android.match_up.MatchUpContainerFragment;
import com.ddastudio.hifivefootball_android.my_news.NotificationContainer;

/**
 * Created by hongmac on 2017. 9. 4..
 */

public class MainVpAdapter extends FragmentPagerAdapter {

    String[] mTabs;
    PostBoardType mBoardType;

    public MainVpAdapter(FragmentManager fm, String[] tabs, PostBoardType boardType) {
        super(fm);
        this.mTabs = tabs;
        this.mBoardType = boardType;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return ContentListContainerFragment.newInstance();
            case 1:
                return MatchUpContainerFragment.newInstance();
            case 2:
                return MainMatchFragment.newInstance();
//            case 3:
//                return MatchScheduleFragment.newInstance();
            case 3:
                return NotificationContainer.newInstance();
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

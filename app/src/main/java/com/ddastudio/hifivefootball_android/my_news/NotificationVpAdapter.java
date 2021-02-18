package com.ddastudio.hifivefootball_android.my_news;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by hongmac on 2017. 12. 4..
 */

public class NotificationVpAdapter extends FragmentPagerAdapter {

    String[] mTabs;

    public NotificationVpAdapter(FragmentManager fm, String[] tabs) {
        super(fm);
        this.mTabs = tabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MyNewsFragment.newInstance(MyNewsFragment.NOTIFICATION_TYPE_UNREAD);
            case 1:
                return MyNewsFragment.newInstance(MyNewsFragment.NOTIFICATION_TYPE_READ);
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

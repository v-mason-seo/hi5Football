package com.ddastudio.hifivefootball_android.user_profile;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ddastudio.hifivefootball_android.data.model.UserModel;

/**
 * Created by hongmac on 2017. 9. 20..
 */

public class UserProfileVpAdapter extends FragmentPagerAdapter {

    String[] mTabs;
    UserModel mUser;

    public UserProfileVpAdapter(FragmentManager fm, String[] tabs, UserModel user) {
        super(fm);
        this.mTabs = tabs;
        this.mUser = user;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return UserHistoryFragment.newInstance(mUser, UserHistoryFragment.TYPE_CONTENT);
            case 1:
                return UserHistoryFragment.newInstance(mUser, UserHistoryFragment.TYPE_COMMENT);
            case 2:
                return UserHistoryFragment.newInstance(mUser, UserHistoryFragment.TYPE_HIFIVE);
            case 3:
                return UserHistoryFragment.newInstance(mUser, UserHistoryFragment.TYPE_SCRAP);
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

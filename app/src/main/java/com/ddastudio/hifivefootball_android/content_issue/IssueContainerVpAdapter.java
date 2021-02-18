package com.ddastudio.hifivefootball_android.content_issue;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ddastudio.hifivefootball_android.common.EntityType;
import com.ddastudio.hifivefootball_android.content_issue.IssueFragment;

/**
 * Created by hongmac on 2017. 9. 25..
 */

public class IssueContainerVpAdapter extends FragmentPagerAdapter {

    String[] mTabs;

    public IssueContainerVpAdapter(FragmentManager fm, String[] tabs) {
        super(fm);
        this.mTabs = tabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return IssueFragment.newInstance(EntityType.ISSUE_TYPE_OPEN);
            case 1:
                return IssueFragment.newInstance(EntityType.ISSUE_TYPE_CLOSE);
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

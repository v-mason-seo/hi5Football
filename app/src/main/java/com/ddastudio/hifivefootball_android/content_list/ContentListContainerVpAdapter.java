package com.ddastudio.hifivefootball_android.content_list;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ddastudio.hifivefootball_android.board.BoardListFragment;
import com.ddastudio.hifivefootball_android.board.model.BoardMasterModel;
import com.ddastudio.hifivefootball_android.football_chat.FootballChatListFragment;

import java.util.List;

/**
 * Created by hongmac on 2017. 9. 4..
 */

public class ContentListContainerVpAdapter extends FragmentPagerAdapter {

    // 1.게시판
    // 2.게시글
    // R.array.content_list_container_tabs_array
    String[] mTabs;

    public ContentListContainerVpAdapter(FragmentManager fm, String[] tabs) {
        super(fm);
        this.mTabs = tabs;
    }

    @Override
    public Fragment getItem(int position) {

        if ( position == 0 ) {
            return FootballChatListFragment.newInstance();
        } else {
            return ContentListFragment.newInstance();
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
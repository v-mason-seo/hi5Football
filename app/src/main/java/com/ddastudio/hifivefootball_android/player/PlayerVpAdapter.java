package com.ddastudio.hifivefootball_android.player;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.board.model.BoardMasterModel;
import com.ddastudio.hifivefootball_android.common.PostBoardType;
import com.ddastudio.hifivefootball_android.common.PostType;
import com.ddastudio.hifivefootball_android.content_list.ContentListFragment;
import com.ddastudio.hifivefootball_android.content_list.FootballRelatedContentListFragment;
import com.ddastudio.hifivefootball_android.player_overview.PlayerInfoFragment;

/**
 * Created by hongmac on 2017. 10. 29..
 */

public class PlayerVpAdapter extends FragmentPagerAdapter {

    String[] mTabs;
    int mPlayerId;
    String mPlayerName;

    public PlayerVpAdapter(FragmentManager fm, String[] tabs, int playerId, String playerName) {
        super(fm);
        this.mTabs = tabs;
        this.mPlayerId = playerId;
        this.mPlayerName = playerName;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                int mode = FootballRelatedContentListFragment.PLAYER_RELATED_CONTENT;
                return FootballRelatedContentListFragment.newInstance(mode, mPlayerId);
            case 1:
                return PlayerInfoFragment.newInstance(PlayerInfoFragment.TYPE_BASIC_INFO, mPlayerId, mPlayerName);
            case 2:
                return PlayerInfoFragment.newInstance(PlayerInfoFragment.TYPE_STATISTICS, mPlayerId, mPlayerName);
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

package com.ddastudio.hifivefootball_android.team;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.content_list.FootballRelatedContentListFragment;
import com.ddastudio.hifivefootball_android.board.model.BoardMasterModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;
import com.ddastudio.hifivefootball_android.match_schedule.MatchScheduleFragment;
import com.ddastudio.hifivefootball_android.player_list.PlayerListFragment;
import com.ddastudio.hifivefootball_android.team_overview.TeamInfoFragment;

/**
 * Created by hongmac on 2017. 10. 30..
 */

public class TeamVpAdapter extends FragmentPagerAdapter {

    TeamModel mTeamData;
    String[] mTabs;

    public TeamVpAdapter(FragmentManager fm, String[] tabs, TeamModel teamData) {
        super(fm);
        this.mTabs = tabs;
        this.mTeamData = teamData;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                int mode = FootballRelatedContentListFragment.TEAM_RELATED_CONTENT;
                int teamId = mTeamData.getTeamId();
                return FootballRelatedContentListFragment.newInstance(mode, teamId);
            case 1:
                return TeamInfoFragment.newInstance(TeamInfoFragment.TYPE_BASIC_INFO, mTeamData);
            case 2:
                return MatchScheduleFragment.newInstance(-1, mTeamData.getTeamId());
            case 3:
                return PlayerListFragment.newInstance(PlayerListFragment.TYPE_TEAM_PLAYER_LIST, mTeamData.getTeamId());
            case 4:
                return TeamInfoFragment.newInstance(TeamInfoFragment.TYPE_STATISTICS, mTeamData);
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

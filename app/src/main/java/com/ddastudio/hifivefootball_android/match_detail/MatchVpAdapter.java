package com.ddastudio.hifivefootball_android.match_detail;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ddastudio.hifivefootball_android.common.PostBoardType;
import com.ddastudio.hifivefootball_android.common.PostType;
import com.ddastudio.hifivefootball_android.content_list.FootballRelatedContentListFragment;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.content_list.ContentListFragment;
import com.ddastudio.hifivefootball_android.ui.fragment.LeagueTableFragment;
import com.ddastudio.hifivefootball_android.match_lineup.LineupGridFragment;
import com.ddastudio.hifivefootball_android.match_event.MatchTimeLineFragment;
import com.ddastudio.hifivefootball_android.match_overview.MatchOverviewFragment;
import com.ddastudio.hifivefootball_android.player_comment.PlayerCommentFragment;

/**
 * Created by hongmac on 2017. 10. 26..
 */

public class MatchVpAdapter extends FragmentPagerAdapter {

    String[] mTabs;
    MatchModel mMatchData;

    public MatchVpAdapter(FragmentManager fm, String[] tabs, MatchModel matchData) {
        super(fm);
        this.mTabs = tabs;
        this.mMatchData = matchData;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                // 관련글은 좀 더 고민하고 넣자
                //return ContentListFragment.newInstance(PostBoardType.ALL_BOARD.value(), PostType.MATCH_RELATION.value(), mMatchData.getMatchId());
                int mode = FootballRelatedContentListFragment.MATCH_RELATED_CONTENT;
                int matchId = mMatchData.getMatchId();
                return FootballRelatedContentListFragment.newInstance(mode, matchId);
            case 1:
                return MatchOverviewFragment.newInstance(mMatchData);
            case 2:
                return LineupGridFragment.newInstance(mMatchData);
            case 3:
                return PlayerCommentFragment.newInstance(mMatchData);
            case 4:
                return LeagueTableFragment.newInstance(mMatchData.getCompetitionId(), mMatchData.getHomeTeamId(), mMatchData.getAwayTeamId());
            case 5:
                return MatchTimeLineFragment.newInstance(mMatchData);
        }

        return null;
    }

    @Override
    public int getCount() {
        return mTabs.length;
        //return 6;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs[position];
    }
}

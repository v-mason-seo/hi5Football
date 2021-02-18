package com.ddastudio.hifivefootball_android.player_rating;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.match_lineup.LineupFragment;
import com.ddastudio.hifivefootball_android.match_lineup.LineupGridFragment;

import java.util.List;

/**
 * Created by hongmac on 2018. 1. 5..
 */

public class PlayerRatingVpAdapter extends FragmentPagerAdapter  {

    int matchId;
    int selectedTeamId;
    int selectedPlayerId;
    String homeTeamUrl;
    String awayTeamUrl;
    MatchModel matchData;

    List<PlayerModel> homeLineupList;
    List<PlayerModel> awayLineupList;

    public PlayerRatingVpAdapter(FragmentManager fm,
                                 MatchModel matchData,
                                 int selectedTeamId,
                                 int selectedPlayerId) {
        super(fm);

        this.matchData = matchData;
        this.selectedTeamId = selectedTeamId;
        this.selectedPlayerId = selectedPlayerId;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return PlayerRatingListFragment.newInstance(matchData, true, selectedTeamId, selectedPlayerId);
            case 1:
                return PlayerRatingListFragment.newInstance(matchData, false, selectedTeamId, selectedPlayerId);
                //return PlayerCommentFragment.newInstance(matchData);
            case 2:
                return LineupGridFragment.newInstance(matchData);
            case 3:
                return LineupFragment.newInstance(matchData);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "A";
            case 1:
                return "B";
            default:
                return "";
        }
    }
}

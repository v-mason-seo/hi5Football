package com.ddastudio.hifivefootball_android.data.event;

import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;

/**
 * Created by hongmac on 2018. 1. 3..
 */

public class MatchEvent {

    /**
     * 글쓰기에서 경기 선택을 완료했을 때 발생하는 이벤트
     */
    public static class ChoiceMatchEvent {

        MatchModel matchData;

        public ChoiceMatchEvent(MatchModel match) {
            this.matchData = match;
        }

        public MatchModel getMatchData() {
            return matchData;
        }

        public void setMatchData(MatchModel matchData) {
            this.matchData = matchData;
        }

        public String getMatchTag() {

            if ( matchData != null ) {
                return "M"+matchData.getMatchId();
            }

            return "";
        }
    }
}

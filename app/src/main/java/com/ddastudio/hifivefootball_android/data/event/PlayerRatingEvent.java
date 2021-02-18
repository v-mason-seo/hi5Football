package com.ddastudio.hifivefootball_android.data.event;

/**
 * Created by hongmac on 2018. 2. 15..
 */

public class PlayerRatingEvent {

    public static class ChoiceTeam {

        boolean isHomeTeam = true;

        public ChoiceTeam(boolean isHomeTeam) {
            this.isHomeTeam = isHomeTeam;
        }

        public boolean isHomeTeam() {
            return isHomeTeam;
        }
    }

    public static class ShowPlayerComment {

        int matchId;
        int playerId;

        public ShowPlayerComment(int matchId, int playerId) {
            this.matchId = matchId;
            this.playerId = playerId;
        }

        public int getPlayerId() {
            return playerId;
        }

        public void setPlayerId(int playerId) {
            this.playerId = playerId;
        }

        public int getMatchId() {
            return matchId;
        }

        public void setMatchId(int matchId) {
            this.matchId = matchId;
        }
    }
}

package com.ddastudio.hifivefootball_android.data.model.footballdata;

import android.text.TextUtils;

import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.google.gson.annotations.SerializedName;

import org.w3c.dom.Text;

/**
 * Created by hongmac on 2017. 10. 27..
 */

public class MatchEventModel extends MultipleItem {

    @SerializedName("match_id") int matchId;
    @SerializedName("match_fa_id") int matchFaId;
    @SerializedName("match_event_id") int matchEventId;
    @SerializedName("team_type") String teamType;
    @SerializedName("event_type") String eventType;
    @SerializedName("minute") String minute;
    @SerializedName("extra_min") String extraMin;
    @SerializedName("player_id") int playerId;
    @SerializedName("player_name") String playerName;
    @SerializedName("assist_id") int assistId;
    @SerializedName("assist_player_name") String assistPlayerName;
    @SerializedName("score_result") String scoreResult;

    @Override
    public int getItemType() {

        if ( teamType.equals("localteam")) {
            return MultipleItem.MATCH_EVENTS_LOCAL_TEAM;
        }

        return MultipleItem.MATCH_EVENTS_VISITOR_TEAM;
    }

    public int getMatchId() {
        return matchId;
    }

    public int getMatchFaId() {
        return matchFaId;
    }

    public int getMatchEventId() {
        return matchEventId;
    }

    public String getTeamType() {
        return teamType;
    }

    public String getEventType() {
        return eventType;
    }

    public String getMinute() {

        if ( minute == null ) return "";
        return minute + "'";
    }

    public String getExtraMin() {
        return TextUtils.isEmpty(extraMin) ? "" : "+" + extraMin + "'";
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getAssistId() {
        return assistId;
    }

    public String getAssistPlayerName() {
        return assistPlayerName;
    }

    public String getAssistPlayerName2() {

        if (!TextUtils.isEmpty(assistPlayerName)) {

            if ( eventType.equals("goal")) {
                return "어시스트: " + assistPlayerName;
            } else if ( eventType.equals("subst")) {
                return "교체: " + assistPlayerName;
            }
        }

        return assistPlayerName;
    }

    public String getScoreResult() {
        return TextUtils.isEmpty(scoreResult) ? "" : scoreResult;
    }
}

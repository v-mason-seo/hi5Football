package com.ddastudio.hifivefootball_android.data.model.convert;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hongmac on 2017. 10. 13..
 */

public class FixtureModel {

    @SerializedName("_links") Links _links;
    @SerializedName("date") Date date;
    @SerializedName("status") String status;
    @SerializedName("matchday") int matchday;
    @SerializedName("homeTeamName") String homeTeamName;
    @SerializedName("awayTeamName") String awayTeamName;
    @SerializedName("result")  Result result;

    public int getFixtureId() {

        String url = _links.getSelfUrl();
        String[] urls = url.split("/");
        int lastIndex = urls.length;

        return Integer.valueOf(urls[lastIndex-1]);
    }

    public int getCompetitionId() {

        String url = _links.getCompetitionUrl();
        String[] urls = url.split("/");
        int lastIndex = urls.length;

        return Integer.valueOf(urls[lastIndex-1]);
    }

    public int getHomeTeamId() {

        String url = _links.getHomeTeamUrl();
        String[] urls = url.split("/");
        int lastIndex = urls.length;

        return Integer.valueOf(urls[lastIndex-1]);
    }

    public int getAwayTeamId() {

        String url = _links.getAwayTeamUrl();
        String[] urls = url.split("/");
        int lastIndex = urls.length;

        return Integer.valueOf(urls[lastIndex-1]);
    }

    public Links get_links() {
        return _links;
    }

    public Date getDate() {
        return date;
    }

    public String getDateString() {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = fmt.format(date);

        return dateString;
    }

    public String getStatus() {
        return status;
    }

    public int getMatchday() {
        return matchday;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public Result getResult() {
        return result;
    }

    public class Result {
        @SerializedName("goalsHomeTeam") int goalsHomeTeam;
        @SerializedName("goalsAwayTeam") int goalsAwayTeam;

        public int getGoalsHomeTeam() {
            return goalsHomeTeam;
        }

        public int getGoalsAwayTeam() {
            return goalsAwayTeam;
        }
    }
}

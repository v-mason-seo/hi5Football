package com.ddastudio.hifivefootball_android.data.model.convert;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by hongmac on 2017. 10. 14..
 */

public class Links {

    @SerializedName("self") SelfUrl selfUrl;
    @SerializedName("fixtures") FixturesUrl fixturesUrl;
    @SerializedName("players") PlayersUrl playersUrl;
    @SerializedName("competition") CompetitionUrl competitionUrl;
    @SerializedName("homeTeam") HomeTeamUrl homeTeamUrl;
    @SerializedName("awayTeam") AwayTeamUrl awayTeamUrl;

    public SelfUrl getSelf() {
        return selfUrl;
    }

    public FixturesUrl getFixtures() {
        return fixturesUrl;
    }

    public PlayersUrl getPlayers() {
        return playersUrl;
    }

    public CompetitionUrl getCompetition() {
        return competitionUrl;
    }

    public String getSelfUrl() {

        if ( selfUrl == null ) return "";
        return selfUrl.getUrl();
    }

    public String getFixturesUrl() {
        return fixturesUrl.getUrl();
    }

    public String getPlayersUrl() {
        return playersUrl.getUrl();
    }

    public String getCompetitionUrl() {

        if ( competitionUrl == null ) return "";
        return competitionUrl.getUrl();
    }

    public String getHomeTeamUrl() {

        if ( homeTeamUrl == null ) return "";
        return homeTeamUrl.getUrl();
    }

    public String getAwayTeamUrl() {

        if ( awayTeamUrl == null ) return "";
        return awayTeamUrl.getUrl();
    }

    /*--------------------------------------------------------*/

    // TODO: 2017-06-29 아래 이너클래스를 스태틱으로 해도 되는지....? 안하면 Error:(64, 12) error: Parceler: Inner Classes annotated with @Parcel must be static.
    @Parcel
    public static class SelfUrl {

        @SerializedName("href") String url;

        public String getUrl() {
            return url;
        }
    }

   /*--------------------------------------------------------*/

    @Parcel
    public static class FixturesUrl {

        @SerializedName("href") String url;

        public String getUrl() {
            return url;
        }
    }

    /*--------------------------------------------------------*/

    @Parcel
    public static class PlayersUrl {
        @SerializedName("href") String url;

        public String getUrl() {
            return url;
        }
    }

    /*--------------------------------------------------------*/

    @Parcel
    public static class CompetitionUrl {
        @SerializedName("href") String url;

        public String getUrl() {
            return url;
        }
    }

    /*--------------------------------------------------------*/

    @Parcel
    public static class AwayTeamUrl {
        @SerializedName("href") String url;

        public String getUrl() {
            return url;
        }
    }

    /*--------------------------------------------------------*/

    @Parcel
    public static class HomeTeamUrl {
        @SerializedName("href") String url;

        public String getUrl() {
            return url;
        }
    }
}

package com.ddastudio.hifivefootball_android.data.model.convert;

import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.utils.DateUtils;
import com.google.gson.annotations.SerializedName;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by hongmac on 2017. 10. 13..
 */

public class CompetitionFDModel extends MultipleItem {

    protected @SerializedName("id") int id;
    @SerializedName("caption") String caption;
    @SerializedName("league") String league;
    @SerializedName("year") String year;
    @SerializedName("currentMatchday") int currentMatchday;
    @SerializedName("numberOfTeams") int numberOfTeams;
    @SerializedName("numberOfGames") int numberOfGames;
    @SerializedName("updated") Date updated;

    public int getId() {
        return id;
    }

    public String getCaption() {
        return caption;
    }

    public String getLeague() {
        return league;
    }

    public String getYear() {
        return year;
    }

    public int getCurrentMatchday() {
        return currentMatchday;
    }

    public int getNumberOfTeams() {
        return numberOfTeams;
    }

    public int getNumberOfGames() {
        return numberOfGames;
    }

    public Date getUpdated() {
        return updated;
    }

    public String getUpdatedString() {
        if ( updated == null ) {
            updated = Calendar.getInstance().getTime();
        }

        return DateUtils.getPrettTime(updated);
    }

    @Override
    public int getItemType() {
        return MultipleItem.ADMIN_COMPETITION;
    }
}

package com.ddastudio.hifivefootball_android.data.model.overview;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hongmac on 2017. 12. 16..
 */

public class MatchRecentFormModel implements MultiItemEntity {

    public int itemType;

    @SerializedName("home_tid") int homeTeamId;
    @SerializedName("home_round") int homeRound;
    @SerializedName("home_position") int homePosition;
    @SerializedName("home_status") String homeStatus;
    @SerializedName("home_recent_form") String homeRecentForm;
    @SerializedName("away_tid") int awayTeamId;
    @SerializedName("away_round") int awayRound;
    @SerializedName("away_position") int awayPosition;
    @SerializedName("away_status") String awayStatus;
    @SerializedName("away_recent_form") String awayRecentForm;


    public MatchRecentFormModel() {
        itemType = ViewType.MATCH_OVERVIEW_RECENT_FORM;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public int getHomeTeamId() {
        return homeTeamId;
    }

    public void setHomeTeamId(int homeTeamId) {
        this.homeTeamId = homeTeamId;
    }

    public int getHomeRound() {
        return homeRound;
    }

    public void setHomeRound(int homeRound) {
        this.homeRound = homeRound;
    }

    public int getHomePosition() {
        return homePosition;
    }

    public void setHomePosition(int homePosition) {
        this.homePosition = homePosition;
    }

    public String getHomeStatus() {
        return homeStatus;
    }

    public void setHomeStatus(String homeStatus) {
        this.homeStatus = homeStatus;
    }

    public String getHomeRecentForm() {
        return homeRecentForm;
    }

    public void setHomeRecentForm(String homeRecentForm) {
        this.homeRecentForm = homeRecentForm;
    }

    public String[] getHomeRecentFormLabelArray() {

        if ( homeRecentForm.toCharArray() == null ) {
            return null;
        }

        int len = homeRecentForm.toCharArray().length;
        String[] labels = new String[len];
        for ( int i=0; i < len; i++) {
            labels[i] = String.valueOf(homeRecentForm.charAt(i));
        }

        return labels;
    }

    public float[] getHomeRecentFormValueArray() {

        if ( homeRecentForm.toCharArray() == null ) {
            return null;
        }

        int len = homeRecentForm.toCharArray().length;
        float[] labels = new float[len];
        for ( int i=0; i < len; i++) {

            switch (homeRecentForm.charAt(i)) {
                case 'W':
                    labels[i] = 3.0f;
                    break;
                case 'D':
                    labels[i] = 2.0f;
                    break;
                case 'L':
                    labels[i] = 1.0f;
                    break;
//                default:
//                    labels[i] = 0.0f;
            }
        }

        return labels;
    }

    public int getAwayTeamId() {
        return awayTeamId;
    }

    public void setAwayTeamId(int awayTeamId) {
        this.awayTeamId = awayTeamId;
    }

    public int getAwayRound() {
        return awayRound;
    }

    public void setAwayRound(int awayRound) {
        this.awayRound = awayRound;
    }

    public int getAwayPosition() {
        return awayPosition;
    }

    public void setAwayPosition(int awayPosition) {
        this.awayPosition = awayPosition;
    }

    public String getAwayStatus() {
        return awayStatus;
    }

    public void setAwayStatus(String awayStatus) {
        this.awayStatus = awayStatus;
    }

    public String getAwayRecentForm() {
        return awayRecentForm;
    }

    public void setAwayRecentForm(String awayRecentForm) {
        this.awayRecentForm = awayRecentForm;
    }

    public String[] getAwayRecentFormLabelArray() {

        if ( awayRecentForm.toCharArray() == null ) {
            return null;
        }

        int len = awayRecentForm.toCharArray().length;
        String[] labels = new String[len];
        for ( int i=0; i < len; i++) {
            labels[i] = String.valueOf(awayRecentForm.charAt(i));
        }

        return labels;
    }

    public float[] getAwayRecentFormValueArray() {

        if ( awayRecentForm.toCharArray() == null ) {
            return null;
        }

        int len = awayRecentForm.toCharArray().length;
        float[] labels = new float[len];
        for ( int i=0; i < len; i++) {

            switch (awayRecentForm.charAt(i)) {
                case 'W':
                    labels[i] = 3.0f;
                    break;
                case 'D':
                    labels[i] = 2.0f;
                    break;
                case 'L':
                    labels[i] = 1.0f;
                    break;
//                default:
//                    labels[i] = 0.0f;
            }
        }

        return labels;
    }
}

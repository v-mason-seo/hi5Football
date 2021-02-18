package com.ddastudio.hifivefootball_android.match_summery.model;

import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hongmac on 2017. 12. 27..
 */

public class TeamSectionWrapperModel extends SummeryBaseWrapperModel {

    @SerializedName("teams") List<TeamModel> teams;

    public List<TeamModel> getTeams() {
        return teams;
    }

}


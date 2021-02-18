package com.ddastudio.hifivefootball_android.match_summery.model;

import com.ddastudio.hifivefootball_android.data.model.football.CompModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CompetitionSectionWrapperModel extends SummeryBaseWrapperModel {

    @SerializedName("competitions")
    List<CompModel> competitionList;

    public List<CompModel> getCompetitionList() {
        return competitionList;
    }
}

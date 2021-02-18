package com.ddastudio.hifivefootball_android.match_summery.model;

import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hongmac on 2017. 12. 27..
 */

public class MatchSectionWrapperModel extends SummeryBaseWrapperModel {

    @SerializedName("matches")
    List<MatchModel> matches;

    public List<MatchModel> getMatches() {
        return matches;
    }
}

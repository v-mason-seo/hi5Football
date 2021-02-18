package com.ddastudio.hifivefootball_android.data.model.convert;

import com.ddastudio.hifivefootball_android.data.model.convert.FixtureModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hongmac on 2017. 10. 13..
 */

public class CompetitionFixtureModel {

    //@SerializedName("_links") Links _links;
    @SerializedName("count") int count;
    @SerializedName("fixtures") List<FixtureModel> fixtures;

//    public Links get_links() {
//        return _links;
//    }_links

    public int getCount() {
        return count;
    }

    public List<FixtureModel> getFixtures() {
        return fixtures;
    }
}

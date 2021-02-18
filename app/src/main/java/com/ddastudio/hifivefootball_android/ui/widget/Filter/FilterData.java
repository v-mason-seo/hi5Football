package com.ddastudio.hifivefootball_android.ui.widget.Filter;

import com.ddastudio.hifivefootball_android.data.model.football.CompModel;
import com.ddastudio.hifivefootball_android.data.model.football.CompetitionModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hongmac on 2018. 2. 4..
 */

public class FilterData implements Serializable  {

    private List<FilterTwoEntity> category;
    private List<FilterEntity> sorts;
    private List<FilterEntity> filters;
    private List<CompModel> competitions;

    public List<FilterTwoEntity> getCategory() {
        return category;
    }

    public void setCategory(List<FilterTwoEntity> category) {
        this.category = category;
    }

    public List<FilterEntity> getSorts() {
        return sorts;
    }

    public void setSorts(List<FilterEntity> sorts) {
        this.sorts = sorts;
    }

    public List<FilterEntity> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterEntity> filters) {
        this.filters = filters;
    }

    public List<CompModel> getCompetitions() {
        return competitions;
    }

    public void setCompetitions(List<CompModel> competitions) {
        this.competitions = competitions;
    }

    public int getCompetitionId(String name) {

        if ( competitions == null)
            return 0;

        for ( int i = 0 ; i < competitions.size(); i++ ) {

            if ( name.equals(competitions.get(i).getCompetitionName())) {
                return competitions.get(i).getCompetitionId();
            }
        }

        return 0;
    }
}

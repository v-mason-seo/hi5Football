package com.ddastudio.hifivefootball_android.data.model;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.data.model.football.CompetitionModel;

import java.util.List;

/**
 * Created by hongmac on 2017. 10. 31..
 */

public class MatchDateModel extends AbstractExpandableItem<MultiItemEntity> implements MultiItemEntity  {

    String matchDate;

    public MatchDateModel(String matchDate) {
        this.matchDate = matchDate;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public CompetitionModel getCompetition(int competitionId) {

        List<MultiItemEntity> competitionList = getSubItems();

        for ( int i = 0 ; i < competitionList.size(); i++) {

            if ( competitionList.get(0) instanceof CompetitionModel) {

                CompetitionModel competitionData = (CompetitionModel)competitionList.get(i);
                if ( competitionData.getCompetitionFaId() == competitionId ) {
                    return competitionData;
                }
            }
        }

        return null;
    }



    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getItemType() {
        return MultipleItem.ARENA_DATE;
    }

    @Override
    public boolean equals(Object obj) {
        return matchDate.equals(obj);
    }

    @Override
    public int hashCode() {
        return matchDate.hashCode();
    }
}

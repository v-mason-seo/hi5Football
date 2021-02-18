package com.ddastudio.hifivefootball_android.data.model.footballdata;



import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.data.model.*;
import com.ddastudio.hifivefootball_android.data.model.football.CompetitionModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hongmac on 2017. 10. 25..
 */

//@Parcel(analyze = {MatchContainerModel.class, CompetitionModel.class})
public class MatchContainerModel extends AbstractExpandableItem<MultiItemEntity> implements MultiItemEntity {

    //@SerializedName("schedule") ScheduleModel schedule;

    @SerializedName("fixture_date") String fixtureDate;
    @SerializedName("pre_fixture_date") String preFixtureDate;
    @SerializedName("next_fixture_date") String nextFixtureDate;
    @SerializedName("competitions") List<CompetitionModel> competitionList;

//    public ScheduleModel getSchedule() {
//        return schedule;
//    }


    public String getFixtureDate() {
        return fixtureDate;
    }

    public String getPreFixtureDate() {
        return preFixtureDate;
    }

    public String getNextFixtureDate() {
        return nextFixtureDate;
    }

    public List<CompetitionModel> getCompetitionList() {
        return competitionList;
    }

    @Override
    public int getItemType() {
        return MultipleItem.ARENA_DATE;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int hashCode() {
        //return super.hashCode();
        return fixtureDate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        //return super.equals(obj);

        if ( obj instanceof MatchContainerModel) {
            return fixtureDate.equals(((MatchContainerModel)obj).getFixtureDate());
//            return fixtureDate == ((MatchContainerModel)obj).fixtureDate
//                    && competitionName.equals(((MatchContainerModel)obj).getCompetitionName());
        }

        return super.equals(obj);
    }

    /*-------------------------------------------------------------------*/

    //    String fixtureDate;
//
//    public MatchContainerModel() {}
//
//    public MatchContainerModel(int id, int imgId, String playerName, String region) {
//
//        super(id, imgId, playerName, region);
//    }
//
//
//    public void setMatchDate(String fixtureDate) {
//        this.fixtureDate = fixtureDate;
//    }
//
//    public String getMatchDate() {
//        if (!TextUtils.isEmpty(fixtureDate)) {
//            Date date = DateUtils.toDate(fixtureDate, "yyyy-MM-dd HH:mm:ss");
//            return DateUtils.convertDateToString(date, "yyyy-MM-dd (EEE)");
//        }
//
//        return "";
//    }
//
//    @Override
//    public int getItemType() {
//        return MultipleItem.ARENA_COMPETITON;
//        //return ScheduleRvAdapter.TYPE_LEVEL_1;
//    }
//
//    @Override
//    public int getLevel() {
//        return 0;
//    }
//
//    @Override
//    public int hashCode() {
//        final int prime = 31;
//
//        int result = 1;
//        result = prime * result + ((competitionName == null) ? 0 : competitionName.hashCode()); result = prime * result + competitionId;
//
//        return result;
//
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        return competitionFaId == ((MatchContainerModel)obj).competitionFaId
//                && competitionName.equals(((MatchContainerModel)obj).getCompetitionName());
//    }
}

package com.ddastudio.hifivefootball_android.data.model;

/**
 * Created by hongmac on 2017. 10. 18..
 */

public class ScheduleFooterModel extends MultipleItem {

    int competitionId;

    public ScheduleFooterModel(int competitionId) {
        this.competitionId = competitionId;
    }

    public int getCompetitionId() {
        return competitionId;
    }

    @Override
    public int getItemType() {
        return MultipleItem.ARENA_SCHEDULE_FOOTER;
    }
}

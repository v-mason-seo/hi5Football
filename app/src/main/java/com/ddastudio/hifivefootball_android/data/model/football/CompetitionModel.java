package com.ddastudio.hifivefootball_android.data.model.football;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

import timber.log.Timber;

/**
 * Created by hongmac on 2017. 10. 17..
 */

@Parcel(analyze = {CompetitionModel.class})
public class CompetitionModel extends AbstractExpandableItem<MultiItemEntity> implements MultiItemEntity {

    int viewType;
    String CompetitionSmallImageBaseUrl = "http://cdn.hifivefootball.com/competitions/24/";
    String CompetitionImageBaseUrl = "http://cdn.hifivefootball.com/competitions/45/";
    String CompetitionLargeImageBaseUrl = "http://cdn.hifivefootball.com/competitions/180/";

    @SerializedName("fixture_date") String fixtureDate;
    @SerializedName("comp_fa_id") int competitionFaId;
    @SerializedName("comp_name") String competitionName;
    @SerializedName("region") String region;
    @SerializedName("comp_image_id") int competitionImageId;
    @SerializedName("is_active") int active;
    @SerializedName("updated") String updated;
    @SerializedName("created") String created;
    @SerializedName("matches") List<MatchModel> matchList;

    public CompetitionModel() {
        this.viewType = MultipleItem.ARENA_COMPETITON;
    }

    public int getActive() {
        return active;
    }

    public boolean isActive() {
        return active > 0;
    }

    public CompetitionModel(int id, int imgId, String name, String region) {

        this.viewType = MultipleItem.ARENA_COMPETITON;
        this.competitionFaId = id;
        this.competitionImageId = imgId;
        this.competitionName = name;
        this.region = region;
    }

    public int getCompetitionImageId() {
        return competitionImageId;
    }

    public String getFixtureDate() {
        return fixtureDate;
    }

    public void setFixtureDate(String fixtureDate) {
        this.fixtureDate = fixtureDate;
    }

    public void setCompetitionImageId(int competitionImageId) {
        this.competitionImageId = competitionImageId;
    }

    public String getCompetitionImageUrl() {

        return CompetitionImageBaseUrl + competitionImageId + ".png";
    }

    public String getCompetitionSmallImageUrl() {

        return CompetitionSmallImageBaseUrl + competitionImageId + ".png";
    }

    public String getCompetitionLargeImageUrl() {

        return CompetitionLargeImageBaseUrl + competitionImageId + ".png";
    }

    public void setCompetitionFaId(int competitionFaId) {
        this.competitionFaId = competitionFaId;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public int getCompetitionFaId() {
        return competitionFaId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public List<MatchModel> getMatchList() {
        return matchList;
    }

    @Override
    public int getItemType() {
        return MultipleItem.ARENA_COMPETITON;
    }

    @Override
    public int getLevel() {
        return 1;
    }

    @Override
    public int hashCode() {
        final int prime = 31;

        int result = 1;
        result = prime * result + ((competitionName == null) ? 0 : competitionName.hashCode()); result = prime * result + competitionFaId;

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj instanceof CompetitionModel) {
            return competitionFaId == ((CompetitionModel)obj).competitionFaId
                    && competitionName.equals(((CompetitionModel)obj).getCompetitionName())
                    && fixtureDate.equals(((CompetitionModel)obj).getFixtureDate());
        }

        return false;
    }

    @Override
    public String toString() {
        return competitionName;
    }
}
package com.ddastudio.hifivefootball_android.data.model.football;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import timber.log.Timber;

/**
 * Created by hongmac on 2018. 2. 22..
 */

@Parcel
public class CompModel implements MultiItemEntity {

    public String CompetitionSmallImageBaseUrl = "http://cdn.hifivefootball.com/competitions/18/";
    public String CompetitionImageBaseUrl = "http://cdn.hifivefootball.com/competitions/45/";
    public String CompetitionLargeImageBaseUrl = "http://cdn.hifivefootball.com/competitions/180/";

    int itemType;

    @SerializedName("id") int competitionId;
    @SerializedName("nm") String competitionName;
    @SerializedName("region") String competitionRegion;
    @SerializedName("comp_image_id") int competitionImageId;
    @SerializedName("is_active") int active;

    /*------------------------------------------------------------------------------*/

    public CompModel() {
        itemType = ViewType.COMPETITION;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    /*------------------------------------------------------------------------------*/

    public int getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public int getCompetitionImageId() {
        return competitionImageId;
    }

    public String getCompetitionRegion() {
        return competitionRegion;
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

    public int getActive() {
        return active;
    }

    public boolean isActive() {
        return active > 0;
    }

    /*------------------------------------------------------------------------------*/

    @Override
    public int hashCode() {
        final int prime = 31;

        int result = 1;
        result = prime * result + ((competitionName == null) ? 0 : competitionName.hashCode()); result = prime * result + competitionId;

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj instanceof CompetitionModel) {
            return competitionId == ((CompetitionModel)obj).competitionFaId
                    && competitionName.equals(((CompetitionModel)obj).getCompetitionName());
        }

        return false;
    }

    @Override
    public String toString() {
        return competitionName;
    }
}

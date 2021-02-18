package com.ddastudio.hifivefootball_android.content_list.model;

import android.text.TextUtils;

import com.ddastudio.hifivefootball_android.utils.DateUtils;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.Date;

/**
 * Created by hongmac on 2017. 9. 4..
 * analyze = {BestContentsHeaderModel.class} <- 부모클래스는 시리얼라이즈 하지 않고 BestContentsHeaderModel 클래스만 시리얼라이즈 한다.
 */

@Parcel/*(analyze = {BestContentsHeaderModel.class})*/
public class BestContentsHeaderModel extends ContentHeaderModel {

    @SerializedName("best_id") String bestId;
    @SerializedName("best_roll") String bestRoll;
    @SerializedName("pre_best_id") String preBestId;

    /*--------------------------------------------------------------*/

    /**
     *  베스트
     * @return
     */
    public String getBestId() {
        return bestId;
    }

    public String getBestDate() {

        if ( bestId == null || TextUtils.isEmpty(bestId))
            return "";

        Date date = DateUtils.toDate(bestId, "yyyyMMdd");
        return DateUtils.convertDateToString(date, "yyyy-MM-dd (E)");
    }

    public String getPreBestId() {
        return preBestId;
    }

    /*--------------------------------------------------------------*/

    @Override
    public boolean equals(Object inObject) {
        if (inObject instanceof BestContentsHeaderModel) {
            BestContentsHeaderModel inItem = (BestContentsHeaderModel) inObject;
            return this.bestId.equals(inItem.bestId)
                    && this.content_id == inItem.getContentId();
        }
        return false;
    }
}

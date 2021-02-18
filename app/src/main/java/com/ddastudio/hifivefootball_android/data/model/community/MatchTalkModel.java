package com.ddastudio.hifivefootball_android.data.model.community;

import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.manager.MatchTalkManager;
import com.ddastudio.hifivefootball_android.data.model.UserActionModel;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.utils.DateUtils;
import com.google.gson.annotations.SerializedName;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by hongmac on 2018. 3. 2..
 */

public class MatchTalkModel implements MultiItemEntity {

    int itemType;

    @SerializedName("talk_id") int matchTalkId;
    @SerializedName("status") String status;
    @SerializedName("content") String content;
    @SerializedName("hifive") int likers;
    @SerializedName("created") Date created;
    @SerializedName("updated") Date updated;
    @SerializedName("user") UserModel user;
    @SerializedName("user_action") UserActionModel userAction;

    public MatchTalkModel() {
        this.itemType = ViewType.MATCH_TALK;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    /*--------------------------------------------------------------*/

    public int getMatchTalkId() {
        return matchTalkId;
    }

    public String getStatus() {

        if ( status == null )
            return "";

        return status;
    }

    public String getStatusMinute() {

        return getStatus().equals("FT") ? status : status + "'";
    }

    public String getContent() {
        return content;
    }

    public int getLikers() {
        return likers;
    }

    public String getCreated() {

        if ( created == null ) {
            created = Calendar.getInstance().getTime();
        }

        return DateUtils.getPrettTime(created);

    }

    public String getUpdated() {
        if ( updated == null ) {
            updated = Calendar.getInstance().getTime();
        }

        return DateUtils.getPrettTime(updated);
    }

    public UserModel getUser() {
        return user;
    }

    public UserActionModel getUserAction() {
        return userAction;
    }
}

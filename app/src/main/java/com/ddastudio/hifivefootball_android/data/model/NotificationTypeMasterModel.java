package com.ddastudio.hifivefootball_android.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hongmac on 2017. 12. 4..
 */

public class NotificationTypeMasterModel {

    @SerializedName("notification_type_id")
    public int notification_type_id;

    @SerializedName("notification_name")
    public String notification_name;

    @SerializedName("description")
    public String description;

    public int getNotification_type_id() {
        return notification_type_id;
    }

    public String getNotification_name() {
        return notification_name;
    }

    public String getDescription() {
        return description;
    }
}

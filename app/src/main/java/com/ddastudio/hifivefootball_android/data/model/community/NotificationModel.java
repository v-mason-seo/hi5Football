package com.ddastudio.hifivefootball_android.data.model.community;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.data.model.NotificationTargetModel;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.utils.DateUtils;
import com.google.gson.annotations.SerializedName;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by hongmac on 2017. 10. 18..
 */

public class NotificationModel {

    @SerializedName("notification_id")
    int notificationId;

    @SerializedName("notification_type_id")
    int notificationTypeId;

    @SerializedName("senders")
    List<UserModel> sender;

    @SerializedName("confirm")
    int confirm;

    @SerializedName("target")
    NotificationTargetModel target;

    @SerializedName("created")
    Date created;


    public String getTitle() {

        if ( sender != null) {
            return String.format("[%s]  %s(%s) %s",
                    target != null ? target.getMent() : "",
                    sender.get(0).getNickname(),
                    sender.get(0).getUsername(),
                    App.getHifiveSettingsManager().getNotificationDescription(notificationTypeId));
        }

        return "";
    }

    public String getAvatarUrl() {

        if ( sender != null )
            return sender.get(0).getAvatarUrl();

        return "";

    }

    public int getNotificationId() {
        return notificationId;
    }

    public int getNotificationTypeId() {
        return notificationTypeId;
    }

    public List<UserModel> getSender() {
        return sender;
    }

    public int getConfirm() {
        return confirm;
    }

    public NotificationTargetModel getTarget() {
        return target;
    }

    public String getCreated() {
        if ( created == null ) {
            created = Calendar.getInstance().getTime();
        }

        return DateUtils.getPrettTime(created);
    }

    public boolean validate() {

        if ( target == null ) {
            return false;
        }
        return true;
    }

}

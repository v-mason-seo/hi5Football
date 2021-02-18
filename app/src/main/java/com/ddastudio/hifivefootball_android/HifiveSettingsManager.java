package com.ddastudio.hifivefootball_android;

import android.content.Context;

import com.ddastudio.hifivefootball_android.board.model.BoardMasterModel;
import com.ddastudio.hifivefootball_android.data.model.NotificationTypeMasterModel;

import java.util.List;

/**
 * Created by hongmac on 2017. 11. 30..
 */

public class HifiveSettingsManager {

    private List<NotificationTypeMasterModel> notificationTypeMasterList;

    public HifiveSettingsManager(Context context) {
    }

    /*--------------------------------------------------------------*/

    public void setNotificationTypeMasterList(List<NotificationTypeMasterModel>
                                                      notificationTypeMasterList) {

        this.notificationTypeMasterList = notificationTypeMasterList;
    }

    public List<NotificationTypeMasterModel> getNotificationTypeMasterList() {
        return notificationTypeMasterList;
    }

    public String getNotificationDescription(int notification_type_id) {

        if ( notificationTypeMasterList == null ) {

            return "X";
        }

        for ( int i = 0; i < notificationTypeMasterList.size(); i++ ) {

            if ( notificationTypeMasterList.get(i).notification_type_id == notification_type_id) {
                return notificationTypeMasterList.get(i).getDescription();
            }
        }

        return "";
    }

    public String getNotificationName(int notification_type_id) {

        if ( notificationTypeMasterList == null ) {

            return "X";
        }

        for ( int i = 0; i < notificationTypeMasterList.size(); i++ ) {

            if ( notificationTypeMasterList.get(i).notification_type_id == notification_type_id) {
                return notificationTypeMasterList.get(i).getNotification_name();
            }
        }

        return "";
    }
}

package com.ddastudio.hifivefootball_android.main;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    public MyFirebaseInstanceIdService() {
        super();
        //Log.i("hong", "MyFirebaseInstanceIdService()");
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        String id = FirebaseInstanceId.getInstance().getId();
        //Log.i("hong", "MyFirebaseInstanceIdService()-refreshedToken : " + refreshedToken );
        //Log.i("hong", "MyFirebaseInstanceIdService()-id : " + id );
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        //Log.i("hong", "onTokenRefresh()");
    }
}

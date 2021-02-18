package com.ddastudio.hifivefootball_android.signin;

/**
 * Created by hongmac on 2017. 11. 17..
 */

public class UserDevice {

    /**
     * 기기번호. UUID. 클라이언트에서 생성.
     */
    String id;

    /**
     * Android ID. 기기를 공장초기화하기 전까지는 unique한 값임.
     * 이 값을 기준으로 이 기기가 이미 등록된 기기인지 체크하므로 index 필요.
     */
    String androidId;

    /**
     * Android device 내에서 생성되는 InstanceID
     */
    String iid;

    /**
     * GCM 토큰.
     * 푸쉬메시지 발송시 이 값을 주소지로 하므로 index 필요
     */
    String gcmToken;

    public UserDevice() { }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public String getGcmToken() {
        return gcmToken;
    }

    public void setGcmToken(String gcmToken) {
        this.gcmToken = gcmToken;
    }

    @Override
    public String toString() {
        //User user = getUser();
        return String.format("{ id = %s, gcmToken = %s, androidId = %s",
                id, gcmToken, androidId);
    }
}

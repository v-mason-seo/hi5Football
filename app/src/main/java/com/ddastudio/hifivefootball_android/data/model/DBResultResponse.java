package com.ddastudio.hifivefootball_android.data.model;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import retrofit2.Response;

/**
 * Created by hongmac on 2017. 9. 4..
 */

public class DBResultResponse {

    @SerializedName("result") int result;
    @SerializedName("message") String message;
    @SerializedName("data") LinkedTreeMap data;

    AuthToken authToken;

    public int getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    int fieldCount;
    int affectedRows;
    int insertId;
    String info;
    int serverStatus;
    int warningStatus;
    int changedRows;

    public int getFieldCount() {
        return fieldCount;
    }

    public int getAffectedRows() {
        return affectedRows;
    }

    public int getInsertId() {
        return insertId;
    }

    public String getInfo() {
        return info;
    }

    public int getServerStatus() {
        return serverStatus;
    }

    public int getWarningStatus() {
        return warningStatus;
    }

    public int getChangedRows() {
        return changedRows;
    }

    public LinkedTreeMap getData() {
        return data;
    }

    /*------------------------------------------*/

    private AuthToken createAuthToken() {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.toJsonTree(data).getAsJsonObject();
        AuthToken auth = gson.fromJson(jsonObject, AuthToken.class);

        return auth;
    }

    public AuthToken getAuthToken() {

        if ( authToken == null ) {
            authToken = createAuthToken();
        }

        return authToken;
    }

    public String getRefreshToken() {
        if ( getAuthToken() != null ) {
            return getAuthToken().getRefreshToken();
        }

        return "";
    }

    public String getAccessToken() {
        if ( getAuthToken() != null ) {
            return getAuthToken().getAccessToken();
        }

        return "";
    }

    /**
     * 토큰정보가 올라른지 확인한다.
     * @return
     */
    public boolean isValidToken() {

        if ( getAuthToken() != null
                && !TextUtils.isEmpty(getAuthToken().getRefreshToken())
                && !TextUtils.isEmpty(getAuthToken().getAccessToken())) {
            return true;
        }

        return false;
    }
}

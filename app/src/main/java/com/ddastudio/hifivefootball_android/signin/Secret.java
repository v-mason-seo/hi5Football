package com.ddastudio.hifivefootball_android.signin;

/**
 * 클라이언트 APK 내에 포함되면 안되는 설정값들을 담는 객체
 *
 * Created by hongmac on 2017. 11. 16..
 */

public class Secret {

    private String name;

    public String facebookAppId;

    public String naverClientName;
    public String naverClientId;
    public String naverClientSecret;

    public String cipherKey;

    public Secret() {
    }

    public String getName() {
        return name;
    }

    public String getFacebookAppId() {
        return facebookAppId;
    }

    public void setFacebookAppId(String facebookAppId) {
        this.facebookAppId = facebookAppId;
    }


    public String getNaverClientName() {
        return naverClientName;
    }

    public void setNaverClientName(String naverClientName) {
        this.naverClientName = naverClientName;
    }

    public String getNaverClientId() {
        return naverClientId;
    }

    public void setNaverClientId(String naverClientId) {
        this.naverClientId = naverClientId;
    }

    public String getNaverClientSecret() {
        return naverClientSecret;
    }

    public void setNaverClientSecret(String naverClientSecret) {
        this.naverClientSecret = naverClientSecret;
    }
}

package com.ddastudio.hifivefootball_android.data.event;

/**
 * Created by hongmac on 2017. 10. 20..
 */

public class LoginEvent {

    public static class LoginStatusEvent {

        String sender;
        boolean isLoginSuccess;

        public LoginStatusEvent(boolean isSuccess, String sender) {
            this.isLoginSuccess = isSuccess;
            this.sender = sender;
        }

        public boolean isLoginSuccess() {
            return isLoginSuccess;
        }

        public String getSender() {
            return sender;
        }
    }
}

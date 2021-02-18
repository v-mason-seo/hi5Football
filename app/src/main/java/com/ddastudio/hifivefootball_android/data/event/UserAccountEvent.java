package com.ddastudio.hifivefootball_android.data.event;

/**
 * Created by hongmac on 2017. 11. 19..
 */

public class UserAccountEvent {

    public static class SignIn extends UserAccountEvent {

    }

    public static class SignOut extends UserAccountEvent {

    }

    public static class SignError extends UserAccountEvent {

        String message;

        public SignError(String message) {

            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}

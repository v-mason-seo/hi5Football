package com.ddastudio.hifivefootball_android.RxMqtt.paho;

public interface OnError {

    /**
     * Action to take in the event of an error occurring
     *
     * @param exception
     *            {@link Throwable}
     */
    void onError(Throwable exception);

}

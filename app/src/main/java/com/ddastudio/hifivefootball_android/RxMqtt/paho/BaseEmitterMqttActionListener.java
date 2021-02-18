package com.ddastudio.hifivefootball_android.RxMqtt.paho;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

public abstract class BaseEmitterMqttActionListener implements IMqttActionListener {

    protected final static Logger LOGGER = Logger.getLogger(BaseEmitterMqttActionListener.class.getName());

    @Override
    public void onFailure(final IMqttToken asyncActionToken, final Throwable exception) {
        if (LOGGER.isLoggable(Level.SEVERE)) {
            LOGGER.log(Level.SEVERE, exception.getMessage(), exception);
        }
        this.getOnError().onError(exception);
    }

    /**
     * Return the {@link OnError} implementation for this listener so that the
     * onFailure() method can call onError()
     *
     * @return {@link OnError} implementation.
     */
    public abstract OnError getOnError();
}

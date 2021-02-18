package com.ddastudio.hifivefootball_android.RxMqtt.paho;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;

public class DisconnectFactory extends BaseMqttActionFactory {

    private final static Logger LOGGER = Logger.getLogger(DisconnectFactory.class.getName());

    static final class DisconnectActionListener extends CompletableEmitterMqttActionListener {

        public DisconnectActionListener(final CompletableEmitter emitter) {
            super(emitter);
        }

        @Override
        public void onSuccess(final IMqttToken asyncActionToken) {
            this.emitter.onComplete();
        }
    }

    public DisconnectFactory(final IMqttAsyncClient client) {
        super(client);
    }

    public Completable create() {
        return Completable.create(emitter -> {

            try {
                this.client.disconnect(null, new DisconnectActionListener(emitter));
            } catch (final MqttException exception) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, exception.getMessage(), exception);
                }
                emitter.onError(exception);
            }
        });
    }

}

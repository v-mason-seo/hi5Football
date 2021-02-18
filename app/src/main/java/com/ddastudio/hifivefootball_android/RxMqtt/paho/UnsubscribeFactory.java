package com.ddastudio.hifivefootball_android.RxMqtt.paho;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;

public class UnsubscribeFactory extends BaseMqttActionFactory {

    private final static Logger LOGGER = Logger.getLogger(UnsubscribeFactory.class.getName());

    static final class UnsubscribeActionListener extends CompletableEmitterMqttActionListener {

        public UnsubscribeActionListener(final CompletableEmitter emitter) {
            super(emitter);
        }

        @Override
        public void onSuccess(final IMqttToken asyncActionToken) {
            this.emitter.onComplete();
        }
    }

    public UnsubscribeFactory(final IMqttAsyncClient client) {
        super(client);
    }

    public Completable create(final String[] topics) {

        return Completable.create(emitter -> {
            try {
                this.client.unsubscribe(topics, null, new UnsubscribeActionListener(emitter));
            } catch (final MqttException exception) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, exception.getMessage(), exception);
                }
                emitter.onError(exception);
            }
        });
    }

}

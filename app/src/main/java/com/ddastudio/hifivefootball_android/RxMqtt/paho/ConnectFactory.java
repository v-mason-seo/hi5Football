package com.ddastudio.hifivefootball_android.RxMqtt.paho;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;

public class ConnectFactory extends BaseMqttActionFactory {

    static final class ConnectActionListener extends CompletableEmitterMqttActionListener {

        public ConnectActionListener(final CompletableEmitter emitter) {
            super(emitter);
        }

        @Override
        public void onSuccess(final IMqttToken asyncActionToken) {
            this.emitter.onComplete();
        }
    }

    private final MqttConnectOptions options;

    private final static Logger LOGGER = Logger.getLogger(ConnectFactory.class.getName());

    public ConnectFactory(final IMqttAsyncClient client, final MqttConnectOptions options) {
        super(client);
        this.options = Objects.requireNonNull(options);
    }

    public Completable create() {
        return Completable.create(emitter -> {
            try {
                this.client.connect(this.options, null, new ConnectActionListener(emitter));
            } catch (final MqttException exception) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, exception.getMessage(), exception);
                }
                emitter.onError(exception);
            }
        });
    }

    public MqttConnectOptions getOptions() {
        return this.options;
    }
}

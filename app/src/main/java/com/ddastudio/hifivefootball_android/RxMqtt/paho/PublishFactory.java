package com.ddastudio.hifivefootball_android.RxMqtt.paho;

import com.ddastudio.hifivefootball_android.RxMqtt.MqttMessage;
import com.ddastudio.hifivefootball_android.RxMqtt.PublishToken;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;


public class PublishFactory extends BaseMqttActionFactory {

    private final static Logger LOGGER = Logger.getLogger(PublishFactory.class.getName());

    static final class PublishActionListener extends BaseEmitterMqttActionListener {

        private final SingleEmitter<? super PublishToken> emitter;

        public PublishActionListener(final SingleEmitter<? super PublishToken> emitter) {
            this.emitter = Objects.requireNonNull(emitter);
        }

        @Override
        public OnError getOnError() {
            return new OnError() {

                @Override
                public void onError(final Throwable t) {
                    PublishActionListener.this.emitter.onError(t);
                }
            };
        }

        @Override
        public void onSuccess(final IMqttToken mqttToken) {

            final PublishToken publishToken = new PublishToken() {

                @Override
                public String getClientId() {
                    return mqttToken.getClient().getClientId();
                }

                @Override
                public String[] getTopics() {
                    return mqttToken.getTopics();
                }

                @Override
                public int getMessageId() {
                    return mqttToken.getMessageId();
                }

                @Override
                public boolean getSessionPresent() {
                    return mqttToken.getSessionPresent();
                }

            };
            this.emitter.onSuccess(publishToken);
        }
    }

    public PublishFactory(final IMqttAsyncClient client) {
        super(client);
    }

    public Single<PublishToken> create(final String topic, final MqttMessage msg) {
        return Single.create(emitter -> {
            try {
                this.client.publish(topic, msg.getPayload(), msg.getQos(), msg.isRetained(), null,
                        new PublishActionListener(emitter));
            } catch (final MqttException exception) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, exception.getMessage(), exception);
                }
                emitter.onError(exception);

            }
        });
    }

}

package com.ddastudio.hifivefootball_android.RxMqtt.paho;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import io.reactivex.Completable;
import io.reactivex.functions.Action;

public class CloseFactory extends BaseMqttActionFactory {

    public CloseFactory(final IMqttAsyncClient client) {
        super(client);
    }

    public Completable create() {

        return Completable.fromAction(new Action() {

            @Override
            public void run() throws MqttException {
                CloseFactory.this.client.close();
            }
        });
    }
}

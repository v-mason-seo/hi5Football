package com.ddastudio.hifivefootball_android.RxMqtt.paho;

import java.util.Objects;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;

public class BaseMqttActionFactory {

    protected final IMqttAsyncClient client;

    public BaseMqttActionFactory(final IMqttAsyncClient client) {
        this.client = Objects.requireNonNull(client);
    }

    public IMqttAsyncClient getMqttAsyncClient() {
        return this.client;
    }

}
package com.ddastudio.hifivefootball_android.data.model.arena;

import android.content.Context;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

/**
 * Created by hongmac on 2018. 3. 7..
 */

public class ArenaConnection {

    private Context context = null;

    private String clientId = null;
    private String host = null;
    private int port = 0;
    private String serverUri;
    private boolean tlsConnection = false;
    private MqttAndroidClient client = null;
    private MqttConnectOptions mqttConnectOptions;

    public ArenaConnection(String clientId, String host, int port, Context context, MqttAndroidClient client, boolean tlsConnection) {

        this.clientId = clientId;
        this.host = host;
        this.port = port;
        this.context = context;
        this.client = client;
        this.tlsConnection = tlsConnection;
    }

    public static ArenaConnection createArenaConnection(String clientId, String host, int port, Context context, boolean tlsConnection) {

        String uri;
        if(tlsConnection) {
            uri = "ssl://" + host + ":" + port;
        } else {
            uri = "tcp://" + host + ":" + port;
        }
        MqttAndroidClient client = new MqttAndroidClient(context, uri, clientId);
        return new ArenaConnection(clientId, host, port, context, client, tlsConnection);
    }


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public MqttAndroidClient getClient() {
        return client;
    }

    public void setClient(MqttAndroidClient client) {
        this.client = client;
    }

    public MqttConnectOptions getMqttConnectOptions() {
        return mqttConnectOptions;
    }

    public void setMqttConnectOptions(MqttConnectOptions mqttConnectOptions) {
        this.mqttConnectOptions = mqttConnectOptions;
    }
}

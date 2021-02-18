package com.ddastudio.hifivefootball_android.RxMqtt;

@Deprecated
public interface SubscribeToken extends MqttToken {

    /**
     * Returns the granted QoS when a subscription is acknowledged
     *
     * @return array of integers representing the QoS levels
     */
    public int[] getGrantedQos();

}

package com.ddastudio.hifivefootball_android.RxMqtt;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * RxJava MQTT API
 *
 * @author pvankann@gmail.com
 *
 */
public interface ObservableMqttClient {

    /**
     * Get the MQTT broker URI
     *
     * @return the MQTT broker URI string
     */
    String getBrokerUri();

    /**
     * Get the MQTT client id from the underlying MQTT client
     *
     * @return {@link String} client identifier
     */
    String getClientId();

    /**
     * Whether the MQTT client is connected to the broker
     *
     * @return <code>true</code> if connected, <code>false</code> otherwise
     */
    boolean isConnected();

    /**
     * Close the MQTT client
     *
     * @return {@link Completable} that will complete on successful closure
     */
    Completable close();

    /**
     * Connect the MQTT client
     *
     * @return {@link Completable} that will complete on successful connection
     */
    Completable connect();

    /**
     * Disconnect the MQTT client
     *
     * @return {@link Completable} that will complete on successful
     *         disconnection
     */
    Completable disconnect();

    /**
     * Publish an {@link MqttMessage} to a {@link String} topic at the given QOS
     * level
     *
     * @return {@link Single} that will complete on successful publication with
     *         a {@link PublishToken}
     */
    Single<PublishToken> publish(String topic, MqttMessage msg);

    /**
     * Subscribe to multiple {@link String} topics to receive multiple
     * {@link MqttMessage} at the given QOS levels
     *
     * @return {@link Single} that will complete on successful publication with
     *         a {@link PublishToken}
     */
    Flowable<MqttMessage> subscribe(String[] topics, int[] qos);

    /**
     * Subscribe to a {@link String} topic to receive multiple
     * {@link MqttMessage} at the supplied QOS level
     *
     * @return {@link Flowable} that will receive multiple {@link MqttMessage}
     */
    Flowable<MqttMessage> subscribe(String topic, int qos);

    /**
     * Unsubscribe from the given topics {@link String} array
     *
     * @return {@link Completable} that will complete on successful unsubscribe
     */
    Completable unsubscribe(String[] topics);

    /**
     * Unsubscribe from the given topic {@link String}
     *
     * @return {@link Completable} that will complete on successful unsubscribe
     */
    Completable unsubscribe(String topic);

}

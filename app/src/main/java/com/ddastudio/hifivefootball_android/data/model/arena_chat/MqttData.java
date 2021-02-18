package com.ddastudio.hifivefootball_android.data.model.arena_chat;

/**
 * Created by hongmac on 2018. 1. 26..
 */

public class MqttData {

    String topic;
    String message;

    public MqttData(String topic, String message) {
        this.topic = topic;
        this.message = message;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

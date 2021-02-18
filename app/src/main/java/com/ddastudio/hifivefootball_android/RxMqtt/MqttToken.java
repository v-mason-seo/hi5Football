package com.ddastudio.hifivefootball_android.RxMqtt;

public interface MqttToken {

    String getClientId();

    /**
     * Returns the topic string for the this token or null if nothing has been
     * published
     *
     * @return {@link String} topics for the token or null
     */
    String[] getTopics();

    /**
     * Returns the identifier for the message associated with this token
     *
     * @return {@link String} message identifier
     */
    public int getMessageId();

    /**
     * Whether a session is present for this topic
     *
     * @return <code>true</code> if session present or <code>false</code>
     *         otherwise
     */
    public boolean getSessionPresent();

}

package com.ddastudio.hifivefootball_android.data.event;

import com.ddastudio.hifivefootball_android.data.model.arena_chat.MqttData;
import com.ddastudio.hifivefootball_android.data.model.arena_chat.ReactionModel;

/**
 * Created by hongmac on 2018. 1. 26..
 */

public class ArenaChatEvent {

    /**
     * Mqtt - connectComplete 이벤트
     * 접속이 성공적으로 이루어지면 받는 이벤트.
     */
    public static class ArenaConnectComplete {

    }


    /**
     * Mqtt - connectionLost 이벤트
     * 접속이 끊겼을때 받는 이벤트
     */
    public static class ArenaConnectLost {

    }

    /**
     * Mqtt - messageArrived 이벤트
     * 메시지를 받았을때
     */
    public static class ArenaMessageArrived {

        ReactionModel reaction;

        public ArenaMessageArrived(ReactionModel reaction) {
            this.reaction = reaction;
        }

        public ReactionModel getReaction() {
            return reaction;
        }
    }

    /**
     * 메시지가 성공적으로 전송됐을 때 받는 이벤트
     */
    public static class ArenaDeliveryComplete {

    }

    public static class ArenaSendMessage {

        ReactionModel reaction;

        public ArenaSendMessage(ReactionModel data) {
            this.reaction = data;
        }

        public ReactionModel getReaction() {
            return reaction;
        }
    }

    public static class ArenaChoiceTeam {

        boolean isHomeTeam = true;

        public ArenaChoiceTeam(boolean isHomeTeam) {
            this.isHomeTeam = isHomeTeam;
        }

        public boolean isHomeTeam() {
            return isHomeTeam;
        }
    }

}

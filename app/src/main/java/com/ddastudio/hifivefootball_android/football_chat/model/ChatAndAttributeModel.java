package com.ddastudio.hifivefootball_android.football_chat.model;

import android.arch.persistence.room.Embedded;

public class ChatAndAttributeModel {

    @Embedded
    public ChatModel chat;

    @Embedded
    public ChatAttributeModel attr;

    public ChatModel getChat() {
        return chat;
    }

    public int getBoardId() {

        if ( chat != null ) {
            return chat.getBoardId();
        }

        return -1;
    }

    public String getBoardName() {

        if ( chat != null ) {
            return chat.getBoardName();
        }

        return "";
    }

    public ChatAttributeModel getAttr() {
        return attr;
    }

    public String getMentionType() {

        if ( chat != null ) {
            return chat.getMentionType();
        }

        return "";
    }

    public String getAttrMentionType() {

        if ( attr != null ) {
            return attr.getMentionType();
        }

        return "";
    }

    public int getMentionId() {

        if ( chat != null ) {
            return chat.getMentionId();
        }

        return 0;
    }

    public int getAttrMentionId() {

        if ( attr != null ) {
            return attr.getMentionId();
        }

        return 0;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ChatAndAttributeModel f = (ChatAndAttributeModel) obj;

        boolean isSame =
                ((chat == null && f.chat == null ) || (chat != null && f.chat != null && chat.equals(f.chat)))
                &&
                (( attr == null && f.attr == null ) || ( attr != null && f.attr != null && attr.equals(f.attr) ))
                ;

        return isSame;
    }
}

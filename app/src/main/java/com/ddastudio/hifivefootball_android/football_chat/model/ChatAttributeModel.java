package com.ddastudio.hifivefootball_android.football_chat.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "football_chat_attrs", primaryKeys = {"attr_mention_type", "attr_mention_id"})
public class ChatAttributeModel {

    @NotNull
    @ColumnInfo(name = "attr_mention_type")
    public String mentionType;

    @NotNull
    @ColumnInfo(name = "attr_mention_id")
    public int mentionId;

    // 현재 게시판이 선택되었는지 여부
    @ColumnInfo(name = "is_sel")
    public int isSelected;

    @ColumnInfo(name = "read_cnt")
    public int readCount;

    public ChatAttributeModel() {

    }

    public ChatAttributeModel(String type, int id, boolean selected, int readCount) {

        this.mentionType = type;
        this.mentionId = id;
        this.isSelected = selected == true ? 1 : 0;
        this.readCount = readCount;
    }

    public String getMentionType() {
        return mentionType;
    }

    public int getMentionId() {
        return mentionId;
    }

    public int getSelected() {
        return isSelected;
    }

    public boolean isSelected() {

        return isSelected == 0 ? false : true;
    }

    public int getReadCount() {
        return readCount;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ChatAttributeModel f = (ChatAttributeModel) obj;

        return this.getMentionType().equals(f.getMentionType())
                && this.getMentionId() == f.getMentionId()
                && this.isSelected() == f.isSelected();
    }
}

package com.ddastudio.hifivefootball_android.football_chat;

import android.support.v7.util.DiffUtil;
import android.util.Log;

import com.ddastudio.hifivefootball_android.football_chat.model.ChatAndAttributeModel;

import java.util.List;

public class ChatAndAttributeDiffUtil extends DiffUtil.Callback {

    private final List<ChatAndAttributeModel> oldList;
    private final List<ChatAndAttributeModel> newList;

    public ChatAndAttributeDiffUtil(List<ChatAndAttributeModel> oldList, List<ChatAndAttributeModel> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList != null ? oldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newList != null ? newList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {

        final ChatAndAttributeModel oldItem = oldList.get(oldItemPosition);
        final ChatAndAttributeModel newItem = newList.get(newItemPosition);

        boolean isSame
                =  oldItem.getMentionId() == newItem.getMentionId()
                && oldItem.getMentionType().equals(newItem.getMentionType())
                && oldItem.getAttrMentionId() == newItem.getAttrMentionId()
                && oldItem.getAttrMentionType().equals(newItem.getAttrMentionType());

        return isSame;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

        final ChatAndAttributeModel oldFootballChat = oldList.get(oldItemPosition);
        final ChatAndAttributeModel newFootballChat = newList.get(newItemPosition);

        return oldFootballChat.equals(newFootballChat);
    }
}

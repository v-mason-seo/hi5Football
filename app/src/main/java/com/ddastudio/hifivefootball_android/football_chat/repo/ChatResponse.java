package com.ddastudio.hifivefootball_android.football_chat.repo;

import com.ddastudio.hifivefootball_android.football_chat.model.ChatAndAttributeModel;

import java.util.List;

public class ChatResponse {

    final private int page;
    final private int limit;
    final private List<ChatAndAttributeModel> list;

    public ChatResponse(int page, int limit, List<ChatAndAttributeModel> list) {
        this.page = page;
        this.limit = limit;
        this.list = list;
    }

    public int getPage() {
        return page;
    }

    public int getLimit() {
        return limit;
    }

    public List<ChatAndAttributeModel> getList() {
        return list;
    }
}

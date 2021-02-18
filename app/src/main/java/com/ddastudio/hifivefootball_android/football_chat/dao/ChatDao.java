package com.ddastudio.hifivefootball_android.football_chat.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.ddastudio.hifivefootball_android.football_chat.model.ChatAndAttributeModel;
import com.ddastudio.hifivefootball_android.football_chat.model.ChatAttributeModel;
import com.ddastudio.hifivefootball_android.football_chat.model.ChatModel;

import java.util.List;

@Dao
public abstract class ChatDao {

    @Query("select * from football_chats order by updated desc")
    public abstract LiveData<List<ChatModel>> loadChats();


    @Query("select * from football_chats where mention_type = :mention_type  and mention_id = :mention_id order by updated desc")
    public abstract LiveData<ChatModel> loadChat(String mention_type, int mention_id);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void saveChats(List<ChatModel> chats);


    @Query("select a.*, b.* " +
           "from  football_chats a " +
           "   ,  football_chat_attrs b " +
           "where a.mention_id = b.attr_mention_id " +
           "and   a.mention_type = b.attr_mention_type " +
           "and   b.is_sel = 1")
    public abstract LiveData<ChatAndAttributeModel> loadSelectedChatAndAttr();

    @Query("select a.*, b.* from football_chats a left join football_chat_attrs b on a.mention_id = b.attr_mention_id and a.mention_type = b.attr_mention_type and b.is_sel = 1")
    public abstract ChatAndAttributeModel loadSelectedChatAndAttr2();

    /**
     * ChatModel, ChatAttributeModel 데이터를 가져온다.
     *  - ChatModel : 챗 리스트 정보
     *  - ChatAttribute : ChatModel 선택여부, 읽은글 개수 정보를 관리
     * @return
     */
    @Query("select a.*, b.* from football_chats a left join football_chat_attrs b on a.mention_id = b.attr_mention_id and a.mention_type = b.attr_mention_type order by updated desc LIMIT :limit OFFSET :offset")
    public abstract LiveData<List<ChatAndAttributeModel>> loadChatAndAttrList(int limit, int offset);

    @Query("select a.*, b.* from football_chats a left join football_chat_attrs b on a.mention_id = b.attr_mention_id and a.mention_type = b.attr_mention_type order by updated desc")
    public abstract List<ChatAndAttributeModel> loadChatAndAttrList2();


    /**
     * ChatAttribute 데이터 입력
     *  - ChatAttribute : ChatModel 선택여부, 읽은글 개수 정보를 관리
     * @param chatAttributes
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void saveChatAttributes(List<ChatAttributeModel> chatAttributes);


    /**
     * ChatAttribute 데이터 입력
     *  - ChatAttribute : ChatModel 선택여부, 읽은글 개수 정보를 관리
     * @param chatAttribute
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void saveChatAttribute(ChatAttributeModel chatAttribute);


    /**
     * 선택한 풋볼챗 정보 삭제
     */
    @Query("update football_chat_attrs set is_sel = 0")
    public abstract void clearSelected();


    /**
     * 1. 선택한 풋볼챗 정보 삭제
     * 2. 현재 선택한 풋볼챗 데이터 입력
     * @param chatAttribute
     */
    @Transaction
    public void clearAndChatAttributeInsertAt(ChatAttributeModel chatAttribute) {
        clearSelected();
        saveChatAttribute(chatAttribute);
    }
}

package com.ddastudio.hifivefootball_android.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.ddastudio.hifivefootball_android.board.model.BoardMasterModel;
import com.ddastudio.hifivefootball_android.board.model.BoardsDao;
import com.ddastudio.hifivefootball_android.football_chat.model.ChatAttributeModel;
import com.ddastudio.hifivefootball_android.football_chat.model.ChatModel;
import com.ddastudio.hifivefootball_android.football_chat.dao.ChatDao;

// AppDatabase.java
@Database(entities = {BoardMasterModel.class, ChatModel.class, ChatAttributeModel.class}, version = 3)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;
    public abstract BoardsDao boardsDao();
    public abstract ChatDao chatDao();

    private static final Object sLock = new Object();

    public static AppDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        //AppDatabase.class, "my_sample.db")
                        AppDatabase.class, "hifive_football.db")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build();
            }
            return INSTANCE;
        }
    }
}
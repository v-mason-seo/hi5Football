package com.ddastudio.hifivefootball_android.board.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public abstract class  BoardsDao {

    @Query("select * from boards")
    public abstract List<BoardMasterModel> getBoards();

    @Query("select * from boards where use_yn = 1 and type_gb in ('A', 'D') ")
    public abstract List<BoardMasterModel> getBoardsToUse();

    @Query("select * from boards where use_yn = 1 and type_gb in ('A', 'D')")
    public abstract LiveData<List<BoardMasterModel>> getAsyncBoardsToUse();

    @Query("select * from boards where use_yn = 1 and type_gb = 'A' and is_sel = 1 limit 1")
    public abstract LiveData<BoardMasterModel> getLiveSelectedBoard();

    @Query("select * from boards where use_yn = 1 and type_gb = 'A' and is_sel = 1 limit 1")
    public abstract BoardMasterModel getSelectedBoard();

    @Query("select * from boards where board_id = (:board_id)")
    public abstract LiveData<BoardMasterModel> getLiveBoardByIds(int board_id);

    @Query("select * from boards where board_id = (:board_id)")
    public abstract BoardMasterModel getBoardByIds(int board_id);

    @Query("select * from boards where is_sel = 'true' limit 1")
    public abstract BoardMasterModel findSelectedBoard();


    /**
     * 게시글 작성할 수 있는 게시판정보를 쿼리한다.
     * @return
     */
    @Query("select * from boards where use_yn = 1 and has_instance_text = 1")
    public abstract List<BoardMasterModel> getAvaibleCreateContentBoardList();

    /**
     * 게시글 작성할 수 있는 게시판정보를 쿼리한다.
     * @return
     */
    @Query("select * from boards where use_yn = 1 and has_instance_text = 1")
    public abstract LiveData<List<BoardMasterModel>> getLiveAvaibleCreateContentBoardList();

//    @Insert
//    void insertAll(BoardMasterModel... boards);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertAll(List<BoardMasterModel> boards);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAt(BoardMasterModel board);

    @Query("update boards set is_sel = 0, is_sel_best = 0;")
    public abstract void clearSelectedBoard();

    @Query("update boards set is_sel = 1, is_sel_best = :is_sel_best where board_id = :board_id")
    public abstract void updateSelectedBoard(int board_id, int is_sel_best);

    @Transaction
    public void clearAndUpdateSelectedBoard(int board_id, int is_sel_best) {
        clearSelectedBoard();
        updateSelectedBoard(board_id, is_sel_best);
    }

    @Query("delete from boards")
    public abstract void deleteAll();

    @Delete
    public abstract void delete(BoardMasterModel board);
}

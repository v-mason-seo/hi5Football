package com.ddastudio.hifivefootball_android.board;

import android.support.v7.util.DiffUtil;

import com.ddastudio.hifivefootball_android.board.model.BoardMasterModel;

import java.util.List;

public class BoardListDiffCallback extends DiffUtil.Callback {

    private final List<BoardMasterModel> oldList;
    private final List<BoardMasterModel> newList;

    public BoardListDiffCallback(List<BoardMasterModel> oldList, List<BoardMasterModel> newList) {
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

        final BoardMasterModel oldBoard = oldList.get(oldItemPosition);
        final BoardMasterModel newBoard = newList.get(newItemPosition);

        return oldBoard.getBoardId() == newBoard.getBoardId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

        final BoardMasterModel oldBoard = oldList.get(oldItemPosition);
        final BoardMasterModel newBoard = newList.get(newItemPosition);

        return oldBoard.equals(newBoard);
    }
}

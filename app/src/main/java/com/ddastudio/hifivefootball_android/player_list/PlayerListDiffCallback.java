package com.ddastudio.hifivefootball_android.player_list;

import android.support.v7.util.DiffUtil;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import java.util.List;

public class PlayerListDiffCallback extends DiffUtil.Callback {

    private final List<PlayerModel> oldList;
    private final List<PlayerModel> newList;

    public PlayerListDiffCallback(List<PlayerModel> oldList, List<PlayerModel> newList) {
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

        final PlayerModel oldPlayer = oldList.get(oldItemPosition);
        final PlayerModel newPlayer = newList.get(newItemPosition);

        return oldPlayer.getPlayerId() == newPlayer.getPlayerId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

        final PlayerModel oldPlayer = oldList.get(oldItemPosition);
        final PlayerModel newPlayer = newList.get(newItemPosition);

        return oldPlayer.equals(newPlayer);
    }
}

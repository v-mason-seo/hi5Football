package com.ddastudio.hifivefootball_android.team_list;

import android.support.v7.util.DiffUtil;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;
import java.util.List;

public class TeamListDiffCallback extends DiffUtil.Callback {

    private final List<TeamModel> oldList;
    private final List<TeamModel> newList;

    public TeamListDiffCallback(List<TeamModel> oldList, List<TeamModel> newList) {
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

        final TeamModel oldTeam = oldList.get(oldItemPosition);
        final TeamModel newTeam = newList.get(newItemPosition);

        return oldTeam.getTeamId() == newTeam.getTeamId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

        final TeamModel oldTeam = oldList.get(oldItemPosition);
        final TeamModel newTeam = newList.get(newItemPosition);

        return oldTeam.equals(newTeam);
    }
}

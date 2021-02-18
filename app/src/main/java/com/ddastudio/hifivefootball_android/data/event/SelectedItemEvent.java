package com.ddastudio.hifivefootball_android.data.event;

import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;

import java.util.List;

/**
 * Created by hongmac on 2017. 11. 23..
 */

public class SelectedItemEvent {

    public static class PlayerItem  {

        List<PlayerModel> items;

        public PlayerItem(List<PlayerModel> items) {
            this.items = items;
        }

        public List<PlayerModel> getItems() {
            return items;
        }
    }

    public static class TeamItem {

        List<TeamModel> items;

        public TeamItem(List<TeamModel> items) {
            this.items = items;
        }

        public List<TeamModel> getItems() {
            return items;
        }
    }
}

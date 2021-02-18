package com.ddastudio.hifivefootball_android.data.model.footballdata;

import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hongmac on 2017. 10. 30..
 */

@Parcel
public class TeamStatisticsModel extends MultipleItem {

    @SerializedName("rank") String rank;
    @SerializedName("wins") String wins;
    @SerializedName("draws") String draws;
    @SerializedName("goals") String goals;
    @SerializedName("losses") String losses;
    @SerializedName("wins_away") String wins_away;
    @SerializedName("wins_home") String wins_home;
    @SerializedName("draws_away") String draws_away;
    @SerializedName("draws_home") String draws_home;
    @SerializedName("goals_away") String goals_away;
    @SerializedName("goals_home") String goals_home;
    @SerializedName("losses_away") String losses_away;
    @SerializedName("losses_home") String losses_home;
    @SerializedName("clean_sheets") String clean_sheets;
    @SerializedName("biggest_defeat") String biggest_defeat;
    @SerializedName("goals_conceded") String goals_conceded;
    @SerializedName("failed_to_score") String failed_to_score;
    @SerializedName("clean_sheets_away") String clean_sheets_away;
    @SerializedName("clean_sheets_home") String clean_sheets_home;
    @SerializedName("biggest_defeat_away") String biggest_defeat_away;
    @SerializedName("biggest_defeat_home") String biggest_defeat_home;
    @SerializedName("goals_conceded_away") String goals_conceded_away;
    @SerializedName("goals_conceded_home") String goals_conceded_home;
    @SerializedName("failed_to_score_away") String failed_to_score_away;
    @SerializedName("failed_to_score_home") String failed_to_score_home;
    @SerializedName("avg_first_goal_scored") String avg_first_goal_scored;
    @SerializedName("avg_first_goal_conceded") String avg_first_goal_conceded;
    @SerializedName("scoring_minutes_0_15_cnt") String scoring_minutes_0_15_cnt;
    @SerializedName("scoring_minutes_0_15_pct") String scoring_minutes_0_15_pct;
    @SerializedName("avg_goals_per_game_scored") String avg_goals_per_game_scored;
    @SerializedName("scoring_minutes_15_30_cnt") String scoring_minutes_15_30_cnt;
    @SerializedName("scoring_minutes_15_30_pct") String scoring_minutes_15_30_pct;
    @SerializedName("scoring_minutes_30_45_cnt") String scoring_minutes_30_45_cnt;
    @SerializedName("scoring_minutes_30_45_pct") String scoring_minutes_30_45_pct;
    @SerializedName("scoring_minutes_45_60_cnt") String scoring_minutes_45_60_cnt;
    @SerializedName("scoring_minutes_45_60_pct") String scoring_minutes_45_60_pct;
    @SerializedName("scoring_minutes_60_75_cnt") String scoring_minutes_60_75_cnt;
    @SerializedName("scoring_minutes_60_75_pct") String scoring_minutes_60_75_pct;
    @SerializedName("scoring_minutes_75_90_cnt") String scoring_minutes_75_90_cnt;
    @SerializedName("scoring_minutes_75_90_pct") String scoring_minutes_75_90_pct;
    @SerializedName("avg_first_goal_scored_away") String avg_first_goal_scored_away;
    @SerializedName("avg_first_goal_scored_home") String avg_first_goal_scored_home;
    @SerializedName("avg_goals_per_game_conceded") String avg_goals_per_game_conceded;
    @SerializedName("avg_first_goal_conceded_away") String avg_first_goal_conceded_away;
    @SerializedName("avg_first_goal_conceded_home") String avg_first_goal_conceded_home;
    @SerializedName("avg_goals_per_game_scored_away") String avg_goals_per_game_scored_away;
    @SerializedName("avg_goals_per_game_scored_home") String avg_goals_per_game_scored_home;
    @SerializedName("avg_goals_per_game_conceded_away") String avg_goals_per_game_conceded_away;
    @SerializedName("avg_goals_per_game_conceded_home") String avg_goals_per_game_conceded_home;

    public List<TeamStatItemModel> generatorTeamStatItemList() {

        List<TeamStatItemModel> items = new ArrayList<>();

        items.add(new TeamStatItemModel("순위", rank));
        items.add(new TeamStatItemModel("골", goals));
        items.add(new TeamStatItemModel("골(원정)", goals_away));
        items.add(new TeamStatItemModel("골(홈)", goals_home));
        items.add(new TeamStatItemModel("승", wins));
        items.add(new TeamStatItemModel("승(원정)", wins_away));
        items.add(new TeamStatItemModel("승(홍)", wins_home));
        items.add(new TeamStatItemModel("무", draws));
        items.add(new TeamStatItemModel("무(원정)", draws_away));
        items.add(new TeamStatItemModel("무(홈)", draws_home));
        items.add(new TeamStatItemModel("패", losses));
        items.add(new TeamStatItemModel("패(원정)", losses_away));
        items.add(new TeamStatItemModel("패(홈)", losses_home));
        items.add(new TeamStatItemModel("clean_sheets", clean_sheets));
        items.add(new TeamStatItemModel("biggest_defeat", biggest_defeat));
        items.add(new TeamStatItemModel("goals_conceded", goals_conceded));
        items.add(new TeamStatItemModel("failed_to_score", failed_to_score));
        items.add(new TeamStatItemModel("clean_sheets_away", clean_sheets_away));
        items.add(new TeamStatItemModel("clean_sheets_home", clean_sheets_home));
        items.add(new TeamStatItemModel("biggest_defeat_away", biggest_defeat_away));
        items.add(new TeamStatItemModel("biggest_defeat_home", biggest_defeat_home));
        items.add(new TeamStatItemModel("goals_conceded_away", goals_conceded_away));
        items.add(new TeamStatItemModel("goals_conceded_home", goals_conceded_home));
        items.add(new TeamStatItemModel("failed_to_score_away", failed_to_score_away));
        items.add(new TeamStatItemModel("failed_to_score_home", failed_to_score_home));
        items.add(new TeamStatItemModel("avg_first_goal_scored", avg_first_goal_scored));
        items.add(new TeamStatItemModel("avg_first_goal_conceded", avg_first_goal_conceded));
        items.add(new TeamStatItemModel("평균득정", avg_goals_per_game_scored));
        items.add(new TeamStatItemModel("scoring_minutes_0_15_cnt", scoring_minutes_0_15_cnt));
        items.add(new TeamStatItemModel("scoring_minutes_0_15_cnt", scoring_minutes_0_15_cnt));
        items.add(new TeamStatItemModel("scoring_minutes_15_30_cnt", scoring_minutes_15_30_cnt));
        items.add(new TeamStatItemModel("scoring_minutes_15_30_pct", scoring_minutes_15_30_pct));
        items.add(new TeamStatItemModel("scoring_minutes_30_45_cnt", scoring_minutes_30_45_cnt));
        items.add(new TeamStatItemModel("scoring_minutes_30_45_pct", scoring_minutes_30_45_pct));
        items.add(new TeamStatItemModel("scoring_minutes_45_60_cnt", scoring_minutes_45_60_cnt));
        items.add(new TeamStatItemModel("scoring_minutes_45_60_pct", scoring_minutes_45_60_pct));
        items.add(new TeamStatItemModel("scoring_minutes_60_75_cnt", scoring_minutes_60_75_cnt));
        items.add(new TeamStatItemModel("scoring_minutes_60_75_pct", scoring_minutes_60_75_pct));
        items.add(new TeamStatItemModel("scoring_minutes_75_90_cnt", scoring_minutes_75_90_cnt));
        items.add(new TeamStatItemModel("scoring_minutes_75_90_pct", scoring_minutes_75_90_pct));
        items.add(new TeamStatItemModel("avg_first_goal_scored_away", avg_first_goal_scored_away));
        items.add(new TeamStatItemModel("avg_first_goal_scored_home", avg_first_goal_scored_home));
        items.add(new TeamStatItemModel("avg_goals_per_game_conceded", avg_goals_per_game_conceded));
        items.add(new TeamStatItemModel("avg_first_goal_conceded_away", avg_first_goal_conceded_away));
        items.add(new TeamStatItemModel("avg_first_goal_conceded_home", avg_first_goal_conceded_home));
        items.add(new TeamStatItemModel("avg_goals_per_game_scored_away", avg_goals_per_game_scored_away));
        items.add(new TeamStatItemModel("avg_goals_per_game_scored_home", avg_goals_per_game_scored_home));
        items.add(new TeamStatItemModel("avg_goals_per_game_conceded_away", avg_goals_per_game_conceded_away));
        items.add(new TeamStatItemModel("avg_goals_per_game_conceded_home", avg_goals_per_game_conceded_home));

        return items;
    }

}

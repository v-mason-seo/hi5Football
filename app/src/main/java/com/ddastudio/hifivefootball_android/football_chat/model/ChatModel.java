package com.ddastudio.hifivefootball_android.football_chat.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;

import com.ddastudio.hifivefootball_android.board.model.BoardMasterModel;
import com.ddastudio.hifivefootball_android.data.model.football.CompModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;
import com.ddastudio.hifivefootball_android.utils.DateUtils;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(tableName = "football_chats", primaryKeys = {"mention_type", "mention_id"})
public class ChatModel {

    @NotNull
    @ColumnInfo(name = "mention_type")
    @SerializedName("mention_type")
    public String mentionType;

    @NotNull
    @ColumnInfo(name = "mention_id")
    @SerializedName("mention_id")
    public int mentionId;

    @ColumnInfo(name = "player")
    @SerializedName("player")
    public PlayerModel player;

    @ColumnInfo(name = "team")
    @SerializedName("team")
    public TeamModel team;

    @ColumnInfo(name = "match")
    @SerializedName("match")
    public MatchModel match;

    @ColumnInfo(name = "board")
    @SerializedName("board")
    public BoardMasterModel board;

    @ColumnInfo(name = "comp")
    @SerializedName("comp")
    public CompModel competition;

    @ColumnInfo(name = "cnt")
    @SerializedName("cnt")
    public int count;

    @ColumnInfo(name = "title")
    @SerializedName("title")
    public String title;

    @ColumnInfo(name = "content_id")
    @SerializedName("content_id")
    public int content_id;

    @ColumnInfo(name = "updated")
    @SerializedName("updated")
    public Date updated;

    public String getMentionType() {
        return mentionType;
    }

    public int getMentionId() {
        return mentionId;
    }

    public String getPlayerEmblem() {

        if ( player != null ) {

            return player.getEmblemUrl();
        }

        return "";
    }

    public String getPlayerTeamName() {

        if ( player != null ) {
            return player.getTeamName();
        }

        return "";
    }

    public int getBoardId() {

        switch (mentionType) {
            case "P":
            case "T":
            case "M":
            case "C":
                // 플레이어, 팀, 매치는 축구게시판 선택
                return 200;
            case "B":
                return board.getBoardId();
            default:
                return -1;
        }
    }

    public String getBoardName() {

        switch (mentionType) {
            case "P":
                return player.getPlayerName();
            case "T":
                return team.getTeamName();
            case "M":
                return match.getHomeTeamName() + "  VS  " + match.getAwayTeamName();
            case "C":
                return getCompetitionName();
            case "B":
                return board.getTitle();
            default:
                return "";
        }
    }

    /**
     * MentionType - C
     * @return
     */
    public CompModel getCompetition() {
        return competition;
    }

    public String getCompetitionName() {

        if ( competition != null ) {
            return competition.getCompetitionName();
        }

        return "";
    }

    public int getCount() {
        return this.count;
    }


    public String getTitle() {
        return this.title;
    }

    public List<String> getImageUrl() {

        List<String> urls = new ArrayList<>();

        switch (mentionType) {
            case "P":
                urls.add(player.getPlayerLargeImageUrl());
                break;
            case "T":
                urls.add(team.getEmblemUrl());
                break;
            case "M":
                urls.add(match.getHomeTeamEmblemUrl());
                urls.add(match.getAwayTeamEmblemUrl());
                break;
            case "C":
                urls.add(competition.getCompetitionImageUrl());
                break;
            case "B":
                break;
        }

        return urls;
    }

    public String getUpdated() {

        if ( updated != null) {
            return DateUtils.getPrettTime(updated);
        }

        return "";
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ChatModel f = (ChatModel) obj;

        return this.getMentionType().equals(f.getMentionType())
                && this.getMentionId() == f.getMentionId()
                && this.getTitle().equals(f.getTitle())
                && this.getCount() == f.getCount()
                //&& this.getBoardName() == f.getBoardName()
                ;
    }
}

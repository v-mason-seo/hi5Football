package com.ddastudio.hifivefootball_android.room;

import android.arch.persistence.room.TypeConverter;

import com.ddastudio.hifivefootball_android.board.model.BoardMasterModel;
import com.ddastudio.hifivefootball_android.data.model.football.CompModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;

public class Converters {

    /*--------------------------------------------------------------------------
     * Date
     *-------------------------------------------------------------------------- */

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }


    /*--------------------------------------------------------------------------
     * Player
     *-------------------------------------------------------------------------- */

    @TypeConverter
    public static PlayerModel stringToPlayerModel(String data) {
        if (data == null || data.equals("null")) {
            return null;
        }

        Gson gson = new Gson();

        Type dataType = new TypeToken<PlayerModel>() {}.getType();

        return gson.fromJson(data, dataType);
    }

    @TypeConverter
    public static String playerModelToString(PlayerModel player) {
        Gson gson = new Gson();
        return gson.toJson(player);
    }


    /*--------------------------------------------------------------------------
     * Team
     *-------------------------------------------------------------------------- */

    @TypeConverter
    public static TeamModel stringToTeamModel(String data) {
        if (data == null || data.equals("null")) {
            return null;
        }

        Gson gson = new Gson();

        Type dataType = new TypeToken<TeamModel>() {}.getType();

        return gson.fromJson(data, dataType);
    }

    @TypeConverter
    public static String teamModelToString(TeamModel team) {
        Gson gson = new Gson();
        return gson.toJson(team);
    }


    /*--------------------------------------------------------------------------
     * Match
     *-------------------------------------------------------------------------- */

    @TypeConverter
    public static MatchModel stringToMatchModel(String data) {
        if (data == null || data.equals("null")) {
            return null;
        }

        Gson gson = new Gson();

        Type dataType = new TypeToken<MatchModel>() {}.getType();

        return gson.fromJson(data, dataType);
    }

    @TypeConverter
    public static String matchModelToString(MatchModel match) {
        Gson gson = new Gson();
        return gson.toJson(match);
    }


    /*--------------------------------------------------------------------------
     * Competition
     *-------------------------------------------------------------------------- */

    @TypeConverter
    public static CompModel stringToComphModel(String data) {
        if (data == null || data.equals("null")) {
            return null;
        }

        Gson gson = new Gson();

        Type dataType = new TypeToken<CompModel>() {}.getType();

        return gson.fromJson(data, dataType);
    }

    @TypeConverter
    public static String compModelToString(CompModel competition) {
        Gson gson = new Gson();
        return gson.toJson(competition);
    }


    /*--------------------------------------------------------------------------
     * Board
     *-------------------------------------------------------------------------- */

    @TypeConverter
    public static BoardMasterModel stringToBoardMasterModel(String data) {
        if (data == null || data.equals("null")) {
            return null;
        }

        Gson gson = new Gson();

        Type dataType = new TypeToken<BoardMasterModel>() {}.getType();

        return gson.fromJson(data, dataType);
    }

    @TypeConverter
    public static String boardMasterModelToString(BoardMasterModel match) {
        Gson gson = new Gson();
        return gson.toJson(match);
    }
}

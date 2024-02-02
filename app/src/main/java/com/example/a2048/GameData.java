package com.example.a2048;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class GameData implements Serializable {

    public static final String TABLE_NAME = "GameData";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATA = "gameData";
    public static final String COLUMN_SCORE = "gameScore";
    public static final String COLUMN_MOVE = "moveNum";
    public static final String COLUMN_SIZE = "gridSize";
    public static final String COLUMN_TIME = "createdTime";
    public static final String COLUMN_GAMING = "gamingTime";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_SCORE + " INTEGER,"
            + COLUMN_MOVE + " INTEGER,"
            + COLUMN_SIZE + " INTEGER,"
            + COLUMN_TIME + " INTEGER,"
            + COLUMN_GAMING + " INTEGER"
            + ")";
    private int id;
    public int[][] array;
    public int gameScore;
    public int moveNum;
    public int gridSize;
    public long createdTime;
    public long gamingTime;

    public GameData() {
    }

    public GameData(int[][] array, int num1, int num2, int num3, long num4) {
        this.array = array;
        this.gameScore = num1;
        this.moveNum = num2;
        this.gridSize = num3;
        this.createdTime = System.currentTimeMillis();
        this.gamingTime = num4;
    }

    public String getGameDataAsString() {
        return GameData2String(this);
    }
    public void setArray(String json){
        array = String2GameData(json).array;
    }

    public static String GameData2String(GameData gameData) {
        Gson gson = new Gson();
        String json = gson.toJson(gameData);
        return json;
    }

    public static GameData String2GameData(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<GameData>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public int getId() {
        return id;
    }

    public void setId(int num) {
        id = num;
    }

    public int[][] getArray() {
        return array;
    }

    public int getGameScore() {
        return gameScore;
    }

    public void setGameScore(int num) {
        gameScore = num;
    }

    public int getMoveNum() {
        return moveNum;
    }

    public void setMoveNum(int num) {
        moveNum = num;
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int num) {
        gridSize = num;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long num) {
        createdTime = num;
    }

    public long getGamingTime() {
        return gamingTime;
    }

    public void setGamingTime(long num) {
        gamingTime = num;
    }

    public String getCreatedTimeAsString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        return sdf.format(new Date(createdTime));
    }

}
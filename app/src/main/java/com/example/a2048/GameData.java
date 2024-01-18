package com.example.a2048;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class GameData implements Serializable {
    public int[][] array;
    public int gameScore;
    public int moveNum;
    public int gridSize;
    public long createdTime;

    public GameData(int[][] array, int num1, int num2, int num3) {
        this.array = array;
        this.gameScore = num1;
        this.moveNum = num2;
        this.gridSize = num3;
        this.createdTime = System.currentTimeMillis();
    }

    public int[][] getArray() {
        return array;
    }

    public int getGameScore() {
        return gameScore;
    }

    public int getMoveNum() {
        return moveNum;
    }

    public int getGridSize() {
        return gridSize;
    }

    public String getCreatedTimeAsString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        return sdf.format(new Date(createdTime));
    }

}
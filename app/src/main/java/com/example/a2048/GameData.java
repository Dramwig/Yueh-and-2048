package com.example.a2048;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class GameData implements Serializable {
    public int[][] array;
    public int GameScore;
    public int MoveNum;
    public int num3;
    public long createdTime;

    public GameData(int[][] array, int num1, int num2, int num3) {
        this.array = array;
        this.GameScore = num1;
        this.MoveNum = num2;
        this.num3 = num3;
        this.createdTime = System.currentTimeMillis();
    }

    public int[][] getArray() {
        return array;
    }

    public int getGameScore() {
        return GameScore;
    }

    public int getMoveNum() {
        return MoveNum;
    }

    public String getCreatedTimeAsString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        return sdf.format(new Date(createdTime));
    }

}
package com.example.a2048;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "GameDataDB";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(GameData.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 处理数据库版本升级的逻辑
        //drop old table if is existed
        db.execSQL("DROP TABLE IF EXISTS " + GameData.TABLE_NAME);
        //create table again
        onCreate(db);
    }

    /**
     *
     * @param GameData
     * @return 返回新插入的行的ID，发生错误，插入不成功，则返回-1
     */
    public long insertGameData(GameData GameData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GameData.COLUMN_DATA, GameData.getGameDataAsString());
        values.put(GameData.COLUMN_SCORE, GameData.getGameScore());
        values.put(GameData.COLUMN_MOVE, GameData.getMoveNum());
        values.put(GameData.COLUMN_SIZE, GameData.getGridSize());
        values.put(GameData.COLUMN_TIME, GameData.getCreatedTime());
        values.put(GameData.COLUMN_GAMING, GameData.getGamingTime());
        long id = db.insert(GameData.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    /**
     *
     * @return 读取数据库，返回一个 GameData 类型的 ArrayList
     */
    public ArrayList<GameData> getAllGameDatas() {
        ArrayList<GameData> GameDataList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + GameData.TABLE_NAME
                + " ORDER BY " + GameData.COLUMN_ID + " ASC";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                GameData GameData = new GameData();
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(GameData.COLUMN_ID));
                @SuppressLint("Range") String gameDataString = cursor.getString(cursor.getColumnIndex(GameData.COLUMN_SCORE));
                @SuppressLint("Range") int gameScore = cursor.getInt(cursor.getColumnIndex(GameData.COLUMN_DATA));
                @SuppressLint("Range") int moveNum = cursor.getInt(cursor.getColumnIndex(GameData.COLUMN_MOVE));
                @SuppressLint("Range") int gridSize = cursor.getInt(cursor.getColumnIndex(GameData.COLUMN_SIZE));
                @SuppressLint("Range") long createdTime = cursor.getLong(cursor.getColumnIndex(GameData.COLUMN_TIME));
                @SuppressLint("Range") long gamingTime = cursor.getLong(cursor.getColumnIndex(GameData.COLUMN_GAMING));
                GameData.setId(id);
                GameData.setArray(gameDataString);
                GameData.setGameScore(gameScore);
                GameData.setMoveNum(moveNum);
                GameData.setGridSize(gridSize);
                GameData.setCreatedTime(createdTime);
                GameData.setGamingTime(gamingTime);

                GameDataList.add(GameData);
            }
            cursor.close(); // 关闭游标
        }

        db.close();
        return GameDataList;
    }

    /**
     *
     * @return 返回数据库行数
     */
    public int getGameDataCount() {
        String countQuery = "SELECT * FROM " + GameData.TABLE_NAME;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }



    /**
     *
     * @param id the database table row id need to delete(需要删除的数据库表中行的ID)
     * @return 返回影响到的行数，如果在 whereClause 有传入条件，返回该条件下影响到的行数，否则返回0。
     * 想要删除所有行，只要在 whereClause 传入 String "1"，并返回删除掉的行数总数（比如：删除了四行就返回4）
     */
    public int deleteGameData(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int idReturnByDelete = db.delete(GameData.TABLE_NAME, GameData.COLUMN_ID + "=? ", new String[]{String.valueOf(id)});
        db.close();
        return idReturnByDelete;
    }

    /**
     * 删除所有行，whereClause 传入 String "1"
     * @return 返回删除掉的行数总数（比如：删除了四行就返回4）
     */
    public int deleteAllGameData() {
        SQLiteDatabase db = getWritableDatabase();
        int idReturnByDelete = db.delete(GameData.TABLE_NAME, String.valueOf(1), null);
        db.close();
        return idReturnByDelete;
    }
}

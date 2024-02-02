package com.example.a2048;

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
     * @param gameData 需要插入的数据
     * @return 返回新插入的行的ID，发生错误，插入不成功，则返回-1
     */
    public long insertGameData(GameData gameData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GameData.COLUMN_DATA, gameData.getGameDataAsString());
        values.put(GameData.COLUMN_SCORE, gameData.getGameScore());
        values.put(GameData.COLUMN_MOVE, gameData.getMoveNum());
        values.put(GameData.COLUMN_SIZE, gameData.getGridSize());
        values.put(GameData.COLUMN_TIME, gameData.getCreatedTime());
        values.put(GameData.COLUMN_GAMING, gameData.getGamingTime());
        long id = db.insert(GameData.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    /**
     *
     * @param id 对应行索引，需要查找的 id
     * @return 返回对应 id 的数据 GameData，没找到返回 null
     */
    public GameData getGameDataById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        GameData gameData = null;

        String[] projection = {
                GameData.COLUMN_ID,
                GameData.COLUMN_DATA,
                GameData.COLUMN_SCORE,
                GameData.COLUMN_MOVE,
                GameData.COLUMN_SIZE,
                GameData.COLUMN_TIME,
                GameData.COLUMN_GAMING
        };

        String selection = GameData.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query(
                GameData.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            String json = cursor.getString(cursor.getColumnIndexOrThrow(GameData.COLUMN_DATA));
            int score = cursor.getInt(cursor.getColumnIndexOrThrow(GameData.COLUMN_SCORE));
            int moveNum = cursor.getInt(cursor.getColumnIndexOrThrow(GameData.COLUMN_MOVE));
            int gridSize = cursor.getInt(cursor.getColumnIndexOrThrow(GameData.COLUMN_SIZE));
            long createdTime = cursor.getLong(cursor.getColumnIndexOrThrow(GameData.COLUMN_TIME));
            long gamingTime = cursor.getLong(cursor.getColumnIndexOrThrow(GameData.COLUMN_GAMING));

            gameData = new GameData(id, json, score, moveNum, gridSize, createdTime, gamingTime);

            cursor.close();
        }

        db.close();

        return gameData;
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
                GameData gameData = new GameData();
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(GameData.COLUMN_ID));
                String gameDataString = cursor.getString(cursor.getColumnIndexOrThrow(GameData.COLUMN_SCORE));
                int gameScore = cursor.getInt(cursor.getColumnIndexOrThrow(GameData.COLUMN_DATA));
                int moveNum = cursor.getInt(cursor.getColumnIndexOrThrow(GameData.COLUMN_MOVE));
                int gridSize = cursor.getInt(cursor.getColumnIndexOrThrow(GameData.COLUMN_SIZE));
                long createdTime = cursor.getLong(cursor.getColumnIndexOrThrow(GameData.COLUMN_TIME));
                long gamingTime = cursor.getLong(cursor.getColumnIndexOrThrow(GameData.COLUMN_GAMING));
                gameData.setId(id);
                gameData.setArray(gameDataString);
                gameData.setGameScore(gameScore);
                gameData.setMoveNum(moveNum);
                gameData.setGridSize(gridSize);
                gameData.setCreatedTime(createdTime);
                gameData.setGamingTime(gamingTime);

                GameDataList.add(gameData);
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
     * @param id update row id （需要更新的ID）
     * @param gameData update value （去更新数据库的内容）
     * @return the number of rows affected (影响到的行数，如果没更新成功，返回0。所以当return 0时，需要告诉用户更新不成功)
     */
    public int updateBusinessCard(int id, GameData gameData) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GameData.COLUMN_DATA, gameData.getGameDataAsString());
        values.put(GameData.COLUMN_SCORE, gameData.getGameScore());
        values.put(GameData.COLUMN_MOVE, gameData.getMoveNum());
        values.put(GameData.COLUMN_SIZE, gameData.getGridSize());
        values.put(GameData.COLUMN_TIME, gameData.getCreatedTime());
        values.put(GameData.COLUMN_GAMING, gameData.getGamingTime());
        int idReturnByUpdate = db.update(GameData.TABLE_NAME, values, GameData.COLUMN_ID + " =? ", new String[]{String.valueOf(id)});
        db.close();
        return idReturnByUpdate;
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

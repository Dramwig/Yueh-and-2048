package com.example.a2048;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class StorageActivity extends AppCompatActivity implements View.OnClickListener, CornerRadiusUpdater{

    public void updateCornerRadius(float radius){};

    private static final int SAVE_BOX_NUM = 6;
    private final TextView[] save_box_text_score = new TextView[SAVE_BOX_NUM];
    private final TextView[] save_box_text_move_times = new TextView[SAVE_BOX_NUM];
    private final TextView[] save_box_text_save_time = new TextView[SAVE_BOX_NUM];
    private final GameView[] save_box_grid_game_view = new GameView[SAVE_BOX_NUM];
    private final View[] save_box_view_background = new View[SAVE_BOX_NUM];
    Map<Integer, Integer> idToIndex = new HashMap<>();
    int adhocColor = Color.parseColor("#FFFFEFAF");
    int unchosenColor = Color.WHITE;
    int chosenColor = Color.GRAY;
    private int chosenIndex = -1;
    GameData gameData;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storagepage);

        setWallpaperFromInternalStorage();

        // 高斯模糊
        final View backgroundView = findViewById(R.id.background_view_to_blur);
        backgroundView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 确保移除监听器，避免多次触发
                backgroundView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // 高斯模糊背景
                BlurBGImageView blurImageView;
                blurImageView = findViewById(R.id.background_view_to_blur);
                blurImageView.refreshPartialBG(backgroundView, 20, 20);
            }
        });

        // 设置每个save box
        for (int i = 0; i < SAVE_BOX_NUM; i++) {
            String boxId = "save_box" + i;
            @SuppressLint("DiscouragedApi") int resID = getResources().getIdentifier(boxId, "id", getPackageName());
            GridLayout save_box = findViewById(resID);

            // 初始化索引，设置默认内容
            save_box_text_score[i] = save_box.findViewById(R.id.text_score);
            save_box_text_score[i].setText("得分：无");

            save_box_text_move_times[i] = save_box.findViewById(R.id.text_move_times);
            save_box_text_move_times[i].setText("滑动次数：无");

            save_box_text_save_time[i] = save_box.findViewById(R.id.text_save_time);
            save_box_text_save_time[i].setText("No Date");

            save_box_grid_game_view[i] = save_box.findViewById(R.id.grid_game_view);

            save_box_view_background[i] = save_box.findViewById(R.id.view_background);
            save_box_view_background[i].setBackgroundColor(i == 0 ? adhocColor : unchosenColor);

            save_box.setOnClickListener(this);
            idToIndex.put(save_box.getId(), i);

            // 在视图树布局完成后加载每个"save box"的数据，不然会崩溃
            final int index = i;
            save_box_grid_game_view[i].getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    save_box_grid_game_view[index].getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    loadSaveBox(index);
                }
            });
        }

        // 设置确认按钮
        RelativeLayout bnt_text_yes = findViewById(R.id.bnt_text_yes);
        bnt_text_yes.setOnClickListener(this);
        idToIndex.put(bnt_text_yes.getId(), -1);

        // 设置取消按钮
        RelativeLayout bnt_text_no = findViewById(R.id.bnt_text_no);
        bnt_text_no.setOnClickListener(this);
        idToIndex.put(bnt_text_no.getId(), -2);

        // 读取存档数据
        Intent intent = getIntent();
        gameData = (GameData) intent.getSerializableExtra("gameData");
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        int index = idToIndex.get(id);
        if (index == -2) {
            onBackPressed(); //返回
        } else if (index == -1) {
            if (chosenIndex != -1 && gameData != null) {
                // 存
                saveGameData(chosenIndex, gameData);
                loadSaveBox(chosenIndex);
            } else if (chosenIndex != -1 && gameData == null) {
                // 取
                gameData = loadGameData(chosenIndex);
                Intent intent = new Intent();
                intent.putExtra("result", gameData);
                setResult(RESULT_OK, intent);
                finish();
            }
        } else if (index >= 0 && index < SAVE_BOX_NUM) {
            if (index != chosenIndex && !(gameData != null && index == 0)) {
                if (chosenIndex != -1)
                    save_box_view_background[chosenIndex].setBackgroundColor(chosenIndex == 0 ? adhocColor : unchosenColor);
                chosenIndex = index;
                save_box_view_background[chosenIndex].setBackgroundColor(chosenColor);
            }
        }

    }

    private void loadSaveBox(int index) {
        GameData gameData = loadGameData(index);
        if (gameData != null) {
            loadSaveBox(index, gameData);
        }
    }

    private void loadSaveBox(int index, GameData gameData) {
        try {
            save_box_grid_game_view[index].setGameData(gameData);
            save_box_text_score[index].setText("得分：" + gameData.getGameScore());
            save_box_text_move_times[index].setText("滑动次数：" + gameData.getMoveNum());
            save_box_text_save_time[index].setText(gameData.getCreatedTimeAsString());
        }catch (Exception e){
            e.printStackTrace();
            save_box_text_save_time[index].setText(e.getMessage());
            saveGameData(index, null);
            System.out.println(index+"号存档覆盖为空：读出错误");
        }
    }

    public void saveGameData(int index, GameData gameData) {
        saveGameData(this, "save_box" + index, gameData);
    }
    public void saveGameData(Context context, String key, GameData gameData) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("gameData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(gameData);
        editor.putString(key, json);
        editor.apply();
    }


    public GameData loadGameData(int index) {
        return loadGameData(this, "save_box" + index);
    }

    public GameData loadGameData(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("gameData", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        Type type = new TypeToken<GameData>() {
        }.getType();
        return gson.fromJson(json, type);
    }


    // -------------------

    private void setWallpaperFromInternalStorage() {
        ImageView imageView = findViewById(R.id.background_view_to_blur);
        try {
            InputStream inputStream = openFileInput("wallpaper_last.jpg");
            Bitmap bitmap_bg = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap_bg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }

    }

    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
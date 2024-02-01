package com.example.a2048;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;

public class GameActivity extends AppCompatActivity implements CornerRadiusUpdater {
    private GameView gameView;
    private GameActivity gamePage = this;
    TextView text_score, text_move, text_status;
    private static final int REQUEST_CODE = 1;
    com.example.a2048.RectangularBorderLayout rectangularBorderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gamepage);

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

        text_move = findViewById(R.id.text_move);
        text_score = findViewById(R.id.text_score);
        text_status = findViewById(R.id.text_status);

        rectangularBorderLayout = findViewById(R.id.border_game_view);
        gameView = findViewById(R.id.grid_game_view);
        View view = gameView;
        final ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = view.getWidth();
                int gridSize = gameView.gridSize; // Assuming gridSize is a property of gameView
                int newWidth = (int) width / gridSize * gridSize;
                int delWith = width - newWidth;
                Log.d("RectangularBorderLayout", "width=" + width + ", newWidth=" + newWidth);
                if (delWith != 0) {
                    delWith += rectangularBorderLayout.getLayoutMargin();
                    rectangularBorderLayout.setMargins(delWith / 2, delWith / 2, delWith - delWith / 2, delWith - delWith / 2);
                }
            }
        });


        // 【个性化界面】读取存档数据展示
        Intent intent = getIntent();
        int data1 = intent.getIntExtra("data1", gameView.gridSize); // 使用默认值
        int data2 = intent.getIntExtra("data2", gameView.addRandomCardNum); // 使用默认值
        gameView.resetGame(data1);
        gameView.setAddRandomCardNum(data2);

        // 返回按钮
        ImageButton btn_return = findViewById(R.id.button_return);
        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //重新开始按钮
        ImageButton btn_restart = findViewById(R.id.button_restart);
        btn_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameView.restartGame();
            }
        });

        //保存按钮
        ImageButton btn_save = findViewById(R.id.button_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GameData gameData = gameView.getGameData();
                Intent intent = new Intent(GameActivity.this, StorageActivity.class);
                intent.putExtra("gameData", gameData);
                startActivity(intent);
            }
        });

        //读取按钮
        ImageButton btn_load = findViewById(R.id.button_load);
        btn_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 启动目标活动
                Intent intent = new Intent(GameActivity.this, StorageActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        //撤回按钮
        ImageButton btn_revoke = findViewById(R.id.button_revoke);
        btn_revoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameView.undoToPreviousStatus();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // 处理返回结果
                GameData resultGameData = (GameData) data.getSerializableExtra("result");
                // 进行相应的操作
                gameView.setGameData(resultGameData);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (gameView.getMoveNum() > 0) {
            saveGameData();
            Toast.makeText(GameActivity.this, "已自动保存", Toast.LENGTH_SHORT).show();
        }
        setResult(chosenActivity.RESULT_CLOSE_CHOSEN_ACTIVITY);
        finish();  // 关闭当前的 GameActivity
    }

    public void updateScoreView() {
        text_score.setText("" + gameView.getGameScore());
    }

    public void updateMoveView() {
        text_move.setText("" + gameView.getMoveNum());
    }

    public void updatePreviousStatusNum(int num) {
        text_status.setText("" + num);
    }

    public void updateCornerRadius(float radius) {
        if (rectangularBorderLayout == null)
            rectangularBorderLayout = findViewById(R.id.border_game_view);
        rectangularBorderLayout.setInsideCornerRadius(radius);
    }

    public void exchangeMode(boolean isTrainingMode) {
        TextView top_text = findViewById(R.id.top_text);
        top_text.setText(isTrainingMode ? "得分 (训练)" : "得分");
    }

    public void saveGameData() {
        int index = 0;
        GameData gameData = gameView.getGameData();
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

    // ----

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

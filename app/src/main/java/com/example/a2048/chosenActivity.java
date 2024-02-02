package com.example.a2048;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;

public class chosenActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_CODE_GAME_ACTIVITY = 1, RESULT_CLOSE_CHOSEN_ACTIVITY = 1;
    SeekBar seekBar[] = new SeekBar[2];
    EditText editTextProgress[] = new EditText[2];
    TextView textView[] = new TextView[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chosenpage);

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

        textView[0] = findViewById(R.id.settext1);
        textView[1] = findViewById(R.id.settext2);
        CharSequence string[] = {textView[0].getText(),textView[1].getText()};
        seekBar[0] = findViewById(R.id.seekBar1);
        seekBar[1] = findViewById(R.id.seekBar2);
        editTextProgress[0] = findViewById(R.id.editTextProgress1);
        editTextProgress[1] = findViewById(R.id.editTextProgress2);

        for(int i=0;i<=1;i++) {
            final int finalI = i;  // 将 i 声明为 final
            seekBar[finalI].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    // 当 SeekBar 进度变化时执行的操作
                    try {
                        // Toast.makeText(chosenActivity.this, "" + progress, Toast.LENGTH_SHORT).show();
                        Editable editable = editTextProgress[finalI].getText();
                        if (editable == null || !editable.toString().equals(String.valueOf(progress))) {
                            // 更新 EditText 的值，因为 SeekBar 的值与 EditText 中的值不相等
                            editTextProgress[finalI].setText(String.valueOf(progress));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            editTextProgress[finalI].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                    // 在文本变化前执行的操作
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    // 在文本变化时执行的操作
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void afterTextChanged(Editable editable) {
                    // 在文本变化后执行的操作
                    try {
                        // 尝试将文本转换为整数
                        int progress = Integer.parseInt(editable.toString());

                        // 确保进度在合法范围内
                        if (progress < seekBar[finalI].getMin()) {
                            seekBar[finalI].setProgress(progress);
                            editTextProgress[finalI].setText(String.valueOf(seekBar[finalI].getMin()));
                            textView[finalI].setText(string[finalI]);
                            textView[finalI].setTextColor(Color.BLACK);
                        } else if (progress <= seekBar[finalI].getMax()) {
                            // 更新 SeekBar 的进度
                            seekBar[finalI].setProgress(progress);
                            textView[finalI].setText(string[finalI]);
                            textView[finalI].setTextColor(Color.BLACK);
                        } else if(progress<=50){
                            //seekBar[finalI].setProgress(progress);
                            textView[finalI].setText(string[finalI] + " ⚠");
                            textView[finalI].setTextColor(Color.RED);
                        }else{
                            editTextProgress[finalI].setText(String.valueOf(50));
                            textView[finalI].setText(string[finalI] + " ⚠");
                            textView[finalI].setTextColor(Color.RED);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        textView[finalI].setText(string[finalI] + " ⚠");
                        textView[finalI].setTextColor(Color.RED);
                    } catch (Exception e) {
                        e.printStackTrace();
                        textView[finalI].setText(string[finalI] + " ⚠");
                        textView[finalI].setTextColor(Color.RED);
                    }
                }
            });
        }

        // 设置确认按钮
        RelativeLayout bnt_text_yes = findViewById(R.id.bnt_text_yes);
        bnt_text_yes.setOnClickListener(this);

        // 设置取消按钮
        RelativeLayout bnt_text_no = findViewById(R.id.bnt_text_no);
        bnt_text_no.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.bnt_text_yes) {
            Intent intent = new Intent(chosenActivity.this, GameActivity.class);
            try {
                int data1 = Integer.parseInt(editTextProgress[0].getText().toString());
                int data2 = Integer.parseInt(editTextProgress[1].getText().toString());

                intent.putExtra("data1", data1);
                intent.putExtra("data2", data2);

                // 使用 startActivityForResult 启动 GameActivity
                startActivityForResult(intent, REQUEST_CODE_GAME_ACTIVITY);
            } catch (NumberFormatException e) {
                // 处理无法解析的文本，可以在这里添加适当的错误处理逻辑
                e.printStackTrace();
            }
        } else if (id == R.id.bnt_text_no) {
            onBackPressed();
        }

    }

    // 在 chosenActivity 中接收 GameActivity 的返回结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_GAME_ACTIVITY) {
            // 根据 resultCode 判断是否需要关闭 chosenActivity
            if (resultCode == RESULT_CLOSE_CHOSEN_ACTIVITY) {
                finish();  // 关闭当前的 chosenActivity
            }
        }
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
package com.example.a2048;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;


public class SplashActivity extends AppCompatActivity {

    private static final long DELAY_TIME = 2000; // 设置延迟时间，单位为毫秒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashpage);

        // 设置 ImageView
        String resourceName = null;
        try {
            ImageView pic = findViewById(R.id.splash_pic);
            Random random = new Random();
            int randomIndex = random.nextInt(13) + 1;
            resourceName = "sp" + randomIndex;
            int resourceId = getResources().getIdentifier(resourceName, "drawable", getPackageName());
            pic.setImageResource(resourceId);
        } catch (Exception e) {
            Toast.makeText(SplashActivity.this, "随机splash错误:" + resourceName, Toast.LENGTH_SHORT).show();
        }

        // 时延
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(SplashActivity.this, "转跳主页面错误", Toast.LENGTH_SHORT).show();
                }
                // 结束当前 Activity
                finish();
            }
        }, DELAY_TIME);
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

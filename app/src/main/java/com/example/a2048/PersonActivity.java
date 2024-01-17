package com.example.a2048;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;

public class PersonActivity extends AppCompatActivity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personpage);

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
    }

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
}

package com.example.a2048;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.imageview.ShapeableImageView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class MainActivity extends Activity implements View.OnClickListener {

    @SuppressLint({"WrongThread", "ScheduleExactAlarm"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        // 设置 ImageView
        Random random = new Random();
        int randomIndex = random.nextInt(100);
        if (randomIndex <= 5) {
            String resourceName = null;
            try {
                ImageView pic = findViewById(R.id.background_view_to_blur);
                randomIndex = random.nextInt(5) + 1;
                resourceName = "bg" + randomIndex;
                int resourceId = getResources().getIdentifier(resourceName, "drawable", getPackageName());
                pic.setImageResource(resourceId);
                //保存为last
                BitmapDrawable drawable = (BitmapDrawable) pic.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                if (bitmap != null) {
                    saveImageToInternalStorage(bitmap, "wallpaper_last.jpg");
                }
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "随机背景错误:" + resourceName, Toast.LENGTH_SHORT).show();
            }
        } else {
            setWallpaperFromDownloads();
        }
        reserveWallpaperFromInternalStorage();

        // 设置两个按钮背景
        final View backgroundView = findViewById(R.id.background_view_to_blur);
        final BlurBGImageView blurImageView1 = findViewById(R.id.button_select_bg1);
        final BlurBGImageView blurImageView2 = findViewById(R.id.button_select_bg2);
        // backgroundView 树布局变化时就会触发
        ViewTreeObserver viewTreeObserver = backgroundView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (blurImageView1.getWidth() > 0 && blurImageView2.getWidth() > 0) {
                    // 高斯模糊 button_bg1
                    blurImageView1.refreshPartialBG(backgroundView, 10, 1);
                    // 高斯模糊 button_bg2
                    blurImageView2.refreshPartialBG(backgroundView, 10, 1);
                }
            }
        });

        // 设置按钮点击事件
        Button button1 = findViewById(R.id.btn_startGame_common);
        button1.setOnClickListener(this);
        Button button2 = findViewById(R.id.btn_startGame_train);
        button2.setOnClickListener(this);
        ShapeableImageView imageButton = findViewById(R.id.imageView);
        imageButton.setOnClickListener(this);
        ImageButton imageButton1 = findViewById(R.id.button_info);
        imageButton1.setOnClickListener(this);
        ImageButton imageButton2 = findViewById(R.id.button_collection);
        imageButton2.setOnClickListener(this);
        ImageButton imageButton3 = findViewById(R.id.button_data);
        imageButton3.setOnClickListener(this);

        try {


        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Calendar", "Error: " + e.getMessage(), e);
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent intent = new Intent();
        if (id == R.id.btn_startGame_common) {
            intent.setClass(this, GameActivity.class);//也可以这样写intent.setClass(MainActivity.this, OtherActivity.class);
            startActivity(intent);
        } else if (id == R.id.btn_startGame_train) {
            intent.setClass(this, chosenActivity.class);//也可以这样写intent.setClass(MainActivity.this, OtherActivity.class);
            startActivity(intent);
        } else if (id == R.id.imageView) {
            intent.setClass(this, PersonActivity.class);//也可以这样写intent.setClass(MainActivity.this, OtherActivity.class);
            startActivity(intent);
        } else if (id == R.id.button_info) {
            NotificationHelper.sendNotification(this, "1 Title", "1 Text", NotificationHelper.CHANNEL_ID_Achievement);
        } else if (id == R.id.button_collection) {
            NotificationHelper.sendNotification(this, "2 Title", "2 Text", NotificationHelper.CHANNEL_ID_NightSpeech);
        } else if (id == R.id.button_data) {
            NotificationHelper.sendNotification(this, "4 Title", "4 Text", NotificationHelper.CHANNEL_ID_Message);
        } else {
            Log.e("onClick", "onClick识别到未知的id");
        }

    }

    // --------

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

    // -----

    private void setWallpaperFromDownloads() {
        ImageView imageView = findViewById(R.id.background_view_to_blur);
        try {
            InputStream inputStream = openFileInput("wallpaper.jpg");
            Bitmap bitmap_bg = BitmapFactory.decodeStream(inputStream);
            if (bitmap_bg != null) {
                saveImageToInternalStorage(bitmap_bg, "wallpaper_last.jpg");
                imageView.setImageBitmap(bitmap_bg);
            }
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "背景设置错误", Toast.LENGTH_SHORT).show();
        }
    }

    private void reserveWallpaperFromInternalStorage() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                String imageUrl = "https://source.unsplash.com/random";
                downloadImage(imageUrl);
            }
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "背景下载错误", Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressLint("StaticFieldLeak")
    private void downloadImage(String imageUrl) {
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... urls) {
                String imageUrl = urls[0];
                Bitmap bitmap = null;
                try {
                    URL url = new URL(imageUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream inputStream = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                if (result != null) {
                    saveImageToInternalStorage(result, "wallpaper.jpg");
                }
            }
        }.execute(imageUrl);
    }

    private void saveImageToInternalStorage(Bitmap bitmap, String filename) {
        try {
            FileOutputStream outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}



package com.example.a2048;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class BlurBGImageView extends ImageView {
    Bitmap overlay;
    int scaleFactor = 2;
    int radius = 8;

    public BlurBGImageView(Context context) {
        super(context);
    }

    public BlurBGImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BlurBGImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScaleFactor(int scaleFactor) {
        this.scaleFactor = scaleFactor;
    }


    public void refreshBG(View bgView, int radius, int scaleFactor) {
        bgView.setDrawingCacheEnabled(true);
        bgView.buildDrawingCache();
        Bitmap bitmap1 = null;
        try {
            bitmap1 = getBitmap(bgView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bitmap1 != null) {
            blur(bitmap1, this, radius, scaleFactor);//模糊处理
            bitmap1.recycle();
        }
        bgView.setDrawingCacheEnabled(false);//清除缓存
    }

    public void refreshPartialBG(View bgView, int radius, int scaleFactor) {
        bgView.setDrawingCacheEnabled(true);
        bgView.buildDrawingCache();
        Bitmap bitmap1 = null;
        try {
            bitmap1 = getPartialBitmap(bgView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bitmap1 != null) {
            blur(bitmap1, this, radius, scaleFactor);//模糊处理
            bitmap1.recycle();
        }
        bgView.setDrawingCacheEnabled(false);//清除缓存
    }

    private void blur(Bitmap bkg, ImageView view, float radius, int scaleFactor) {
        if (overlay != null) {
            overlay.recycle();
        }
        overlay = Bitmap.createScaledBitmap(bkg, bkg.getWidth() / scaleFactor, bkg.getHeight() / scaleFactor, false);
        overlay = blur(getContext(), overlay, radius);//高斯模糊
        view.setImageBitmap(overlay);
    }

    private Bitmap blur(Context context, Bitmap image, float radius) {
        RenderScript rs = RenderScript.create(context);
        Bitmap outputBitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
        Allocation in = Allocation.createFromBitmap(rs, image);
        Allocation out = Allocation.createFromBitmap(rs, outputBitmap);

        ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        intrinsicBlur.setRadius(radius);
        intrinsicBlur.setInput(in);
        intrinsicBlur.forEach(out);

        out.copyTo(outputBitmap);
        image.recycle();
        rs.destroy();
        return outputBitmap;
    }

    private Bitmap getBitmap(View view) {
        int width = view.getWidth();
        int height = view.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private Bitmap getPartialBitmap(View view) {

        // 获取 this 的位置信息
        int[] location1 = new int[2];
        view.getLocationOnScreen(location1);
        int x1 = location1[0];
        int y1 = location1[1];

        // 获取 view 的位置信息
        int[] location2 = new int[2];
        this.getLocationOnScreen(location2);
        int x2 = location2[0];
        int y2 = location2[1];

        // 获取 view 的宽度和高度
        int relativeWidth = this.getWidth();
        int relativeHeight = this.getHeight();

        // 计算 view 相对于 this 的位置和大小
        int relativeX = x2 - x1;
        int relativeY = y2 - y1;

        Bitmap bitmap = Bitmap.createBitmap(relativeWidth, relativeHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        //canvas.drawBitmap(view.getDrawingCache(), relativeX, relativeY, null); // 绘制指定区域
        canvas.translate(-relativeX, -relativeY); // 将画布平移以绘制指定区域
        view.draw(canvas);
        canvas.translate(relativeX, relativeY); // 恢复画布位置
        return bitmap;
    }


}


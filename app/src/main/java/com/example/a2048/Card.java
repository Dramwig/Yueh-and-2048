package com.example.a2048;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class Card extends FrameLayout {

    TextView textView;

    private int num;

    private float cardRadius;

    static Map<Integer, Integer> backgroundColorIdMap = new HashMap<>();
    static Map<Integer, Integer> textColorIdMap = new HashMap<>();

    static {
        textColorIdMap.put(0, R.color.textColor0);
        textColorIdMap.put(2, R.color.textColor2);
        textColorIdMap.put(4, R.color.textColor4);

        backgroundColorIdMap.put(0, R.color.backgroundColor0);
        backgroundColorIdMap.put(2, R.color.backgroundColor2);
        backgroundColorIdMap.put(4, R.color.backgroundColor4);
        backgroundColorIdMap.put(8, R.color.backgroundColor8);
        backgroundColorIdMap.put(16, R.color.backgroundColor16);
        backgroundColorIdMap.put(32, R.color.backgroundColor32);
        backgroundColorIdMap.put(64, R.color.backgroundColor64);
        backgroundColorIdMap.put(128, R.color.backgroundColor128);
        backgroundColorIdMap.put(256, R.color.backgroundColor256);
        backgroundColorIdMap.put(512, R.color.backgroundColor512);
        backgroundColorIdMap.put(1024, R.color.backgroundColor1024);
    }

    private GradientDrawable gradientDrawable = new GradientDrawable();
    private float zoomRatio = 1;

    public Card(@NonNull Context context) {
        super(context);

        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setCornerRadius(getResources().getDimension(R.dimen.game_card_radius));

        textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setText(getNum() + "");
        textView.setTextSize((int) (50 * zoomRatio));
        this.setBackgroundResource(R.drawable.card_square);

        setNum(0);

        LayoutParams lp = new LayoutParams(-1, -1);
        //lp.setMargins(10, 10, 10, 10);

        addView(textView, lp);
    }

    public Card(@NonNull Context context, int width, boolean isInGame) {
        super(context);
        zoomRatio = 1f * px2dp((Activity) context, 1f * width) / (200 * 160 / 420); // 控制比例

        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        //gradientDrawable.setCornerRadius(isInGame ? getResources().getDimension(R.dimen.game_card_radius) : getResources().getDimension(R.dimen.button_radius));
        cardRadius = getResources().getDimension(R.dimen.game_card_radius) * zoomRatio;
        gradientDrawable.setCornerRadius(cardRadius);

        textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setText(getNum() + "");
        textView.setTextSize((int) (50 * zoomRatio));
        this.setBackgroundResource(R.drawable.card_square);

        setNum(0);

        LayoutParams lp = new LayoutParams(-1, -1);
        //lp.setMargins(10, 10, 10, 10);

        addView(textView, lp);
    }

    public float getCornerRadius(){
        return cardRadius;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        try {
            this.num = num;
            //数字改变时，同时改变改变字体大小和颜色
            updateView();
        }catch (Exception e) {
            Toast.makeText(getContext(), "setNum " + num + " 报错", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateView() {
        try {
            textView.setText(num + "");
            changeColor(num);
            changeSize(num);
        } catch (Exception e) {
            Toast.makeText(getContext(), "updateView " + num + " 报错", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeSize(int num) {
        // 以改为dp为单位
        if (num >= 1024) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (int) (25 * zoomRatio * 0.9));
        } else if (num >= 128) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (int) (35 * zoomRatio * 0.9));
        } else if (num >= 16) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (int) (42 * zoomRatio * 0.9));
        } else {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (int) (50 * zoomRatio * 0.9));
        }
    }

    private void changeColor(int num) {

        if (num > 16) {
            textView.setTextColor(getResources().getColor(R.color.textColorCommon));
        } else if (num > 0) {
            textView.setTextColor(getResources().getColor(R.color.black));
        } else {
            textView.setTextColor(getResources().getColor(R.color.textColor0));
        }

        if (num >= 2048) {
            gradientDrawable.setColor(getResources().getColor(R.color.backgroundColorBiggerThan2048));
        } else {
            gradientDrawable.setColor(getResources().getColor(backgroundColorIdMap.get(num)));
        }
        this.setBackground(gradientDrawable);
    }

    // 定义一个函数，接受一个px值作为参数，返回一个dp值
    public static int px2dp(Activity activity, float pxValue) {
        // 获取DisplayMetrics对象
        DisplayMetrics dm = new DisplayMetrics();
        // 从activity中获取当前屏幕的信息
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        // 获取当前屏幕的dpi
        int dpi = dm.densityDpi;
        // 根据换算公式计算dp值，并四舍五入取整
        int dpValue = Math.round(pxValue * 160 / dpi);
        // 返回dp值
        return dpValue;
    }
    public static int dp2px(Activity activity, float dpValue) {
        // 获取DisplayMetrics对象
        DisplayMetrics dm = new DisplayMetrics();
        // 从activity中获取当前屏幕的信息
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        // 获取当前屏幕的密度
        float density = dm.density;
        // 根据换算公式计算px值，并四舍五入取整
        int pxValue = Math.round(dpValue * density);
        // 返回px值
        return pxValue;
    }

}

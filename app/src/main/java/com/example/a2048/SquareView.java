package com.example.a2048;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class SquareView extends View {
    public SquareView(Context context) {
        super(context);
    }

    public SquareView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        // 让宽和高为小的那个值
        if (widthMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.EXACTLY) {
            int width = widthMode == MeasureSpec.EXACTLY ? MeasureSpec.getSize(widthMeasureSpec) : MeasureSpec.getSize(heightMeasureSpec);
            int height = heightMode == MeasureSpec.EXACTLY ? MeasureSpec.getSize(heightMeasureSpec) : MeasureSpec.getSize(widthMeasureSpec) ;
            height = width = Math.min(width, height);
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
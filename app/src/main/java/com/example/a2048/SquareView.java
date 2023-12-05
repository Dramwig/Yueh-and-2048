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

        //如果宽度指定特定值，并且高度未指定特定值（让高度等于宽度就保证了宽高相等）
        if (widthMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.EXACTLY) {
            int width = widthMode == MeasureSpec.EXACTLY ? MeasureSpec.getSize(widthMeasureSpec) : 0;
            int height = heightMode == MeasureSpec.EXACTLY ? MeasureSpec.getSize(heightMeasureSpec) : 0;
            width = Math.max(width, height);
            height = width;
            if (heightMode == MeasureSpec.AT_MOST) {//这里还考虑了高度受上限的情况（比如父容器固定了高度）
                height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
            }
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
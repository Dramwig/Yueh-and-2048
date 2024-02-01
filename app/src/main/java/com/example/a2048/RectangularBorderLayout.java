package com.example.a2048;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

public class RectangularBorderLayout extends RelativeLayout {

    private int backgroundColor, frameWidth, frameColor, DEFAULT_FrameWidth, layoutMargin;
    private float cornerRadius;
    private GradientDrawable gradientDrawable;

    public RectangularBorderLayout(Context context) {
        super(context);
        init(context);
    }

    public RectangularBorderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RectangularBorderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // Create a GradientDrawable object
        gradientDrawable = new GradientDrawable();

        // Set the shape to rectangle
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);

        // Set the background color
        backgroundColor = context.getResources().getColor(R.color.game_box_background);
        gradientDrawable.setColor(backgroundColor);

        // Set the padding (make it consistent with the border width)
        // Set the rectangle border width and color
        DEFAULT_FrameWidth = (int) context.getResources().getDimension(R.dimen.game_box_frame);
        frameWidth = DEFAULT_FrameWidth;
        frameColor = context.getResources().getColor(R.color.game_box_frame);
        gradientDrawable.setStroke(frameWidth, frameColor);
        setPadding(frameWidth, frameWidth, frameWidth, frameWidth);

        // Set the corner radius (you can dynamically set it if needed)
        cornerRadius = context.getResources().getDimension(R.dimen.game_box_radius);
        gradientDrawable.setCornerRadius(cornerRadius);

        // Set the background as GradientDrawable
        setBackground(gradientDrawable);

        // Add debugging information
        Log.d("RectangularBorderLayout", "Background color: " + backgroundColor);
        Log.d("RectangularBorderLayout", "Frame width: " + frameWidth);
        Log.d("RectangularBorderLayout", "Frame color: " + frameColor);
        Log.d("RectangularBorderLayout", "Corner radius: " + cornerRadius);

        final int[] width = {0};
        View view = this;
        final ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int newWidth = view.getWidth();
                if (width[0] != newWidth) {
                    width[0] = newWidth;
                    Log.d("RectangularBorderLayout", "Width: " + getWidth());
                    setFrameWidth((int) (1F * width[0] / 974 * DEFAULT_FrameWidth));
                }
            }
        });
    }

    // You can also provide a method to dynamically set the corner radius at runtime
    public void setCornerRadius(float radius) {
        if (cornerRadius != radius) {
            cornerRadius = radius;
            gradientDrawable.setCornerRadius(cornerRadius);
            Log.d("RectangularBorderLayout", "Set corner radius: " + cornerRadius);
        }
    }

    public void setInsideCornerRadius(float radius) {
        setCornerRadius(radius + 1f * frameWidth / 2);
    }

    public void setFrameWidth(int width) {
        if (frameWidth != width) {
            frameWidth = width;
            gradientDrawable.setStroke(width, frameColor);
            setPadding(width, width, width, width);
            Log.d("RectangularBorderLayout", "Set frame width: " + frameWidth);
        }
    }

    // 函数来更改layout_margin
    void setMargins(int left, int top, int right, int bottom) {
        // Get the current layout parameters
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
        // Ensure layout parameters are not null
        if (layoutParams != null) {
            layoutMargin = left + right;
            // Set the new margins
            layoutParams.setMargins(left, top, right, bottom);
            // Apply the layout parameters
            setLayoutParams(layoutParams);
            // Request a layout update
            requestLayout();
            Log.d("RectangularBorderLayout", "Set Margins: left=" + left + ", top=" + top + ", right=" + right + ", bottom=" + bottom);
        }
    }

    public int getLayoutMargin() {
        return layoutMargin;
    }


}
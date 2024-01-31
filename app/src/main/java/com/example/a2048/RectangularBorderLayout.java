package com.example.a2048;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

public class RectangularBorderLayout extends RelativeLayout {

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
        GradientDrawable gradientDrawable = new GradientDrawable();

        // Set the shape to rectangle
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);

        // Set the padding (make it consistent with the border width)
        int padding = (int) context.getResources().getDimension(R.dimen.game_box_frame);
        setPadding(padding, padding, padding, padding);

        // Set the background color
        int backgroundColor = context.getResources().getColor(R.color.game_box_background);
        gradientDrawable.setColor(backgroundColor);

        // Set the rectangle border width and color
        int frameWidth = (int) context.getResources().getDimension(R.dimen.game_box_frame);
        int frameColor = context.getResources().getColor(R.color.game_box_frame);
        gradientDrawable.setStroke(frameWidth, frameColor);

        // Set the corner radius (you can dynamically set it if needed)
        float cornerRadius = context.getResources().getDimension(R.dimen.game_box_radius);
        gradientDrawable.setCornerRadius(cornerRadius);

        // Set the background as GradientDrawable
        setBackground(gradientDrawable);

        // Add debugging information
        Log.d("RectangularBorderLayout", "Background color: " + backgroundColor);
        Log.d("RectangularBorderLayout", "Frame width: " + frameWidth);
        Log.d("RectangularBorderLayout", "Frame color: " + frameColor);
        Log.d("RectangularBorderLayout", "Corner radius: " + cornerRadius);
    }

    // You can also provide a method to dynamically set the corner radius at runtime
    public void setCornerRadius(float radius) {
        GradientDrawable gradientDrawable = (GradientDrawable) getBackground();
        gradientDrawable.setCornerRadius(radius);
        Log.d("RectangularBorderLayout", "Set corner radius: " + radius);
    }
}
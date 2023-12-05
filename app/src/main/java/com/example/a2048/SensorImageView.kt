package com.example.a2048

import android.content.Context
import kotlin.jvm.JvmOverloads
import android.util.AttributeSet
import android.widget.ImageView

import android.graphics.Canvas

class SensorImageView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :  androidx.appcompat.widget.AppCompatImageView(context!!, attrs, defStyleAttr) {

    private var mDrawableWidth = 0
    private var mDrawableHeight = 0

    private var mWidth = 0
    private var mHeight = 0

    private var mMaxOffset = 0f

    private var mProgress = 0f

    fun updateProgress(progress: Float) {
        mProgress = progress;
        invalidate();
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
        mHeight = MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom
        if (drawable != null) {
            mDrawableWidth = drawable.intrinsicWidth
            mDrawableHeight = drawable.intrinsicHeight
            val imgScale = mHeight.toFloat() / mDrawableHeight.toFloat()
            mMaxOffset = Math.abs((mDrawableWidth * imgScale - mWidth) * 0.5f)
        }
    }

    override fun onDraw(canvas: Canvas) {
        if ( drawable == null ) {
            super.onDraw(canvas)
            return
        }
        val currentOffsetX = mMaxOffset * mProgress
        canvas.translate(currentOffsetX, 0f)
        super.onDraw(canvas)

    }

    init {
        super.setScaleType(ScaleType.CENTER_CROP)
    }
}

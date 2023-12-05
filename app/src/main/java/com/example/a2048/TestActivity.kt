package com.example.a2048

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class TestActivity : AppCompatActivity(), SensorEventListener {
    private val NS2S: Float = 1.0f / 1000000000.0f
    private var mLastTimestamp: Long = 0
    private var mRotateRadianY: Double = 0.0
    private var mMaxRotateRadian = Math.PI / 9

    private lateinit var imageView: SensorImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.testpage)

        imageView = findViewById<View>(R.id.panorama_image_view) as SensorImageView

        var manager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        var defaultSensor = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        try{
            manager.registerListener(this, defaultSensor, SensorManager.SENSOR_DELAY_FASTEST)
        } catch (e: Exception) {
            Log.e("TestActivity", "注册传感器监听器时发生异常", e)
            Toast.makeText(this, "注册传感器监听器时发生异常:$manager", Toast.LENGTH_SHORT).show()
        }

        // 设置两个按钮背景

//        // 设置两个按钮背景
//        val backgroundView = findViewById<View>(R.id.panorama_image_view)
//        val blurImageView1 = findViewById<BlurBGImageView>(R.id.button_select_bg1)
//        val blurImageView2 = findViewById<BlurBGImageView>(R.id.button_select_bg2)
//        // backgroundView 树布局变化时就会触发
//        // backgroundView 树布局变化时就会触发
//        val viewTreeObserver = backgroundView.viewTreeObserver
//        viewTreeObserver.addOnGlobalLayoutListener {
//            if (blurImageView1.width > 0 && blurImageView2.width > 0) {
//                // 高斯模糊 button_bg1
//                blurImageView1.refreshPartialBG(backgroundView, 10, 1)
//                // 高斯模糊 button_bg2
//                blurImageView2.refreshPartialBG(backgroundView, 10, 1)
//            }
//        }

    }

    override fun onSensorChanged(event: SensorEvent) {

        if (mLastTimestamp == 0L) {
            mLastTimestamp = event.timestamp
            return
        }

        val rotateX = Math.abs(event.values[0])
        val rotateY = Math.abs(event.values[1])
        val rotateZ = Math.abs(event.values[2])

        if (rotateY > rotateX + rotateZ) {
            val dT: Float = (event.timestamp - mLastTimestamp) * NS2S
            mRotateRadianY += (event.values[1] * dT)
            if (mRotateRadianY > mMaxRotateRadian) {
                mRotateRadianY = mMaxRotateRadian

            } else if (mRotateRadianY < -mMaxRotateRadian) {
                mRotateRadianY = -mMaxRotateRadian
            } else {

                imageView.updateProgress((mRotateRadianY / mMaxRotateRadian).toFloat())
            }
        }
        mLastTimestamp = event.timestamp
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}


}

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

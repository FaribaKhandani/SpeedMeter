package com.example.speedmeter

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.PowerManager
import android.os.RemoteException
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.cardiomood.android.controls.gauge.SpeedometerGauge

class MainActivity : AppCompatActivity() {


    private lateinit var speedometerView: SpeedMeter
    private lateinit var tachometerView: TachMeter
    private lateinit var container: FrameLayout



    private lateinit var gestureDetector: GestureDetector

    private var dataGeneratorService: IDataGenerator? = null

    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            dataGeneratorService = IDataGenerator.Stub.asInterface(service)
            try {
                dataGeneratorService?.registerDataCallback(dataCallback)
                dataGeneratorService?.startDataGeneration()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        container = findViewById(R.id.container)
        speedometerView = findViewById(R.id.speedometer)
        tachometerView = findViewById(R.id.tachmeter)


        gestureDetector = GestureDetector(this, MyGestureListener())

        gestureDetector.setOnDoubleTapListener(MyGestureListener())

        val intent = Intent(this, DataGeneratorService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

    }

    private val dataCallback = object : IDataCallback.Stub() {
        override fun onDataGenerated(data: Int) {

            runOnUiThread {

                speedometerView.updateData(data)
                tachometerView.updateRpm(data)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
        try {
            dataGeneratorService?.unregisterDataCallback(dataCallback)
            dataGeneratorService?.stopDataGeneration()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }





    private fun switchToSpeedometerView() {
        speedometerView.visibility = View.VISIBLE
        tachometerView.visibility = View.GONE


    }

    private fun switchToTachometerView() {
        speedometerView.visibility = View.GONE
        tachometerView.visibility = View.VISIBLE

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    private inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {

            return true
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {


                val sensitivityThreshold = 20

            if (e1 != null) {
                if (e2.x - e1.x > sensitivityThreshold) {

                    switchToTachometerView()
                } else if (e2.x - e1.x < -sensitivityThreshold) {

                    switchToSpeedometerView()
                }
            }

            return true
        }


    }

}



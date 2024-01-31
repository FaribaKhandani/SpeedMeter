package com.example.speedmeter

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteCallbackList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class DataGeneratorService :Service(){
    private val callbacks = RemoteCallbackList<IDataCallback>()
    private var running = false
    private val coroutineScope = CoroutineScope(Dispatchers.Default)



   private val dataGeneratorBinder = object : IDataGenerator.Stub() {
        override fun registerDataCallback(callback: IDataCallback?) {
            callback?.let { callbacks.register(callback) }
        }

        override fun unregisterDataCallback(callback: IDataCallback?) {
            callback?.let { callbacks.unregister(callback) }
        }

       //generation data based on time
        override fun startDataGeneration() {
            running = true


            coroutineScope.launch {
                var time = 0.0
                val frameDelayMillis = 1000 / 60 // 60fps
                while (running) {



                      val sin1 = 80.0 * sin(1 * PI * time / 5.0)
                     val sin2 = 50.0 * sin(1 * PI * time / 2.0)
                    val newData = (100.0 + sin1 + sin2).toInt()
                    val n = callbacks.beginBroadcast()
                    for (i in 0 until n) {
                        try {
                            callbacks.getBroadcastItem(i)?.onDataGenerated(newData)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    callbacks.finishBroadcast()

                    delay(frameDelayMillis.toLong())
                    time += frameDelayMillis.toDouble() / 1000.0
                }
            }
        }

        override fun stopDataGeneration() {
            running = false
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return dataGeneratorBinder
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

}
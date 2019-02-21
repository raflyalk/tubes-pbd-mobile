package com.example.freya

import android.hardware.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.hardware.SensorManager
import android.util.Log


class MainActivity : AppCompatActivity() , SensorEventListener {
    private var mSensorManager : SensorManager ?= null
    private var mAccelerometer : Sensor ?= null
    private var lastUpdate : Long = 0
    private var last_x : Float = 0.0f
    private var last_y : Float = 0.0f
    private var last_z : Float = 0.0f
    private val SHAKE_THRESHOLD = 10000

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("accuracy", "nothing")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.sensor === mAccelerometer) {
            val curTime = System.currentTimeMillis()
            // only allow one update every 100ms.
            if (curTime - lastUpdate > 100) {
                val diffTime = curTime - lastUpdate
                lastUpdate = curTime

                var x = event!!.values[SensorManager.DATA_X]
                var y = event!!.values[SensorManager.DATA_Y]
                var z = event!!.values[SensorManager.DATA_Z]

                val speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000

                if (speed > SHAKE_THRESHOLD) {
                    Log.d("sensor", "shake detected w/ speed: $speed")
                    Toast.makeText(this, "shake detected w/ speed: $speed", Toast.LENGTH_SHORT).show()
                }
                last_x = x
                last_y = y
                last_z = z
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        mSensorManager!!.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME)
    }
}

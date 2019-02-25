package com.example.freya

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.hardware.SensorManager







class TemperatureCheckActivity : AppCompatActivity(), SensorEventListener {
    override fun onSensorChanged(event: SensorEvent?) {
        var currentValue = event!!.values[0]
        mTextSensorTemperature!!.text = getResources().getString(
            R.string.label_temperature, currentValue)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //    Individual temperature sensor
    private var mSensorManager: SensorManager? = null
    private var mSensorTemperature : Sensor ?= null
    private var mTextSensorTemperature: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temperature_check)

        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mTextSensorTemperature = findViewById<TextView>(R.id.label_temperature)
        mSensorTemperature = mSensorManager!!.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        var sensor_error = resources.getString(R.string.error_no_sensor) as String
        if (mSensorTemperature == null) {
            mTextSensorTemperature!!.text = sensor_error
        }
    }

    override fun onStart() {
        super.onStart()
        if (mSensorTemperature != null) {
            mSensorManager!!.registerListener(
                this, mSensorTemperature,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onStop() {
        super.onStop()
        mSensorManager!!.unregisterListener(this)
    }

}

package com.example.freya

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class MainActivity : AppCompatActivity() , SensorEventListener, GoogleApiClient.OnConnectionFailedListener {
    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d(TAG, "onConnectionFailed:$connectionResult")
    }

    private val TAG = "MainActivity"

    private var mSensorManager : SensorManager ?= null
    private var mAccelerometer : Sensor ?= null
    private var lastUpdate : Long = 0
    private var last_x : Float = 0.0f
    private var last_y : Float = 0.0f
    private var last_z : Float = 0.0f
    private val SHAKE_THRESHOLD = 10000

    private var mFirebaseAuth: FirebaseAuth? = null
    private var mFirebaseUser: FirebaseUser? = null
    private var mGoogleApiClient: GoogleApiClient? = null

    private lateinit var latestFireBtn: LinearLayout
    private lateinit var firefighterBtn: LinearLayout
    private lateinit var firstAidBtn: LinearLayout
    private lateinit var reportBtn: LinearLayout
    private lateinit var tempBtn: LinearLayout
    private lateinit var settingsBtn: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        var darkMode = sharedPref.getBoolean("example_switch", false)
        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        setContentView(R.layout.activity_main)

        latestFireBtn = findViewById(R.id.latest_fire_btn)
        latestFireBtn.setOnClickListener {
            latestFireOnClick()
        }

        firefighterBtn = findViewById(R.id.firefighter_btn)
        firefighterBtn.setOnClickListener {
            firefighterOnClick()
        }

        firstAidBtn = findViewById(R.id.first_aid_btn)
        firstAidBtn.setOnClickListener {
            firstAidOnClick()
        }

        reportBtn = findViewById(R.id.report_btn)
        reportBtn.setOnClickListener {
            reportOnClick()
        }

        tempBtn = findViewById(R.id.check_temp_btn)
        tempBtn.setOnClickListener {
            tempOnClick()
        }

        settingsBtn = findViewById(R.id.settings_btn)
        settingsBtn.setOnClickListener {
            settingsOnClick()
        }

        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseUser = mFirebaseAuth!!.currentUser

        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
            .addApi(Auth.GOOGLE_SIGN_IN_API)
            .build()

        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mSensorManager!!.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    private fun latestFireOnClick() {
        startActivity(Intent(this, LatestFireActivity::class.java))
    }

    private fun firefighterOnClick() {
        val mailClient = Intent(Intent.ACTION_SEND)
        mailClient.setType("text/html")
        mailClient.putExtra(Intent.EXTRA_EMAIL, Array<String>(1) { getString(R.string.contact_email) })
        mailClient.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.default_subject))
        mailClient.putExtra(Intent.EXTRA_TEXT, getString(R.string.default_body))
        startActivity(Intent.createChooser(mailClient, "Send Email"))
    }

    private fun firstAidOnClick() {
        startActivity(Intent(this, FirstAidActivity::class.java))
    }

    private fun reportOnClick() {
        startActivity(Intent(this, ReportActivity::class.java))
    }

    private fun tempOnClick() {
        startActivity(Intent(this, TemperatureCheckActivity::class.java))
    }

    private fun settingsOnClick() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sign_out_menu -> {
                mFirebaseAuth!!.signOut()
                Auth.GoogleSignInApi.signOut(mGoogleApiClient)
                mFirebaseUser = null
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }

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
                    reportOnClick()
                }
                last_x = x
                last_y = y
                last_z = z
            }
        }
    }
}

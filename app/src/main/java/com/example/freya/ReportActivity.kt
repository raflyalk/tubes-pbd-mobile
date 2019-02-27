package com.example.freya

import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.widget.TextView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.GeoPoint
import java.util.*

class ReportActivity : AppCompatActivity() {
    //    private lateinit var view : Button
    private lateinit var locationClient : FusedLocationProviderClient
    lateinit var geocoder : Geocoder
    private lateinit var temperatureXML : TextView
    private lateinit var locationXML : TextView
    private lateinit var dateXML : TextView
    private lateinit var geopointXML : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        var darkMode = sharedPref.getBoolean("example_switch", false)
        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        setContentView(R.layout.activity_report)
        locationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())

        locationXML = findViewById(R.id.locationRepVal)
        dateXML = findViewById(R.id.dateRepVal)
        temperatureXML = findViewById(R.id.temperatureRepVal)
        geopointXML = findViewById(R.id.geopointRepVal)

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        


    }

    fun geoLocToLoc(geoloc : GeoPoint) : String{
        val addresses = geocoder.getFromLocation(geoloc.latitude, geoloc.longitude,1)
        val location = addresses.get(0).featureName.toString()
        return(location)
    }


}

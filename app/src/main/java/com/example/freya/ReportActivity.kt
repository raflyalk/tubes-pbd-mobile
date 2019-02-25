package com.example.freya

import android.location.Geocoder
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.GeoPoint
import java.util.*

class ReportActivity : AppCompatActivity() {
    //    private lateinit var view : Button
    private lateinit var locationClient : FusedLocationProviderClient
    lateinit var geocoder : Geocoder

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
    }

    fun geoLocToLoc(geoloc : GeoPoint) : String{
        val addresses = geocoder.getFromLocation(geoloc.latitude, geoloc.longitude,1)
        val location = addresses.get(0).featureName.toString()
        return(location)
    }


}

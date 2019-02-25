package com.example.freya

import android.location.Geocoder
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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

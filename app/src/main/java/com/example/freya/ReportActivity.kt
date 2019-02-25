package com.example.freya

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class ReportActivity : AppCompatActivity() {
    //    private lateinit var view : Button
    private lateinit var locationClient : FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        locationClient = LocationServices.getFusedLocationProviderClient(this)

    }
}

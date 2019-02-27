package com.example.freya

import android.Manifest
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.util.Half.toFloat
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import java.util.*
import kotlin.collections.HashMap

class ReportActivity : AppCompatActivity() {
    //    private lateinit var view : Button
    private lateinit var locationClient : FusedLocationProviderClient
    lateinit var geocoder : Geocoder
    private lateinit var temperatureXML : TextView
    private lateinit var locationXML : TextView
    private lateinit var dateXML : TextView
    private lateinit var geopointXML : TextView
    private lateinit var reportBtn : Button
    private lateinit var mapBtn : Button
    private lateinit var geoPointVal: GeoPoint
    private lateinit var locationVal: String
    private lateinit var dateVal : Timestamp
    private lateinit var userVal : String
    private lateinit var temperatureVal : String
    private lateinit var currentTime : Date
    private var errorLoc : Boolean = true
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        ActivityCompat.requestPermissions(this,Array<String>(1){ Manifest.permission.ACCESS_FINE_LOCATION}, 1)
        var darkMode = sharedPref.getBoolean("example_switch", false)
        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        setContentView(R.layout.activity_report)
        locationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
        db = FirebaseFirestore.getInstance()
        locationXML = findViewById(R.id.locationRepVal)
        dateXML = findViewById(R.id.dateRepVal)
        temperatureXML = findViewById(R.id.temperatureRepVal)
        geopointXML = findViewById(R.id.geopointRepVal)
        reportBtn = findViewById(R.id.report_button)
        mapBtn = findViewById(R.id.loc_btn)

        currentTime = Calendar.getInstance().time
        dateXML.setText(currentTime.toString())
        temperatureXML.setText("Sensor Not Found")

        dateVal = Timestamp(currentTime)
        temperatureVal = "-"
        try {
            userVal = (FirebaseAuth.getInstance().currentUser as FirebaseUser).email as String
        }
        catch (e : Exception){
            userVal = "-"
            Toast.makeText(baseContext,"Connection Error", Toast.LENGTH_SHORT).show()
        }

        val mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        class CusLocationListener() : LocationListener{

            init{}

            override fun onLocationChanged(location: Location) {
                Toast.makeText(
                    baseContext,
                    "Location changed : Lat: " + location.latitude + "\nLng: "
                            + location.longitude, Toast.LENGTH_SHORT).show()
                val longitude = "Longitude: " + location.longitude
                val latitude = "Latitude: " + location.latitude
                geopointXML.setText(longitude + "\n" + latitude)
                geoPointVal = GeoPoint(location.latitude,location.longitude)


                //Get Location Name from Coordinate
                var locName :String = ""
                try{
                    locName = geoLocToLoc(geoPointVal)
                }
                catch(e : Exception){
                    locName = "GeoLocation Error"
                }

                locationVal = locName
                locationXML.setText(locName)

            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onProviderEnabled(provider: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onProviderDisabled(provider: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        val mLocationListener = CusLocationListener()
        try{
            mLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,5000,toFloat(10),mLocationListener
            )
        }
        catch (e : SecurityException){
            locationVal = "-"
            Toast.makeText(baseContext,"Permission Denied",Toast.LENGTH_SHORT).show()
            geopointXML.setText("Access Permission Denied")
            locationXML.setText("Access Permission Denied")
        }

        reportBtn.setOnClickListener {
            val thread = Thread {
                sendToFirebase()
            }
            thread.start()
            if (!thread.isAlive){
                Toast.makeText(baseContext,"Location still Loading",Toast.LENGTH_SHORT).show()
            }

        }


    }

    fun geoLocToLoc(geoloc : GeoPoint) : String{
        val addresses = geocoder.getFromLocation(geoloc.latitude, geoloc.longitude,1)
        val location = addresses.get(0).getAddressLine(0).toString()
        return(location)
    }

    fun sendToFirebase(){
        val docData =  HashMap<String,Any>()
        docData.put("date",dateVal)
        try{
            docData.put("geopoint",geoPointVal)
            docData.put("location",locationVal)
            errorLoc = false
        }
        catch (e : Exception){
            Log.d("Error","Location still Loading")
            errorLoc = true
            runOnUiThread(Runnable {
                Toast.makeText(baseContext,"Location still Loading",Toast.LENGTH_SHORT).show()
            })
            return
        }
        docData.put("temperature",temperatureVal)
        docData.put("user",userVal)
        db.collection("reports").document(currentTime.toString()).set(docData)
    }


}

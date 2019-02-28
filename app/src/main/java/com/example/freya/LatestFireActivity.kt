package com.example.freya

import android.location.Geocoder
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*


class LatestFireActivity : AppCompatActivity() {

    lateinit var list: MutableList<ReportModel>
    lateinit var recycle: RecyclerView
    lateinit var view: Button
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        var darkMode = sharedPref.getBoolean("example_switch", false)
        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        setContentView(R.layout.activity_latest_fire)
        recycle = findViewById<View>(R.id.recycle) as RecyclerView

        db = FirebaseFirestore.getInstance()

        fetchData()
    }

    private fun fetchData() {
        db.collection("reports")
            .get()
            .addOnSuccessListener { result ->
                list = ArrayList()
                for (document in result) {
                    val fire = ReportModel()
                    val dateTemp = document.data["date"] as Timestamp

                    fire.location = document.data["location"].toString()

                    fire.date = dateTemp.toDate().toString()
                    fire.temperature = document.data["temperature"].toString()
                    fire.user = document.data["user"].toString()
                    list.add(fire)
                }
                updateRecyclerView()
            }
            .addOnFailureListener { exception ->
                Log.w("datatata", "Error getting documents.", exception)
            }
    }

    fun updateRecyclerView() {
        try {
            val recyclerAdapter = RecyclerAdapter(list, this@LatestFireActivity)
            val recyce = GridLayoutManager(this@LatestFireActivity, 1)

            /// RecyclerView.LayoutManager recyce = new LinearLayoutManager(MainActivity.this);
            // recycle.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
            recycle.layoutManager = recyce
            recycle.itemAnimator = DefaultItemAnimator()
            recycle.adapter = recyclerAdapter
        } catch (e: Exception) {
            Toast.makeText(this@LatestFireActivity, "No Data Received", Toast.LENGTH_SHORT).show()
        }
    }
}
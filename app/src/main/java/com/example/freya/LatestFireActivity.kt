package com.example.freya

import android.location.Geocoder
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import java.util.*


class LatestFireActivity : AppCompatActivity() {

    lateinit var list: MutableList<ReportModel>
    lateinit var recycle: RecyclerView
    lateinit var view: Button
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_fire)
        view = findViewById<View>(R.id.view) as Button
        recycle = findViewById<View>(R.id.recycle) as RecyclerView

        db = FirebaseFirestore.getInstance()

        fetchData()

        view.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                fetchData()
            }
        })
    }

    private fun fetchData() {
        db.collection("reports")
            .get()
            .addOnSuccessListener { result ->
                list = ArrayList()
                for (document in result) {
                    val fire = ReportModel()

                    fire.location = document.data["Location"].toString()
                    fire.date = document.data["Date"].toString()
                    fire.temperature = document.data["Temperature"].toString()
                    fire.user = document.data["User"].toString()
                    list.add(fire)
                    Log.d("datatata", document.id + " => " + document.data)
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
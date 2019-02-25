package com.example.freya

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.database.*


class LatestFireActivity : AppCompatActivity() {

    lateinit var database: FirebaseDatabase
    lateinit var myRef: DatabaseReference
    lateinit var list: MutableList<ReportModel>
    lateinit var recycle: RecyclerView
    lateinit var view: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_fire)
        view = findViewById<View>(R.id.view) as Button
        recycle = findViewById<View>(R.id.recycle) as RecyclerView
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference("reports/")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                list = ArrayList<ReportModel>()
                for (dataSnapshot1 in dataSnapshot.children) {

                    val value = dataSnapshot1.getValue(ReportModel::class.java)
                    val fire = ReportModel()
                    val location = value?.location
                    val date = value?.date
                    val temperature = value?.temperature
                    val user = value?.user
                    fire.location = location
                    fire.date = date
                    fire.temperature = temperature
                    fire.user = user
                    list.add(fire)

                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Hello", "Failed to read value.", error.toException())
            }
        })


        view.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                try {
                    val recyclerAdapter = RecyclerAdapter(list, this@LatestFireActivity)
                    val recyce = GridLayoutManager(this@LatestFireActivity, 2)
                    /// RecyclerView.LayoutManager recyce = new LinearLayoutManager(MainActivity.this);
                    // recycle.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
                    recycle.layoutManager = recyce
                    recycle.itemAnimator = DefaultItemAnimator()
                    recycle.adapter = recyclerAdapter
                } catch (e: Exception) {
                    Toast.makeText(this@LatestFireActivity, "No Data Received", Toast.LENGTH_SHORT).show()
                }


            }
        })


    }
}
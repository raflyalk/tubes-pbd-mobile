package com.example.freya

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class RecyclerAdapter(lst: List<ReportModel>, cntx: Context) : RecyclerView.Adapter<RecyclerAdapter.MyHolder>() {
    lateinit var reportList: List<ReportModel>
    lateinit var context: Context

    init {
        reportList = lst
        context = cntx
    }

    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var location: TextView
        var date: TextView
        var temperature: TextView
        var user: TextView

        init {
            location = itemView.findViewById(R.id.locationVal)
            date = itemView.findViewById(R.id.dateVal)
            temperature = itemView.findViewById(R.id.temperatureVal)
            user = itemView.findViewById(R.id.userVal)
        }
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerAdapter.MyHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.fire_card, p0, false)
        view.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
        val holder = MyHolder(view)
        return holder
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val myList = reportList.get(position)
        holder.location.setText(myList.location)
        holder.date.setText(myList.date)
        holder.temperature.setText(myList.temperature)
        holder.user.setText(myList.user)
    }

    override fun getItemCount(): Int {
        var arr = 0
        try {
            if (reportList.size == 0) {
                arr = 0
            } else {
                arr = reportList.size
            }
        } catch (e: Exception) {

        }
        return arr
    }
}
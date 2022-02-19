package com.example.manager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar
import kotlinx.android.synthetic.main.activity_choose_analytics.*

class ChooseAnalyticsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_analytics)
        val toolbar=findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle("Analytics")
       todaycardview.setOnClickListener {
           val intent= Intent(this,DailyAnalyticsActivity::class.java)
           startActivity(intent)
       }
        weekcardview.setOnClickListener {
            val intent=Intent(this,WeeklyAnalyticsActivity::class.java)
            startActivity(intent)
        }
        monthcardview.setOnClickListener {
            val intent=Intent(this,MonthlyAnalyticsActivity::class.java)
            startActivity(intent)
        }
    }
}
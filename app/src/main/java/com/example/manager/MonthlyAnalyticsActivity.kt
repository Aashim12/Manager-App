package com.example.manager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toolbar
import com.anychart.AnyChartView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MonthlyAnalyticsActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var expenseRef: FirebaseDatabase
    var onlineUserId:String=""
    private lateinit var personalRef: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_analytics)
        val toolbar=findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle("Monthly Analytics")
        val totalBudgetAmountTextView = findViewById<TextView>(R.id.totalBudgetAmountTextView)

        //general analytic
        val monthSpentAmount = findViewById<TextView>(R.id.monthSpentAmount)
        val linearLayoutAnalysis = findViewById<RelativeLayout>(R.id.linearLayoutAnalysis)
        val monthRatioSpending = findViewById<TextView>(R.id.monthRatioSpending)
        val monthRatioSpending_Image = findViewById<ImageView>(R.id.monthRatioSpending_Image)
        val analyticsTransportAmount = findViewById<TextView>(R.id.analyticsTransportAmount)
        val analyticsFoodAmount = findViewById<TextView>(R.id.analyticsFoodAmount)
        val analyticsHouseExpensesAmount = findViewById<TextView>(R.id.analyticsHouseExpensesAmount)
        val analyticsEntertainmentAmount = findViewById<TextView>(R.id.analyticsEntertainmentAmount)
        val analyticsEducationAmount = findViewById<TextView>(R.id.analyticsEducationAmount)
        val analyticsCharityAmount = findViewById<TextView>(R.id.analyticsCharityAmount)
        val analyticsApparelAmount = findViewById<TextView>(R.id.analyticsApparelAmount)
        val analyticsHealthAmount = findViewById<TextView>(R.id.analyticsHealthAmount)
        val analyticsPersonalExpensesAmount = findViewById<TextView>(R.id.analyticsPersonalExpensesAmount)
        val analyticsOtherAmount = findViewById<TextView>(R.id.analyticsOtherAmount)

        //Relative layouts views
        val  linearLayoutTransport = findViewById<RelativeLayout>(R.id.linearLayoutTransport)
        val  linearLayoutFood = findViewById<RelativeLayout>(R.id.linearLayoutFood)
        val linearLayoutFoodHouse = findViewById<RelativeLayout>(R.id.linearLayoutFoodHouse)
        val linearLayoutEntertainment = findViewById<RelativeLayout>(R.id.linearLayoutEntertainment)
        val linearLayoutEducation = findViewById<RelativeLayout>(R.id.linearLayoutEducation)
        val     linearLayoutCharity = findViewById<RelativeLayout>(R.id.linearLayoutCharity)
        val linearLayoutApparel = findViewById<RelativeLayout>(R.id.linearLayoutApparel)
        val linearLayoutHealth = findViewById<RelativeLayout>(R.id.linearLayoutHealth)
        val linearLayoutPersonalExp = findViewById<RelativeLayout>(R.id.linearLayoutPersonalExp)
        val linearLayoutOther = findViewById<RelativeLayout>(R.id.linearLayoutOther)

        //textviews
        val   progress_ratio_transport = findViewById<TextView>(R.id.progress_ratio_transport)
        val progress_ratio_food = findViewById<TextView>(R.id.progress_ratio_food)
        val progress_ratio_house = findViewById<TextView>(R.id.progress_ratio_house)
        val progress_ratio_ent = findViewById<TextView>(R.id.progress_ratio_ent)
        val progress_ratio_edu = findViewById<TextView>(R.id.progress_ratio_edu)
        val progress_ratio_cha = findViewById<TextView>(R.id.progress_ratio_cha)
        val progress_ratio_app = findViewById<TextView>(R.id.progress_ratio_app)
        val progress_ratio_hea = findViewById<TextView>(R.id.progress_ratio_hea)
        val progress_ratio_per = findViewById<TextView>(R.id.progress_ratio_per)
        val progress_ratio_oth = findViewById<TextView>(R.id.progress_ratio_oth)
        //imageviews
        val  status_Image_transport = findViewById<ImageView>(R.id.status_Image_transport)
        val status_Image_food = findViewById<ImageView>(R.id.status_Image_food)
        val status_Image_house = findViewById<ImageView>(R.id.status_Image_house)
        val status_Image_ent = findViewById<ImageView>(R.id.status_Image_ent)
        val status_Image_edu = findViewById<ImageView>(R.id.status_Image_edu)
        val status_Image_cha = findViewById<ImageView>(R.id.status_Image_cha)
        val status_Image_app = findViewById<ImageView>(R.id.status_Image_app)
        val status_Image_hea = findViewById<ImageView>(R.id.status_Image_hea)
        val status_Image_per = findViewById<ImageView>(R.id.status_Image_per)
        val status_Image_oth = findViewById<ImageView>(R.id.status_Image_oth)

        //anyChartView
        val   anyChartView = findViewById<AnyChartView>(R.id.anyChartView);


    }
}
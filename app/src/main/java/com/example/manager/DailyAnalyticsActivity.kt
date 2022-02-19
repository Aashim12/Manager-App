package com.example.manager

import android.R.attr
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_daily_analytics.*
import org.joda.time.MutableDateTime
import java.text.SimpleDateFormat
import java.util.*
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry

import android.R.attr.data
import android.R.attr.data
import android.icu.number.IntegerWidth
import android.util.Log
import com.anychart.AnyChart.pie
import com.anychart.enums.Align
import com.anychart.enums.LegendLayout
import java.lang.Float.parseFloat


class DailyAnalyticsActivity : AppCompatActivity() {
    private lateinit var mAuth:FirebaseAuth
    private lateinit var expenseRef:DatabaseReference
    var onlineUserId:String=""
    private lateinit var personalRef:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_analytics)
        val toolbar=findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle("Today Analytics")
        mAuth= FirebaseAuth.getInstance()
        onlineUserId=mAuth.currentUser!!.uid
          personalRef=FirebaseDatabase.getInstance().getReference("Personal").child(onlineUserId)
        expenseRef=FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        //general analytic
//       val monthSpentAmount = findViewById<TextView>(R.id.monthSpentAmount)
//        val linearLayoutAnalysis = findViewById<RelativeLayout>(R.id.linearLayoutAnalysis)
//        val monthRatioSpending = findViewById<TextView>(R.id.monthRatioSpending)
//        val monthRatioSpending_Image = findViewById<ImageView>(R.id.monthRatioSpending_Image)
//       val analyticsTransportAmount = findViewById<TextView>(R.id.analyticsTransportAmount)
//        val analyticsFoodAmount = findViewById<TextView>(R.id.analyticsFoodAmount)
//        val analyticsHouseExpensesAmount = findViewById<TextView>(R.id.analyticsHouseExpensesAmount)
//        val analyticsEntertainmentAmount = findViewById<TextView>(R.id.analyticsEntertainmentAmount)
//        val analyticsEducationAmount = findViewById<TextView>(R.id.analyticsEducationAmount)
//         val analyticsCharityAmount = findViewById<TextView>(R.id.analyticsCharityAmount)
//        val analyticsApparelAmount = findViewById<TextView>(R.id.analyticsApparelAmount)
//        val analyticsHealthAmount = findViewById<TextView>(R.id.analyticsHealthAmount)
//        val analyticsPersonalExpensesAmount = findViewById<TextView>(R.id.analyticsPersonalExpensesAmount)
//        val analyticsOtherAmount = findViewById<TextView>(R.id.analyticsOtherAmount)
//
//        //Relative layouts views
//      val  linearLayoutTransport = findViewById<RelativeLayout>(R.id.linearLayoutTransport)
//        val  linearLayoutFood = findViewById<RelativeLayout>(R.id.linearLayoutFood)
//        val linearLayoutFoodHouse = findViewById<RelativeLayout>(R.id.linearLayoutFoodHouse)
//        val linearLayoutEntertainment = findViewById<RelativeLayout>(R.id.linearLayoutEntertainment)
//        val linearLayoutEducation = findViewById<RelativeLayout>(R.id.linearLayoutEducation)
//        val     linearLayoutCharity = findViewById<RelativeLayout>(R.id.linearLayoutCharity)
//        val linearLayoutApparel = findViewById<RelativeLayout>(R.id.linearLayoutApparel)
//        val linearLayoutHealth = findViewById<RelativeLayout>(R.id.linearLayoutHealth)
//        val linearLayoutPersonalExp = findViewById<RelativeLayout>(R.id.linearLayoutPersonalExp)
//        val linearLayoutOther = findViewById<RelativeLayout>(R.id.linearLayoutOther)
//
//        //textviews
//     val   progress_ratio_transport = findViewById<TextView>(R.id.progress_ratio_transport)
//       val progress_ratio_food = findViewById<TextView>(R.id.progress_ratio_food)
//       val progress_ratio_house = findViewById<TextView>(R.id.progress_ratio_house)
//       val progress_ratio_ent = findViewById<TextView>(R.id.progress_ratio_ent)
//        val progress_ratio_edu = findViewById<TextView>(R.id.progress_ratio_edu)
//       val progress_ratio_cha = findViewById<TextView>(R.id.progress_ratio_cha)
//       val progress_ratio_app = findViewById<TextView>(R.id.progress_ratio_app)
//       val progress_ratio_hea = findViewById<TextView>(R.id.progress_ratio_hea)
//        val progress_ratio_per = findViewById<TextView>(R.id.progress_ratio_per)
//       val progress_ratio_oth = findViewById<TextView>(R.id.progress_ratio_oth)
//        //imageviews
//      val  status_Image_transport = findViewById<ImageView>(R.id.status_Image_transport)
//       val status_Image_food = findViewById<ImageView>(R.id.status_Image_food)
//       val status_Image_house = findViewById<ImageView>(R.id.status_Image_house)
//       val status_Image_ent = findViewById<ImageView>(R.id.status_Image_ent)
//       val status_Image_edu = findViewById<ImageView>(R.id.status_Image_edu)
//       val status_Image_cha = findViewById<ImageView>(R.id.status_Image_cha)
//       val status_Image_app = findViewById<ImageView>(R.id.status_Image_app)
//       val status_Image_hea = findViewById<ImageView>(R.id.status_Image_hea)
//       val status_Image_per = findViewById<ImageView>(R.id.status_Image_per)
//       val status_Image_oth = findViewById<ImageView>(R.id.status_Image_oth)
//
//        //anyChartView
//     val   anyChartView = findViewById<AnyChartView>(R.id.anyChartView);

      getTodayTransExpenses()
        getTodayFoodExpenses()
        getTodayHousExpenses()
        getTodayEntExpenses()
        getTodayEduExpenses()
        getTodayCharExpenses()
        getTodayAppExpenses()
        getTodayHealExpenses()
        getTodayAppExpenses()
        getTodayPerExpenses()
        getTodayOthExpenses()
        getTodayExpenses()
        Timer().schedule(
            object : TimerTask() {
                override fun run() {
                    loadGraph()
                    //setStatusImageResource()
                }
            },
            2000
        )

    }
    private fun getTodayExpenses(){
        val dateFormat=SimpleDateFormat("dd-MM-yyyy")
        val cal = Calendar.getInstance()
        val date= dateFormat.format(cal.time)
        val reference=FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query=reference.orderByChild("date").equalTo(date)
        query.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists() && snapshot.childrenCount>0){
                    var totalamount = 0
                    for(snap in snapshot.children){
                        val map = snap.value as Map<String, Any>?
                        val total = map!!["amount"]
                        val ptotal = total.toString().toInt()
                        totalamount+=ptotal
                    }
                    Log.d("tag","Today test",null)
                    totalbudgetamount.setText("Total today's spending \u20B9 $totalamount")
                    monthSpentAmount.setText("Total spent \u20B9 $totalamount")

                }
                else{
                    Log.d("tag","Failed test",null)
                    totalbudgetamount.setText("You've not spent anything today")
                    anyChartView.visibility=View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DailyAnalyticsActivity,"Failed",Toast.LENGTH_LONG).show()
            }

        })
    }
    private fun getTodayTransExpenses(){
        val epoch=MutableDateTime()
        epoch.setDate(0)
        val cal = Calendar.getInstance()
        val dateFormat=SimpleDateFormat("dd-MM-yyyy")
      val date= dateFormat.format(cal.time)
        val itemNday="Transport$date"
        val reference=FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query = reference.orderByChild("itemday").equalTo(itemNday)
         query.addValueEventListener(object :ValueEventListener{
             override fun onDataChange(snapshot: DataSnapshot) {
                 if(snapshot.exists()){
                     var totalamount=0
                     for(snap in snapshot.children){
                         val map = snap.value as Map<String, Any>?
                         val total = map!!["amount"]
                         val ptotal = total.toString().toInt()
                         totalamount+=ptotal
                     analyticsTransportAmount.setText("Spent: $totalamount")
                     }
                     personalRef.child("dayTrans").setValue(totalamount)
                 }
                 else{
                     linearLayoutTransport.visibility= View.GONE
                 }
             }

             override fun onCancelled(error: DatabaseError) {
                 Toast.makeText(this@DailyAnalyticsActivity,"Failed",Toast.LENGTH_LONG).show()
             }

         })

    }
    private fun getTodayFoodExpenses(){
        val epoch=MutableDateTime()
        epoch.setDate(0)
        val cal = Calendar.getInstance()
        val dateFormat=SimpleDateFormat("dd-MM-yyyy")
        val date= dateFormat.format(cal.time)
        val itemNday="Food$date"
        val reference=FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query = reference.orderByChild("itemday").equalTo(itemNday)
        query.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalamount=0
                    for(snap in snapshot.children){
                        val map = snap.value as Map<String, Any>?
                        val total = map!!["amount"]
                        val ptotal = total.toString().toInt()
                        totalamount+=ptotal
                        analyticsFoodAmount.setText("Spent: $totalamount")
                    }
                    personalRef.child("dayFood").setValue(totalamount)
                }
                else{
                    linearLayoutFood.visibility= View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DailyAnalyticsActivity,"Failed",Toast.LENGTH_LONG).show()
            }

        })

    }
    private fun getTodayEntExpenses(){
        val epoch=MutableDateTime()
        epoch.setDate(0)
        val cal = Calendar.getInstance()
        val dateFormat=SimpleDateFormat("dd-MM-yyyy")
        val date= dateFormat.format(cal.time)
        val itemNday="Entertainment$date"
        val reference=FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query = reference.orderByChild("itemday").equalTo(itemNday)
        query.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalamount=0
                    for(snap in snapshot.children){
                        val map = snap.value as Map<String, Any>?
                        val total = map!!["amount"]
                        val ptotal = total.toString().toInt()
                        totalamount+=ptotal
                        analyticsEntertainmentAmount.setText("Spent: $totalamount")
                    }
                    personalRef.child("dayEnt").setValue(totalamount)
                }
                else{
                    linearLayoutEntertainment.visibility= View.GONE
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DailyAnalyticsActivity,"Failed",Toast.LENGTH_LONG).show()
            }

        })
    }
    private fun getTodayHousExpenses(){
        val epoch=MutableDateTime()
        epoch.setDate(0)
        val cal = Calendar.getInstance()
        val dateFormat=SimpleDateFormat("dd-MM-yyyy")
        val date= dateFormat.format(cal.time)
        val itemNday="House$date"
        val reference=FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query = reference.orderByChild("itemday").equalTo(itemNday)
        query.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalamount=0
                    for(snap in snapshot.children){
                        val map = snap.value as Map<String, Any>?
                        val total = map!!["amount"]
                        val ptotal = total.toString().toInt()
                        totalamount+=ptotal
                        analyticsHouseExpensesAmount.setText("Spent: $totalamount")
                    }
                    personalRef.child("dayHous").setValue(totalamount)
                }
                else{
                    linearLayoutHouse.visibility= View.GONE
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DailyAnalyticsActivity,"Failed",Toast.LENGTH_LONG).show()
            }

        })
    }
    private fun getTodayEduExpenses(){
        val epoch=MutableDateTime()
        epoch.setDate(0)
        val cal = Calendar.getInstance()
        val dateFormat=SimpleDateFormat("dd-MM-yyyy")
        val date= dateFormat.format(cal.time)
        val itemNday="Education$date"
        val reference=FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query = reference.orderByChild("itemday").equalTo(itemNday)
        query.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalamount=0
                    for(snap in snapshot.children){
                        val map = snap.value as Map<String, Any>?
                        val total = map!!["amount"]
                        val ptotal = total.toString().toInt()
                        totalamount+=ptotal
                        analyticsEducationAmount.setText("Spent: $totalamount")
                    }
                    personalRef.child("dayEdu").setValue(totalamount)
                }
                else{
                    linearLayoutEducation.visibility= View.GONE
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DailyAnalyticsActivity,"Failed",Toast.LENGTH_LONG).show()
            }

        })
    }
    private fun getTodayCharExpenses(){
        val epoch=MutableDateTime()
        epoch.setDate(0)
        val cal = Calendar.getInstance()
        val dateFormat=SimpleDateFormat("dd-MM-yyyy")
        val date= dateFormat.format(cal.time)
        val itemNday="Charity$date"
        val reference=FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query = reference.orderByChild("itemday").equalTo(itemNday)
        query.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalamount=0
                    for(snap in snapshot.children){
                        val map = snap.value as Map<String, Any>?
                        val total = map!!["amount"]
                        val ptotal = total.toString().toInt()
                        totalamount+=ptotal
                        analyticsCharityAmount.setText("Spent: $totalamount")
                    }
                    personalRef.child("dayChar").setValue(totalamount)
                }
                else{
                    linearLayoutCharity.visibility= View.GONE
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DailyAnalyticsActivity,"Failed",Toast.LENGTH_LONG).show()
            }

        })
    }
    private fun getTodayAppExpenses(){
        val epoch=MutableDateTime()
        epoch.setDate(0)
        val cal = Calendar.getInstance()
        val dateFormat=SimpleDateFormat("dd-MM-yyyy")
        val date= dateFormat.format(cal.time)
        val itemNday="Apparel$date"
        val reference=FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query = reference.orderByChild("itemday").equalTo(itemNday)
        query.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalamount=0
                    for(snap in snapshot.children){
                        val map = snap.value as Map<String, Any>?
                        val total = map!!["amount"]
                        val ptotal = total.toString().toInt()
                        totalamount+=ptotal
                        analyticsApparelAmount.setText("Spent: $totalamount")
                    }
                    personalRef.child("dayApp").setValue(totalamount)
                }
                else{
                    linearLayoutApparel.visibility= View.GONE
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DailyAnalyticsActivity,"Failed",Toast.LENGTH_LONG).show()
            }

        })
    }
    private fun getTodayHealExpenses(){
        val epoch=MutableDateTime()
        epoch.setDate(0)
        val cal = Calendar.getInstance()
        val dateFormat=SimpleDateFormat("dd-MM-yyyy")
        val date= dateFormat.format(cal.time)
        val itemNday="Health$date"
        val reference=FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query = reference.orderByChild("itemday").equalTo(itemNday)
        query.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalamount=0
                    for(snap in snapshot.children){
                        val map = snap.value as Map<String, Any>?
                        val total = map!!["amount"]
                        val ptotal = total.toString().toInt()
                        totalamount+=ptotal
                       analyticsHealthAmount.setText("Spent: $totalamount")
                    }
                    personalRef.child("dayHeal").setValue(totalamount)
                }
                else{
                    linearLayoutHealth.visibility= View.GONE
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DailyAnalyticsActivity,"Failed",Toast.LENGTH_LONG).show()
            }

        })
    }
    private fun getTodayPerExpenses(){
        val epoch=MutableDateTime()
        epoch.setDate(0)
        val cal = Calendar.getInstance()
        val dateFormat=SimpleDateFormat("dd-MM-yyyy")
        val date= dateFormat.format(cal.time)
        val itemNday="Personal$date"
        val reference=FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query = reference.orderByChild("itemday").equalTo(itemNday)
        query.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalamount=0
                    for(snap in snapshot.children){
                        val map = snap.value as Map<String, Any>?
                        val total = map!!["amount"]
                        val ptotal = total.toString().toInt()
                        totalamount+=ptotal
                        analyticsPersonalExpensesAmount.setText("Spent: $totalamount")
                    }
                    personalRef.child("dayPer").setValue(totalamount)
                }
                else{
                    linearLayoutPersonalExp.visibility= View.GONE
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DailyAnalyticsActivity,"Failed",Toast.LENGTH_LONG).show()
            }

        })
    }
    private fun getTodayOthExpenses(){
        val epoch=MutableDateTime()
        epoch.setDate(0)
        val cal = Calendar.getInstance()
        val dateFormat=SimpleDateFormat("dd-MM-yyyy")
        val date= dateFormat.format(cal.time)
        val itemNday="Other$date"
        val reference=FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query = reference.orderByChild("itemday").equalTo(itemNday)
        query.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var totalamount=0
                    for(snap in snapshot.children){
                        val map = snap.value as Map<String, Any>?
                        val total = map!!["amount"]
                        val ptotal = total.toString().toInt()
                        totalamount+=ptotal
                        analyticsOtherAmount.setText("Spent: $totalamount")
                    }
                    personalRef.child("dayOth").setValue(totalamount)
                }
                else{
                    linearLayoutOther.visibility= View.GONE
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DailyAnalyticsActivity,"Failed",Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun loadGraph(){
        personalRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    Log.d("Tag","Function test",null)
                    var traTotal:Int
                    if(snapshot.hasChild("dayTrans")){
                        traTotal= Integer.parseInt(snapshot.child("dayTrans").getValue().toString())
                    }
                    else{
                        traTotal=0
                    }
                    var foodTotal:Int
                    if(snapshot.hasChild("dayFood")){
                        foodTotal= Integer.parseInt(snapshot.child("dayFood").getValue().toString())
                    }
                    else{
                        foodTotal=0
                    }
                  var entTotal:Int
                  if(snapshot.hasChild("dayEnt")){
                      entTotal=Integer.parseInt(snapshot.child("dayEnt").getValue().toString())
                  }
                    else{
                        entTotal=0
                  }
                    var housTotal:Int
                    if(snapshot.hasChild("dayHous")){
                        housTotal=Integer.parseInt(snapshot.child("dayHous").getValue().toString())
                    }
                    else{
                        housTotal=0
                    }
                    var eduTotal:Int
                    if(snapshot.hasChild("dayEdu")){
                        eduTotal=Integer.parseInt(snapshot.child("dayEdu").getValue().toString())
                    }
                    else{
                        eduTotal=0
                    }
                    var charTotal:Int
                    if(snapshot.hasChild("dayChar")){
                        charTotal= Integer.parseInt(snapshot.child("dayChar").getValue().toString())
                    }
                    else{
                        charTotal=0
                    }
                    var appTotal:Int
                    if(snapshot.hasChild("dayApp")){
                        appTotal=Integer.parseInt(snapshot.child("dayApp").getValue().toString())
                    }
                    else{
                    appTotal=0
                    }
                    var healTotal:Int
                    if(snapshot.hasChild("dayHeal")){
                        healTotal= Integer.parseInt(snapshot.child("dayHeal").getValue().toString())
                    }
                    else{
                        healTotal=0
                    }
                    var perTotal:Int
                    if(snapshot.hasChild("dayPer")){
                        perTotal=Integer.parseInt(snapshot.child("dayPer").getValue().toString())
                    }
                    else{
                        perTotal=0
                    }
                    var othTotal:Int
                    if(snapshot.hasChild("dayOth")){
                        othTotal=Integer.parseInt(snapshot.child("dayOth").getValue().toString())
                    }
                    else{
                        othTotal=0
                    }
                    val pie = AnyChart.pie()
                    val data: MutableList<DataEntry> = ArrayList()
                    data.add(ValueDataEntry("Transport", traTotal))
                    data.add(ValueDataEntry("House exp", housTotal))
                    data.add(ValueDataEntry("Food", foodTotal))
                    data.add(ValueDataEntry("Entertainment", entTotal))
                    data.add(ValueDataEntry("Education", eduTotal))
                    data.add(ValueDataEntry("Charity", charTotal))
                    data.add(ValueDataEntry("Apparel", appTotal))
                    data.add(ValueDataEntry("Health", healTotal))
                    data.add(ValueDataEntry("Personal", perTotal))
                    data.add(ValueDataEntry("Other", othTotal))
                    Log.d("Tag","Pie chart test",null)

                //Setting pie chart according to the data
                    pie.data(data)
                    pie.title("Daily Analytics")
                    pie.labels().position("outside")
                 pie.legend().title().enabled(true)
                    pie.legend().title()
                        .text("Items Spent On")
                        .padding(0.0, 0.0, 10.0, 0.0)
                    pie.legend()
                        .position("center-bottom")
                        .itemsLayout(LegendLayout.HORIZONTAL)
                        .align(Align.CENTER);

                    anyChartView.setChart(pie);

                }
                else{
                    Toast.makeText(this@DailyAnalyticsActivity,"Child Does not exist",Toast.LENGTH_LONG).show()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setStatusImageResource(){
        personalRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    var traTotal : Int
                    if (snapshot.hasChild("dayTrans")) {
                        traTotal =
                            Integer.parseInt(snapshot.child("dayTrans").getValue().toString())
                    } else {
                        traTotal = 0
                    }
                    var foodTotal : Int
                    if (snapshot.hasChild("dayFood")) {
                        foodTotal =
                            Integer.parseInt(snapshot.child("dayFood").getValue().toString())
                    } else {
                        foodTotal = 0
                    }
                    var entTotal : Int
                    if (snapshot.hasChild("dayEnt")) {
                        entTotal = Integer.parseInt(snapshot.child("dayEnt").getValue().toString())
                    } else {
                        entTotal = 0
                    }
                    var housTotal : Int
                    if (snapshot.hasChild("dayHous")) {
                        housTotal =
                            Integer.parseInt(snapshot.child("dayHous").getValue().toString())
                    } else {
                        housTotal = 0
                    }
                    var eduTotal : Int
                    if (snapshot.hasChild("dayEdu")) {
                        eduTotal = Integer.parseInt(snapshot.child("dayEdu").getValue().toString())
                    } else {
                        eduTotal = 0
                    }
                    var charTotal : Int
                    if (snapshot.hasChild("dayChar")) {
                        charTotal =
                            Integer.parseInt(snapshot.child("dayChar").getValue().toString())
                    } else {
                        charTotal = 0
                    }
                    var appTotal : Int
                    if (snapshot.hasChild("dayApp")) {
                        appTotal = Integer.parseInt(snapshot.child("dayApp").getValue().toString())
                    } else {
                        appTotal = 0
                    }
                    var healTotal : Int
                    if (snapshot.hasChild("dayHeal")) {
                        healTotal =
                            Integer.parseInt(snapshot.child("dayHeal").getValue().toString())
                    } else {
                        healTotal = 0
                    }
                    var perTotal : Int
                    if (snapshot.hasChild("dayPer")) {
                        perTotal = Integer.parseInt(snapshot.child("dayPer").getValue().toString())
                    } else {
                        perTotal = 0
                    }
                    var othTotal : Int
                    if (snapshot.hasChild("dayOth")) {
                        othTotal = Integer.parseInt(snapshot.child("dayOth").getValue().toString())
                    } else {
                        othTotal = 0
                    }
                    var monthTotalspentamount : Int
                    if (snapshot.hasChild("today")) {
                        monthTotalspentamount =
                            Integer.parseInt(snapshot.child("today").getValue().toString())
                    } else {
                        monthTotalspentamount = 0
                    }

                    //Getting ratios

                    var traRatio : Int
                    if (snapshot.hasChild("dayTransratio")) {
                        traRatio =
                            Integer.parseInt(snapshot.child("dayTransratio").getValue().toString())
                    } else {
                        traRatio = 0
                    }
                    var foodRatio : Int
                    if (snapshot.hasChild("dayFoodratio")) {
                        foodRatio =
                            Integer.parseInt(snapshot.child("dayFoodratio").getValue().toString())
                    }
                    else {
                        foodRatio = 0
                    }
                    var houseRatio : Int
                    if (snapshot.hasChild("dayhousratio")) {
                        houseRatio =
                            Integer.parseInt(snapshot.child("dayHousratio").getValue().toString())
                    }
                    else {
                        houseRatio =0
                    }
                    var entRatio : Int
                    if (snapshot.hasChild("dayEntratio")) {
                        entRatio = snapshot.child("dayEntratio").toString().toInt()
                    } else {
                        entRatio = 0
                    }
                    var eduRatio : Int
                    if (snapshot.hasChild("dayEduratio")) {
                        eduRatio =
                            Integer.parseInt(snapshot.child("dayEduratio").getValue().toString())
                    } else {
                        eduRatio = 0
                    }
                    var charRatio : Int
                    if (snapshot.hasChild("dayCharatio")) {
                        charRatio =
                            Integer.parseInt(snapshot.child("dayCharatio").getValue().toString())
                    } else {
                        charRatio = 0
                    }
                    var appRatio : Int
                    if (snapshot.hasChild("dayAppratio")) {
                        appRatio =
                            Integer.parseInt(snapshot.child("dayAppratio").getValue().toString())
                    } else {
                        appRatio = 0
                    }
                    var healRatio : Int
                    if (snapshot.hasChild("dayHealratio")) {
                        healRatio =
                            Integer.parseInt(snapshot.child("dayHealratio").getValue().toString())
                    } else {
                        healRatio = 0
                    }
                    var perRatio : Int
                    if (snapshot.hasChild("dayPerratio")) {
                        perRatio =
                            Integer.parseInt(snapshot.child("dayPerratio").getValue().toString())
                    } else {
                        perRatio = 0
                    }
                    var othRatio : Int
                    if (snapshot.hasChild("dayOthratio")) {
                        othRatio =
                            Integer.parseInt(snapshot.child("dayOthratio").getValue().toString())
                    } else {
                        othRatio = 0
                    }
                    var monthTotalspentratio :Int
                    if (snapshot.hasChild("dailyBudget")) {
                        monthTotalspentratio =
                            Integer.parseInt(snapshot.child("dailyBudget").getValue().toString())
                    } else {
                        monthTotalspentratio = 0
                    }
                    var monthPercent = (monthTotalspentamount/monthTotalspentratio)*100
                      if(monthPercent<50){
                          monthSpentAmount.setText("$monthPercent% used of $monthTotalspentratio Status ")
                          monthRatioSpending_Image.setImageResource(R.drawable.green)
                      }
                        else if(monthPercent>=50 && monthPercent<100){
                          monthSpentAmount.setText("$monthPercent% used of $monthTotalspentratio Status ")
                        monthRatioSpending_Image.setImageResource(R.drawable.brown)
                      }
                    else{
                          monthSpentAmount.setText("$monthPercent% used of $monthTotalspentratio Status ")
                          monthRatioSpending_Image.setImageResource(R.drawable.red)
                      }
                    var transportPercent = (traTotal/traRatio)*100
                      if(transportPercent<50){
                          progress_ratio_transport.setText("$transportPercent % used of $traTotal Status")
                          status_Image_transport.setImageResource(R.drawable.green)
                      }
                    else if(transportPercent>=50 && transportPercent<100){
                          progress_ratio_transport.setText("$transportPercent % used of $traTotal Status")
                          status_Image_transport.setImageResource(R.drawable.brown)
                      }
                    else{
                          progress_ratio_transport.setText("$transportPercent % used of $traTotal Status")
                          status_Image_transport.setImageResource(R.drawable.red)
                      }
                    var foodPercent=(foodTotal/foodRatio)*100
                    if(foodPercent<50){
                        progress_ratio_food.setText("$foodPercent % used of $foodTotal Status")
                        status_Image_food.setImageResource(R.drawable.green)
                    }
                       else if(foodPercent>=50 && foodPercent<100){
                        progress_ratio_food.setText("$foodPercent % used of $foodTotal Status")
                        status_Image_food.setImageResource(R.drawable.brown)
                       }
                    else{
                        progress_ratio_food.setText("$foodPercent % used of $foodTotal Status")
                        status_Image_food.setImageResource(R.drawable.red)
                    }
                    var housePercent=(housTotal/houseRatio)*100
                    if(housePercent<50){
                        progress_ratio_house.setText("$housePercent % used of $housTotal Status")
                        status_Image_house.setImageResource(R.drawable.green)
                    }
                    else if (housePercent>=50 && housePercent<100){
                        progress_ratio_house.setText("$housePercent % used of $housTotal Status")
                        status_Image_house.setImageResource(R.drawable.brown)
                    }
                    else{
                        progress_ratio_house.setText("$housePercent % used of $housTotal Status")
                        status_Image_house.setImageResource(R.drawable.red)
                    }
                    var entPercent = (entTotal/entRatio)*100
                    if(entPercent<50){
                        progress_ratio_ent.setText("$entPercent % used of $entTotal Status")
                        status_Image_ent.setImageResource(R.drawable.green)
                    }
                    else if(entPercent>=50 && entPercent<100){
                        progress_ratio_ent.setText("$entPercent % used of $entTotal Status")
                        status_Image_ent.setImageResource(R.drawable.brown)
                    }
                    else{
                        progress_ratio_ent.setText("$entPercent % used of $entTotal Status")
                        status_Image_ent.setImageResource(R.drawable.red)
                    }
                    var eduPercent = (eduTotal/eduRatio)*100
                    if(eduPercent<50){
                        progress_ratio_edu.setText("$eduPercent % used of $eduTotal Status")
                        status_Image_edu.setImageResource(R.drawable.green)
                    }
                   else if(eduPercent>=50 && eduPercent<100){
                        progress_ratio_edu.setText("$eduPercent % used of $eduTotal Status")
                        status_Image_edu.setImageResource(R.drawable.brown)
                    }
                    else{
                        progress_ratio_edu.setText("$eduPercent % used of $eduTotal Status")
                        status_Image_edu.setImageResource(R.drawable.red)
                    }
                    var charPercent = (charTotal/charRatio)*100
                    if(charPercent<50){
                        progress_ratio_cha.setText("$charPercent % used of $charTotal Status")
                        status_Image_cha.setImageResource(R.drawable.green)
                    }
                    else if (charPercent>=50 && charPercent<100){
                        progress_ratio_cha.setText("$charPercent % used of $charTotal Status")
                        status_Image_cha.setImageResource(R.drawable.brown)
                    }
                    else{
                        progress_ratio_cha.setText("$charPercent % used of $charTotal Status")
                        status_Image_cha.setImageResource(R.drawable.green)
                    }
                    var appPercent = (appTotal/appRatio)*100
                    if(appPercent<50){
                        progress_ratio_app.setText("$appPercent % used of $appTotal")
                        status_Image_app.setImageResource(R.drawable.green)
                    }
                    else if(appPercent>=50 && appPercent<100){
                        progress_ratio_app.setText("$appPercent % used of $appTotal")
                        status_Image_app.setImageResource(R.drawable.brown)
                    }
                      else{
                        progress_ratio_app.setText("$appPercent % used of $appTotal")
                        status_Image_app.setImageResource(R.drawable.red)
                    }
                    var perPercent = (perTotal/perRatio)*100
                    if(perPercent<50){
                        progress_ratio_per.setText("$perPercent % used of $perTotal")
                        status_Image_per.setImageResource(R.drawable.green)
                    }
                    else if(perPercent>=50 && perPercent<100){
                        progress_ratio_per.setText("$perPercent % used of $perTotal")
                        status_Image_per.setImageResource(R.drawable.brown)
                    }
                    else{
                        progress_ratio_per.setText("$perPercent % used of $perTotal")
                        status_Image_per.setImageResource(R.drawable.red)
                    }
                    var othPercent = (othTotal/othRatio)*100
                    if(othPercent<50){
                        progress_ratio_oth.setText("$othPercent % used of $othTotal")
                        status_Image_oth.setImageResource(R.drawable.green)
                    }
                    else if (othPercent>=50 && othPercent<100){
                        progress_ratio_oth.setText("$othPercent % used of $othTotal")
                        status_Image_oth.setImageResource(R.drawable.brown)
                    }
                    else{
                        progress_ratio_oth.setText("$othPercent % used of $othTotal")
                        status_Image_oth.setImageResource(R.drawable.red)
                    }
                    var healPercent = (healTotal/healRatio)*100
                    if(healPercent<50){
                        progress_ratio_hea.setText("$healPercent % used of $healTotal")
                        status_Image_hea.setImageResource(R.drawable.green)
                    }
                    else if(healPercent>=50 && healPercent<100){
                        progress_ratio_hea.setText("$healPercent % used of $healTotal")
                        status_Image_hea.setImageResource(R.drawable.brown)
                    }
                    else{
                        progress_ratio_hea.setText("$healPercent % used of $healTotal")
                        status_Image_hea.setImageResource(R.drawable.red)
                    }
                 }
             else{
                 Toast.makeText(this@DailyAnalyticsActivity,"Setimageandresource errro ",Toast.LENGTH_LONG).show()

             }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}
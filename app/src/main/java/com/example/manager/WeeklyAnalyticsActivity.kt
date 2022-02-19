package com.example.manager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.enums.Align
import com.anychart.enums.LegendLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_daily_analytics.*
import kotlinx.android.synthetic.main.activity_daily_analytics.analyticsTransportAmount
import kotlinx.android.synthetic.main.activity_daily_analytics.anyChartView
import kotlinx.android.synthetic.main.activity_daily_analytics.linearLayoutFood
import kotlinx.android.synthetic.main.activity_daily_analytics.linearLayoutTransport
import kotlinx.android.synthetic.main.activity_daily_analytics.monthSpentAmount
import kotlinx.android.synthetic.main.activity_weekly_analytics.*
import org.joda.time.DateTime
import org.joda.time.MutableDateTime
import org.joda.time.Weeks
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class WeeklyAnalyticsActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var expenseRef: DatabaseReference
    var onlineUserId:String=""
    private lateinit var personalRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekly_analytics)
        val toolbar=findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle("Weekly Analytics")
        mAuth= FirebaseAuth.getInstance()
        onlineUserId=mAuth.currentUser!!.uid
        personalRef=FirebaseDatabase.getInstance().getReference("Personal").child(onlineUserId)
        expenseRef=FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
//        val totalBudgetAmountTextView = findViewById<TextView>(R.id.totalBudgetAmountTextView)
//
//        //general analytic
//        val monthSpentAmount = findViewById<TextView>(R.id.monthSpentAmount)
//        val linearLayoutAnalysis = findViewById<RelativeLayout>(R.id.linearLayoutAnalysis)
//        val monthRatioSpending = findViewById<TextView>(R.id.monthRatioSpending)
//        val monthRatioSpending_Image = findViewById<ImageView>(R.id.monthRatioSpending_Image)
//        val analyticsTransportAmount = findViewById<TextView>(R.id.analyticsTransportAmount)
//        val analyticsFoodAmount = findViewById<TextView>(R.id.analyticsFoodAmount)
//        val analyticsHouseExpensesAmount = findViewById<TextView>(R.id.analyticsHouseExpensesAmount)
//        val analyticsEntertainmentAmount = findViewById<TextView>(R.id.analyticsEntertainmentAmount)
//        val analyticsEducationAmount = findViewById<TextView>(R.id.analyticsEducationAmount)
//        val analyticsCharityAmount = findViewById<TextView>(R.id.analyticsCharityAmount)
//        val analyticsApparelAmount = findViewById<TextView>(R.id.analyticsApparelAmount)
//        val analyticsHealthAmount = findViewById<TextView>(R.id.analyticsHealthAmount)
//        val analyticsPersonalExpensesAmount = findViewById<TextView>(R.id.analyticsPersonalExpensesAmount)
//        val analyticsOtherAmount = findViewById<TextView>(R.id.analyticsOtherAmount)
//
//        //Relative layouts views
//        val  linearLayoutTransport = findViewById<RelativeLayout>(R.id.linearLayoutTransport)
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
//        val   progress_ratio_transport = findViewById<TextView>(R.id.progress_ratio_transport)
//        val progress_ratio_food = findViewById<TextView>(R.id.progress_ratio_food)
//        val progress_ratio_house = findViewById<TextView>(R.id.progress_ratio_house)
//        val progress_ratio_ent = findViewById<TextView>(R.id.progress_ratio_ent)
//        val progress_ratio_edu = findViewById<TextView>(R.id.progress_ratio_edu)
//        val progress_ratio_cha = findViewById<TextView>(R.id.progress_ratio_cha)
//        val progress_ratio_app = findViewById<TextView>(R.id.progress_ratio_app)
//        val progress_ratio_hea = findViewById<TextView>(R.id.progress_ratio_hea)
//        val progress_ratio_per = findViewById<TextView>(R.id.progress_ratio_per)
//        val progress_ratio_oth = findViewById<TextView>(R.id.progress_ratio_oth)
//        //imageviews
//        val  status_Image_transport = findViewById<ImageView>(R.id.status_Image_transport)
//        val status_Image_food = findViewById<ImageView>(R.id.status_Image_food)
//        val status_Image_house = findViewById<ImageView>(R.id.status_Image_house)
//        val status_Image_ent = findViewById<ImageView>(R.id.status_Image_ent)
//        val status_Image_edu = findViewById<ImageView>(R.id.status_Image_edu)
//        val status_Image_cha = findViewById<ImageView>(R.id.status_Image_cha)
//        val status_Image_app = findViewById<ImageView>(R.id.status_Image_app)
//        val status_Image_hea = findViewById<ImageView>(R.id.status_Image_hea)
//        val status_Image_per = findViewById<ImageView>(R.id.status_Image_per)
//        val status_Image_oth = findViewById<ImageView>(R.id.status_Image_oth)
//
//        //anyChartView
//        val   anyChartView = findViewById<AnyChartView>(R.id.anyChartView);
//
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
        val dateFormat= SimpleDateFormat("dd-MM-yyyy")
        val cal = Calendar.getInstance()
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()
        val weeks= Weeks.weeksBetween(epoch,now)
        val value = weeks.weeks.toDouble()
       // val date= dateFormat.format(cal.time)
        val reference=FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query=reference.orderByChild("week").equalTo(value)
        query.addValueEventListener(object : ValueEventListener {
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
                   totalBudgetAmountTextView.setText("Total week's spending \u20B9 $totalamount")
                       //.setText("Total spent \u20B9 $totalamount")

                }
                else{
                    Log.d("tag","Failed test",null)
                    totalbudgetamount.setText("You've not spent anything this week")
                    anyChartViewweek.visibility= View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@WeeklyAnalyticsActivity,"Failed", Toast.LENGTH_LONG).show()
            }

        })
    }
    private fun getTodayTransExpenses(){
        val epoch=MutableDateTime()
        epoch.setDate(0)
        val cal = Calendar.getInstance()
        val now = DateTime()
        val weeks=Weeks.weeksBetween(epoch,now)

        val dateFormat=SimpleDateFormat("dd-MM-yyyy")
        val date= dateFormat.format(cal.time)
        val itemNday="Transport${weeks.weeks.toDouble()}"
        val reference=FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query = reference.orderByChild("itemweek").equalTo(itemNday)
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
                    personalRef.child("weekTrans").setValue(totalamount)
                }
                else{
                    linearLayoutTransportweek.visibility= View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@WeeklyAnalyticsActivity,"Failed",Toast.LENGTH_LONG).show()
            }

        })

    }
    private fun getTodayFoodExpenses(){
        val epoch=MutableDateTime()
        epoch.setDate(0)
        val cal = Calendar.getInstance()
        val now = DateTime()
        val weeks=Weeks.weeksBetween(epoch,now)

        val dateFormat=SimpleDateFormat("dd-MM-yyyy")
        val date= dateFormat.format(cal.time)
        val itemNday="Food${weeks.weeks.toDouble()}"
        val reference=FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query = reference.orderByChild("itemweek").equalTo(itemNday)
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
                    personalRef.child("weekFood").setValue(totalamount)
                }
                else{
                    linearLayoutFood.visibility= View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@WeeklyAnalyticsActivity,"Failed",Toast.LENGTH_LONG).show()
            }

        })
    }
    private fun getTodayHousExpenses(){
        val epoch=MutableDateTime()
        epoch.setDate(0)
        val cal = Calendar.getInstance()
        val now = DateTime()
        val weeks=Weeks.weeksBetween(epoch,now)

        val dateFormat=SimpleDateFormat("dd-MM-yyyy")
        val date= dateFormat.format(cal.time)
        val itemNday="House${weeks.weeks.toDouble()}"
        val reference=FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query = reference.orderByChild("itemweek").equalTo(itemNday)
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
                    personalRef.child("weekHous").setValue(totalamount)
                }
                else{
                    linearLayouthous.visibility= View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@WeeklyAnalyticsActivity,"Failed",Toast.LENGTH_LONG).show()
            }

        })

    }
    private fun getTodayEntExpenses(){
        val epoch=MutableDateTime()
        epoch.setDate(0)
        val cal = Calendar.getInstance()
        val now = DateTime()
        val weeks=Weeks.weeksBetween(epoch,now)

        val dateFormat=SimpleDateFormat("dd-MM-yyyy")
        val date= dateFormat.format(cal.time)
        val itemNday="Entertainment${weeks.weeks.toDouble()}"
        val reference=FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query = reference.orderByChild("itemweek").equalTo(itemNday)
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
                    personalRef.child("weekEnt").setValue(totalamount)
                }
                else{
                    linearLayoutEntertainmentweek.visibility= View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@WeeklyAnalyticsActivity,"Failed",Toast.LENGTH_LONG).show()
            }

        })

    }
    private fun getTodayEduExpenses(){
        val epoch=MutableDateTime()
        epoch.setDate(0)
        val cal = Calendar.getInstance()
        val now = DateTime()
        val weeks=Weeks.weeksBetween(epoch,now)

        val dateFormat=SimpleDateFormat("dd-MM-yyyy")
        val date= dateFormat.format(cal.time)
        val itemNday="Education${weeks.weeks.toDouble()}"
        val reference=FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query = reference.orderByChild("itemweek").equalTo(itemNday)
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
                    personalRef.child("weekEdu").setValue(totalamount)
                }
                else{
                    linearLayoutEducationweek.visibility= View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@WeeklyAnalyticsActivity,"Failed",Toast.LENGTH_LONG).show()
            }

        })

    }
    private fun getTodayCharExpenses(){
        val epoch=MutableDateTime()
        epoch.setDate(0)
        val cal = Calendar.getInstance()
        val now = DateTime()
        val weeks=Weeks.weeksBetween(epoch,now)

        val dateFormat=SimpleDateFormat("dd-MM-yyyy")
        val date= dateFormat.format(cal.time)
        val itemNday="Charity${weeks.weeks.toDouble()}"
        val reference=FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query = reference.orderByChild("itemweek").equalTo(itemNday)
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
                    personalRef.child("weekChar").setValue(totalamount)
                }
                else{
                    linearLayoutCharityweek.visibility= View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@WeeklyAnalyticsActivity,"Failed",Toast.LENGTH_LONG).show()
            }

        })

    }
    private fun getTodayAppExpenses(){
        val epoch=MutableDateTime()
        epoch.setDate(0)
        val cal = Calendar.getInstance()
        val now = DateTime()
        val weeks=Weeks.weeksBetween(epoch,now)

        val dateFormat=SimpleDateFormat("dd-MM-yyyy")
        val date= dateFormat.format(cal.time)
        val itemNday="Apparel${weeks.weeks.toDouble()}"
        val reference=FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query = reference.orderByChild("itemweek").equalTo(itemNday)
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
                    personalRef.child("weekApp").setValue(totalamount)
                }
                else{
                    linearLayoutApparelweek.visibility= View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@WeeklyAnalyticsActivity,"Failed",Toast.LENGTH_LONG).show()
            }

        })

    }
    private fun getTodayHealExpenses(){
        val epoch=MutableDateTime()
        epoch.setDate(0)
        val cal = Calendar.getInstance()
        val now = DateTime()
        val weeks=Weeks.weeksBetween(epoch,now)

        val dateFormat=SimpleDateFormat("dd-MM-yyyy")
        val date= dateFormat.format(cal.time)
        val itemNday="Health${weeks.weeks.toDouble()}"
        val reference=FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query = reference.orderByChild("itemweek").equalTo(itemNday)
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
                    personalRef.child("weekHeal").setValue(totalamount)
                }
                else{
                    linearLayoutHealthweek.visibility= View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@WeeklyAnalyticsActivity,"Failed",Toast.LENGTH_LONG).show()
            }

        })

    }
    private fun getTodayPerExpenses(){
        val epoch=MutableDateTime()
        epoch.setDate(0)
        val cal = Calendar.getInstance()
        val now = DateTime()
        val weeks=Weeks.weeksBetween(epoch,now)

        val dateFormat=SimpleDateFormat("dd-MM-yyyy")
        val date= dateFormat.format(cal.time)
        val itemNday="Personal${weeks.weeks.toDouble()}"
        val reference=FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query = reference.orderByChild("itemweek").equalTo(itemNday)
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
                    personalRef.child("weekPer").setValue(totalamount)
                }
                else{
                    linearLayoutPersonalExpweek.visibility= View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@WeeklyAnalyticsActivity,"Failed",Toast.LENGTH_LONG).show()
            }

        })

    }
    private fun getTodayOthExpenses(){
        val epoch=MutableDateTime()
        epoch.setDate(0)
        val cal = Calendar.getInstance()
        val now = DateTime()
        val weeks=Weeks.weeksBetween(epoch,now)

        val dateFormat=SimpleDateFormat("dd-MM-yyyy")
        val date= dateFormat.format(cal.time)
        val itemNday="Other${weeks.weeks.toDouble()}"
        val reference=FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query = reference.orderByChild("itemweek").equalTo(itemNday)
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
                    personalRef.child("weekOth").setValue(totalamount)
                }
                else{
                    linearLayoutOtherweek.visibility= View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@WeeklyAnalyticsActivity,"Failed",Toast.LENGTH_LONG).show()
            }

        })

    }
    private fun loadGraph(){
        personalRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    Log.d("Tag","Function test",null)
                    var traTotal:Int
                    if(snapshot.hasChild("weekTrans")){
                        traTotal= Integer.parseInt(snapshot.child("weekTrans").getValue().toString())
                    }
                    else{
                        traTotal=0
                    }
                    var foodTotal:Int
                    if(snapshot.hasChild("weekFood")){
                        foodTotal= Integer.parseInt(snapshot.child("weelFood").getValue().toString())
                    }
                    else{
                        foodTotal=0
                    }
                    var entTotal:Int
                    if(snapshot.hasChild("weekEnt")){
                        entTotal=Integer.parseInt(snapshot.child("weekEnt").getValue().toString())
                    }
                    else{
                        entTotal=0
                    }
                    var housTotal:Int
                    if(snapshot.hasChild("weekHous")){
                        housTotal=Integer.parseInt(snapshot.child("weekHous").getValue().toString())
                    }
                    else{
                        housTotal=0
                    }
                    var eduTotal:Int
                    if(snapshot.hasChild("weekEdu")){
                        eduTotal=Integer.parseInt(snapshot.child("weekEdu").getValue().toString())
                    }
                    else{
                        eduTotal=0
                    }
                    var charTotal:Int
                    if(snapshot.hasChild("weekChar")){
                        charTotal= Integer.parseInt(snapshot.child("weekChar").getValue().toString())
                    }
                    else{
                        charTotal=0
                    }
                    var appTotal:Int
                    if(snapshot.hasChild("weekApp")){
                        appTotal=Integer.parseInt(snapshot.child("weekApp").getValue().toString())
                    }
                    else{
                        appTotal=0
                    }
                    var healTotal:Int
                    if(snapshot.hasChild("weekHeal")){
                        healTotal= Integer.parseInt(snapshot.child("weekHeal").getValue().toString())
                    }
                    else{
                        healTotal=0
                    }
                    var perTotal:Int
                    if(snapshot.hasChild("weekPer")){
                        perTotal=Integer.parseInt(snapshot.child("weekPer").getValue().toString())
                    }
                    else{
                        perTotal=0
                    }
                    var othTotal:Int
                    if(snapshot.hasChild("weekOth")){
                        othTotal=Integer.parseInt(snapshot.child("weekOth").getValue().toString())
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
                    pie.title("Weekly Analytics")
                    pie.labels().position("outside")
                    pie.legend().title().enabled(true)
                    pie.legend().title()
                        .text("Items Spent On")
                        .padding(0.0, 0.0, 10.0, 0.0)
                    pie.legend()
                        .position("center-bottom")
                        .itemsLayout(LegendLayout.HORIZONTAL)
                        .align(Align.CENTER);

                    anyChartViewweek.setChart(pie);

                }
                else{
                    Toast.makeText(this@WeeklyAnalyticsActivity,"Child Does not exist",Toast.LENGTH_LONG).show()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}
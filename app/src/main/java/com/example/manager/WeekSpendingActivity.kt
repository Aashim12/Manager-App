package com.example.manager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.manager.adapters.TodaySpendingAdapter
import com.example.manager.adapters.WeekSpendingAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_week_spending.*
import org.joda.time.DateTime
import org.joda.time.Months
import org.joda.time.MutableDateTime
import org.joda.time.Weeks
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.google.firebase.database.FirebaseDatabase

class WeekSpendingActivity : AppCompatActivity() {
    private lateinit var weekref: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    var onlineUserId:String=""
    var type:String=""
    private lateinit var  toolbar :Toolbar
    private lateinit var weekitemAdapter: WeekSpendingAdapter
    private lateinit var myDatalist:ArrayList<Data>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_week_spending)
        val recyclerView=findViewById<RecyclerView>(R.id.recyclerview)
        toolbar=findViewById(R.id.toolbar)
        toolbar.setTitle("This Week's spending")
 //For recycler view
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = linearLayoutManager
 //Setting adapter for recycler view
        myDatalist= ArrayList()
        weekitemAdapter= WeekSpendingAdapter(myDatalist,this)
        recyclerView.adapter=weekitemAdapter

        mAuth= FirebaseAuth.getInstance()
        onlineUserId=mAuth.currentUser!!.uid

        // for week and month spendings.
        if (intent.extras != null)
        {
            type=intent.getStringExtra("type").toString()
            if(type.equals("week")){
                readWeekspending()
            }
            else if(type.equals("month")){
                readMonthspending()
            }
        }
      //  readMonthspending()
     // readWeekspending()
    }
//Month spending
    private fun readMonthspending() {
        val dateformat = SimpleDateFormat("dd-MM-yyyy")
        val cal = Calendar.getInstance()
        val date = dateformat.format(cal.time)
       toolbar=findViewById(R.id.toolbar)
    toolbar.setTitle("Month's Spending")
        cal.add(Calendar.DAY_OF_YEAR,7)
        val today=dateformat.format(cal.time)
        val progressBar=findViewById<ProgressBar>(R.id.progressbars)
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()
        val months:Months= Months.monthsBetween(epoch,now)
        val value = months.months.toDouble()

        weekref = FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query: Query = weekref.orderByChild("month").equalTo(value)

        query.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                myDatalist.clear()
                for(snap : DataSnapshot in snapshot.children){
                    val data = snap.getValue(Data::class.java)
                    myDatalist.add(data!!)
                }
                weekitemAdapter.notifyDataSetChanged()
                progressBar.visibility= View.GONE
                var totalamount:Int?=0
                for (snap: DataSnapshot in snapshot.children){
                    val data = snap.getValue(Data::class.java)
                    // myDatalist.add(data!!)
                    Log.d("Tag","Success",null)
                    totalamount = totalamount?.plus(data?.amount!!.toInt())
                    var stotal: String = "Total month spending : \u20B9 $totalamount"
                    totalweekamount.setText(stotal)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

//Week spending
    private fun readWeekspending() {
        val dateformat = SimpleDateFormat("dd-MM-yyyy")
        val cal = Calendar.getInstance()
        val date = dateformat.format(cal.time)
       val progressBar=findViewById<ProgressBar>(R.id.progressbars)
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()
       val weeks:Weeks= Weeks.weeksBetween(epoch,now)
        val value = weeks.weeks.toDouble()

        weekref = FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query: Query = weekref.orderByChild("week").equalTo(value)

        query.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
             myDatalist.clear()
                for(snap : DataSnapshot in snapshot.children){
                    val data = snap.getValue(Data::class.java)
                    myDatalist.add(data!!)
                }
                weekitemAdapter.notifyDataSetChanged()
                progressBar.visibility= View.GONE
                var totalamount:Int?=0
                for (snap: DataSnapshot in snapshot.children){
                    val data = snap.getValue(Data::class.java)
                   // myDatalist.add(data!!)
                    Log.d("Tag","Success",null)
                    totalamount = totalamount?.plus(data?.amount!!.toInt())
                    var stotal: String = "Total week spending : \u20B9 $totalamount"
                    totalweekamount.setText(stotal)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}




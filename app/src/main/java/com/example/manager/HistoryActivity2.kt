package com.example.manager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.manager.adapters.TodaySpendingAdapter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_history2.*
import android.app.DatePickerDialog
import android.util.Log
import android.view.View
import android.widget.DatePicker
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList
import org.joda.time.DateTimeFieldType.dayOfMonth





class HistoryActivity2 : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var todaySpendingAdapter: TodaySpendingAdapter
    private lateinit var expenseref:DatabaseReference
    private lateinit var personalref:DatabaseReference
    var onlineUserid:String=""
    private lateinit var myDatalist:ArrayList<Data>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history2)
        val toolbar=findViewById<Toolbar>(R.id.my_Feed_Toolbar)
        toolbar.setTitle("History")
        mAuth= FirebaseAuth.getInstance()
        onlineUserid=mAuth.currentUser!!.uid
         val recyclerView=findViewById<RecyclerView>(R.id.recyclerviewHistory)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
    //     recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = linearLayoutManager

        myDatalist= ArrayList()
        todaySpendingAdapter= TodaySpendingAdapter(myDatalist,this)
        recyclerView.adapter=todaySpendingAdapter

        search.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            this,
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        )
        datePickerDialog.show()
        Log.d("Tag","Date test",null)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val months=month+1
    //  val date="09-12-2021"
             var date :String=""
        if(dayOfMonth>9){
        if(months>9){
             date = "$dayOfMonth-$months-$year"
        }
            else{
             date = "$dayOfMonth-0$months-$year"
        }
        }
        else{
            if (months>9){
                 date = "0$dayOfMonth-$months-$year"
            }
            else{
                 date = "0$dayOfMonth-0$months-$year"
            }

        }
//        historytotalamountspent.visibility=View.VISIBLE
//        historytotalamountspent.setText(date)
        val reference=FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserid)
        val query=reference.orderByChild("date").equalTo(date)
        query.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    myDatalist.clear()
                    for (snap: DataSnapshot in snapshot.children) {
                        val data = snap.getValue(Data::class.java)
                        myDatalist.add(data!!)
                    }
                    todaySpendingAdapter.notifyDataSetChanged()
                    recyclerviewHistory.visibility = View.VISIBLE
                    Log.d("Tag", "Date test", null)
                    var totalamount = 0
                    for (ds in snapshot.children) {
                        val map = ds.value as Map<String, Any>?
                        val total = map!!["amount"]
                        val ptotal = total.toString().toInt()
                        totalamount += ptotal
                        if (totalamount > 0) {
                            historytotalamountspent.visibility = View.VISIBLE
                            historytotalamountspent.setText("You spent $totalamount on that day")

                        }
                    }
                }
                else{
                   historytotalamountspent.visibility=View.VISIBLE
                    historytotalamountspent.setText("There are no transactions from that day")
                }

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}
package com.example.manager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import org.joda.time.DateTime
import org.joda.time.Months
import org.joda.time.MutableDateTime
import java.text.SimpleDateFormat
import java.util.*
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.manager.adapters.TodaySpendingAdapter
import com.google.firebase.database.*
import com.google.firebase.database.core.Tag
import kotlinx.android.synthetic.main.activity_today_spending.*
import org.joda.time.Weeks
import kotlin.collections.ArrayList

class TodaySpendingActivity : AppCompatActivity() {
    private lateinit var expenseref: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    var onlineUserId:String=""
    private lateinit var todayitemAdapter:TodaySpendingAdapter
    private lateinit var myDatalist:ArrayList<Data>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_today_spending)
       val toolbar=findViewById<android.widget.Toolbar>(R.id.toolbar)
        toolbar.setTitle("Today's spending")
           toolbar.textAlignment=View.TEXT_ALIGNMENT_CENTER
        val recyclerView=findViewById<RecyclerView>(R.id.recyclerview)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        mAuth= FirebaseAuth.getInstance()
        onlineUserId=mAuth.currentUser!!.uid
        expenseref=FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
     //For recycler view
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = linearLayoutManager

         myDatalist= ArrayList()
        todayitemAdapter= TodaySpendingAdapter(myDatalist,this)
      recyclerView.adapter=todayitemAdapter

        readitem()

        fab.setOnClickListener {
            additemspenton()
        }

    }

    private fun readitem() {
        val dateformat = SimpleDateFormat("dd-MM-yyyy")
        val cal = Calendar.getInstance()
        val date = dateformat.format(cal.time)
        val totalamountspenton=findViewById<TextView>(R.id.totalamount)
        val reference:DatabaseReference= FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query:Query=reference.orderByChild("date").equalTo(date)
        query.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
    myDatalist.clear()
                var totalamount:Int?=0
                for (snap: DataSnapshot in snapshot.children){
                    val data = snap.getValue(Data::class.java)
                 myDatalist.add(data!!)
                    Log.d("Tag","Success",null)
                    totalamount = totalamount?.plus(data?.amount!!.toInt())
                    var stotal: String = "Total spending today : \u20B9 $totalamount"
                    totalamountspenton.setText(stotal)
                }
               todayitemAdapter.notifyDataSetChanged()
                progressbars.visibility=View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun additemspenton() {
        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogview = inflater.inflate(R.layout.input_layout, null)
        builder.setView(dialogview)


        val alertDialog = builder.create()
        val note =dialogview.findViewById<EditText>(R.id.note)
        val spinner = dialogview.findViewById<Spinner>(R.id.itemsspinner)
        val edittext = dialogview.findViewById<EditText>(R.id.amount)
        val cancel = dialogview.findViewById<Button>(R.id.cancel)
        val save = dialogview.findViewById<Button>(R.id.save)

        note.visibility= View.VISIBLE
        progressbars.visibility=View.VISIBLE
        save.setOnClickListener {
            val amount = edittext.text.toString()
            val item = spinner.selectedItem.toString()
            val notes = note.text.toString()
            if (TextUtils.isEmpty(amount)) {
                edittext.setError("Amount is required")
            }
            if (item.equals("Select item ")) {
                Toast.makeText(this, "Select a valid item ", Toast.LENGTH_LONG).show()
            }
            if(TextUtils.isEmpty(notes)){
                note.setError("Note is required")
            }
            else {
                val id = expenseref.push().key
                val dateformat = SimpleDateFormat("dd-MM-yyyy")
                val cal = Calendar.getInstance()
                val date = dateformat.format(cal.time)
                val epoch = MutableDateTime()
                epoch.setDate(0)
                val now = DateTime()
                val months = Months.monthsBetween(epoch, now)
                val weeks=Weeks.weeksBetween(epoch,now)
                val itemday=item.plus(date)
                val itemweek=item.plus(weeks.weeks)
                val itemmonth=item.plus(months.months)

                val data = Data(item, date, id,itemday,itemweek,itemmonth, notes, Integer.parseInt(amount), months.months,weeks.weeks)
                expenseref.child(id.toString()).setValue(data).addOnCompleteListener {

                    Toast.makeText(this, "Succesfully Saved", Toast.LENGTH_LONG).show()

                }.addOnFailureListener {
                    Log.d("Tag","Success",null)
                    Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
                }

            }
            alertDialog.dismiss()
        }
        cancel.setOnClickListener {
            alertDialog.dismiss()
        }


        alertDialog.show()
    }

}
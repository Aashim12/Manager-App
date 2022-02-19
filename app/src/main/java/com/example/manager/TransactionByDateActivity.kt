package com.example.manager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.manager.adapters.TransactionBydateAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_today_spending.*
import kotlinx.android.synthetic.main.activity_transaction_by_date.*

class TransactionByDateActivity : AppCompatActivity() {
    private lateinit var bydateAdapter: TransactionBydateAdapter
    private lateinit var datalist:ArrayList<Data>
    private lateinit var mAuth: FirebaseAuth
    var onlineUserId:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_by_date)
          mAuth= FirebaseAuth.getInstance()
        onlineUserId=mAuth.currentUser!!.uid
        val recyclerView=findViewById<RecyclerView>(R.id.recyclerviewbydate)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = linearLayoutManager
             datalist= ArrayList()
        bydateAdapter= TransactionBydateAdapter(datalist,this)
         recyclerView.adapter=bydateAdapter
        readitem()
    }
    private fun readitem(){
        val date="09-12-2021"
        val totalamountspenton=findViewById<TextView>(R.id.totalamount)
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query: Query =reference.orderByChild("date").equalTo(date)
        query.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                datalist.clear()
                var totalamount:Int?=0
                for(snap:DataSnapshot in snapshot.children){
                    val data=snap.getValue(Data::class.java)
                    datalist.add(data!!)
                    totalamount=totalamount?.plus(data!!.amount!!.toInt())
                    var stotal: String = "Total spending today : \u20B9 $totalamount"
                    totalamountspenton.setText(stotal)
                }
                bydateAdapter.notifyDataSetChanged()
                progressbarsbydate.visibility= View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}
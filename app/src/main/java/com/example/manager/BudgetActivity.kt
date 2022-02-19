package com.example.manager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_budget.*
import org.joda.time.DateTime
import org.joda.time.Months
import org.joda.time.MutableDateTime
import org.joda.time.Weeks
import java.text.SimpleDateFormat
import java.util.*

class BudgetActivity : AppCompatActivity() {
    private lateinit var budgetref: DatabaseReference
    private lateinit var personalref: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    var postkey: String? =""
    var itemupdate: String? =""
    private var amount: Int =0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)
        mAuth = FirebaseAuth.getInstance()
        personalref = FirebaseDatabase.getInstance().getReference().child("Personal").child(mAuth.currentUser!!.uid)
        budgetref = FirebaseDatabase.getInstance().getReference().child("Budget").child(mAuth.currentUser!!.uid)

        val totalBudgetamount = findViewById<TextView>(R.id.totalbudgetamountTextview)
        recyclerView = findViewById(R.id.recyclerview)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = linearLayoutManager

// Updating the total budget on top
        budgetref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalamount: Int? = 0
                for (snap: DataSnapshot in snapshot.children) {
                    var data = snap.getValue(Data::class.java)
                    totalamount = totalamount?.plus(data?.amount!!.toInt())
                    var stotal: String = "Monthly Expenditure: \u20B9 $totalamount"
                    totalBudgetamount.setText(stotal)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        )
        // Adding a new entry through floating action button
        fab.setOnClickListener {
            additem()
        }
         budgetref.addValueEventListener(object :ValueEventListener{
             override fun onDataChange(snapshot: DataSnapshot) {
                 if (snapshot.exists() && snapshot.childrenCount > 0) {
                     var totalamount = 0
                     for (snap in snapshot.children) {
                         var data = snap.getValue(Data::class.java)
                         totalamount = totalamount?.plus(data?.amount!!.toInt())
                         var stotal = "Monthly Expenditure : \u20B9 $totalamount"
                         totalBudgetamount.setText(stotal)
                     }
                     var weeklyBudget = totalamount / 4
                     var dailyBudget = totalamount / 30
                     personalref.child("budget").setValue(totalamount)
                     personalref.child("weeklyBudget").setValue(weeklyBudget)
                     personalref.child("dailyBudget").setValue(dailyBudget)
                 }
                 else{
                     personalref.child("budget").setValue(0)
                     personalref.child("weeklyBudget").setValue(0)
                     personalref.child("dailyBudget").setValue(0)
                 }
             }
             override fun onCancelled(error: DatabaseError) {
                 TODO("Not yet implemented")
             }

         })
       getMonthTransportRatio()
        getMonthFoodRatio()
        getMonthHouRatio()
        getMonthEntRatio()
        getMonthEduRatio()
        getMonthCharityRatio()
        getMonthAppRatio()
        getMonthHealthRatio()
        getMonthPerRatio()
        getMonthOthRatio()


    }
    //Adding a new entry
    private fun additem() {
        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogview = inflater.inflate(R.layout.input_layout, null)
        builder.setView(dialogview)


        val alertDialog = builder.create()
        val spinner = dialogview.findViewById<Spinner>(R.id.itemsspinner)
        val edittext = dialogview.findViewById<EditText>(R.id.amount)
        val cancel = dialogview.findViewById<Button>(R.id.cancel)
        val save = dialogview.findViewById<Button>(R.id.save)

        save.setOnClickListener {
            val budgetamount = edittext.text.toString()
            val budgetitem = spinner.selectedItem.toString()
            if (TextUtils.isEmpty(budgetamount)) {
                edittext.setError("Amount is required")
            }
            if (budgetitem.equals("Select item ")) {
                Toast.makeText(this, "Select a valid item ", Toast.LENGTH_LONG).show()
            } else {

                val id = budgetref.push().key
                val dateformat = SimpleDateFormat("dd-MM-yyyy")
                val cal = Calendar.getInstance()
                val date = dateformat.format(cal.time)
                val epoch = MutableDateTime()
                epoch.setDate(0)
                val now = DateTime()
                val months = Months.monthsBetween(epoch, now)
                val weeks=Weeks.weeksBetween(epoch,now)
                val itemday=budgetitem.plus(date)
                val itemweek=budgetitem.plus(weeks.weeks)
                val itemmonth=budgetitem.plus(months.months)
                val data =
                    Data(budgetitem, date, id, itemday,itemweek,itemmonth,null, Integer.parseInt(budgetamount), months.months,weeks.weeks)
                budgetref.child(id.toString()).setValue(data).addOnCompleteListener {
                    Toast.makeText(this, "Succesfully Saved", Toast.LENGTH_LONG).show()
                }.addOnFailureListener {
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

    override fun onStart() {
        super.onStart()

        val options = FirebaseRecyclerOptions.Builder<Data>()
            .setQuery(budgetref, Data::class.java)
            .build()
        val adapter = object : FirebaseRecyclerAdapter<Data, MyViewholder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewholder {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.retrive_layout, parent, false)
                return MyViewholder(itemView)
            }

            //model is p2 i.e. Data,\u20B9 is for Rs symbol
            override fun onBindViewHolder(p0: MyViewholder, p1: Int, p2: Data) {
                p0.amount.setText("Allocated amount: \u20B9 ${p2.amount}")
                p0.date.setText("On ${p2.date}")
                p0.itemname.setText("BudgetItem: ${p2.item}")
                p0.note.visibility = View.GONE
            // for updating and deleting the data
                p0.mview.setOnClickListener {
                    postkey = getRef(p1).key
                    itemupdate = p2.item
                    amount = p2.amount!!.toInt()
                    updateData()
                    Log.d("Tag","Success",null)
                }
            }
        }
        recyclerView.adapter = adapter
        adapter.startListening()
        adapter.notifyDataSetChanged()

    }


    class MyViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mview = itemView
        var imageView = itemView.findViewById<ImageView>(R.id.imageview)
        val amount = itemView.findViewById<TextView>(R.id.amount)
        val note: TextView = itemView.findViewById(R.id.note)
        val date: TextView = itemView.findViewById(R.id.date)
        val itemname: TextView = itemView.findViewById(R.id.item)


    }

    fun updateData() {
        val myDialog = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val mView = inflater.inflate(R.layout.update_layout, null)
        myDialog.setView(mView)
      val alertDialog= myDialog.create()

        var mItem= mView.findViewById<TextView>(R.id.itemname)
        var mamount=mView.findViewById<EditText>(R.id.amount)
        var mnotes=mView.findViewById<EditText>(R.id.note)
      mnotes.visibility=View.GONE
        mItem.setText(itemupdate)
      mamount.setText("$amount")


         val delbtn = mView.findViewById<Button>(R.id.btndelete)
        val updbtn = mView.findViewById<Button>(R.id.btnupdate)

            //Updating a current entry
        updbtn.setOnClickListener {
            amount = Integer.parseInt(mamount.text.toString())
            val dateformat = SimpleDateFormat("dd-MM-yyyy")
            val cal = Calendar.getInstance()
            val date = dateformat.format(cal.time)
            val epoch = MutableDateTime()
            epoch.setDate(0)
            val now = DateTime()
            val months = Months.monthsBetween(epoch, now)
             val weeks=Weeks.weeksBetween(epoch,now)
            val itemday=itemupdate.plus(date)
            val itemweek=itemupdate.plus(weeks.weeks)
            val itemmonth=itemupdate.plus(months.months)
            val data = Data(itemupdate, date, postkey,itemday,itemweek,itemmonth,null, amount, months.months,weeks.weeks)
            budgetref.child(postkey.toString()).setValue(data).addOnCompleteListener {
                Toast.makeText(this, "Updated Succesfully ", Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
            }

          alertDialog.dismiss()
        }
// Deleting a current entry
        delbtn.setOnClickListener {
            budgetref.child(postkey.toString()).removeValue().addOnCompleteListener {
                Toast.makeText(this, "Entry Removed", Toast.LENGTH_LONG).show()



            }.addOnFailureListener {
                Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
            }

            alertDialog.dismiss()

        }


alertDialog.show()
    }

//Ratios functions
    private fun getMonthTransportRatio() {
     val query=budgetref.orderByChild("item").equalTo("Transport")
    query.addValueEventListener(object:ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            if(snapshot.exists()){
                var ptotal=0
                for(snap in snapshot.children){
                    val map = snap.value as Map<String, Any>?
                    val total = map!!["amount"]
                     ptotal = total.toString().toInt()
                }
                val dayTransratio= ptotal/30
                val weekTransratio=ptotal/4
                val monthTransratio=ptotal

                   personalref.child("dayTransratio").setValue(dayTransratio)
                personalref.child("weekTransratio").setValue(weekTransratio)
                personalref.child("monthTransratio").setValue(monthTransratio)

            }
            else{
                personalref.child("dayTransratio").setValue(0)
                personalref.child("weekTransratio").setValue(0)
                personalref.child("monthTransratio").setValue(0)
            }


        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    })

    }

    private fun getMonthFoodRatio() {
        val query=budgetref.orderByChild("item").equalTo("Food")
        query.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var ptotal=0
                    for(snap in snapshot.children){
                        val map = snap.value as Map<String, Any>?
                        val total = map!!["amount"]
                        ptotal = total.toString().toInt()
                    }
                    val dayFoodratio= ptotal/30
                    val weekFoodratio=ptotal/4
                    val monthFoodratio=ptotal
                    personalref.child("dayFoodratio").setValue(dayFoodratio)
                    personalref.child("weekFoodratio").setValue(weekFoodratio)
                    personalref.child("monthFoodratio").setValue(monthFoodratio)

                }
                else{
                    personalref.child("dayFoodratio").setValue(0)
                    personalref.child("weekFoodratio").setValue(0)
                    personalref.child("monthFoodratio").setValue(0)

                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

private fun getMonthHouRatio(){
    val query=budgetref.orderByChild("item").equalTo("House")
    query.addValueEventListener(object:ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            if(snapshot.exists()){
                var ptotal=0
                for(snap in snapshot.children){
                    val map = snap.value as Map<String, Any>?
                    val total = map!!["amount"]
                    ptotal = total.toString().toInt()
                }
                val dayHousratio= ptotal/30
                val weekHousratio=ptotal/4
                val monthHousratio=ptotal
                personalref.child("dayHousratio").setValue(dayHousratio)
                personalref.child("weekHousratio").setValue(weekHousratio)
                personalref.child("monthHousratio").setValue(monthHousratio)

            }
            else{
                personalref.child("dayHousratio").setValue(0)
                personalref.child("weekHousratio").setValue(0)
                personalref.child("monthHousratio").setValue(0)

            }


        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    })

}
    private fun getMonthEntRatio(){
        val query=budgetref.orderByChild("item").equalTo("Entertainment")
            query.addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        var ptotal=0
                        for(snap in snapshot.children){
                            val map = snap.value as Map<String, Any>?
                            val total = map!!["amount"]
                            ptotal = total.toString().toInt()
                        }
                        val dayEntratio= ptotal/30
                        val weekEntratio=ptotal/4
                        val monthEntratio=ptotal
                        personalref.child("dayEntratio").setValue(dayEntratio)
                        personalref.child("weekEntratio").setValue(weekEntratio)
                        personalref.child("monthEntratio").setValue(monthEntratio)
                    }
                    else{
                        personalref.child("dayEntratio").setValue(0)
                        personalref.child("weekEntratio").setValue(0)
                        personalref.child("monthEntratio").setValue(0)

                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        }


    private fun getMonthEduRatio(){
        val query=budgetref.orderByChild("item").equalTo("Education")
        query.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var ptotal=0
                    for(snap in snapshot.children){
                        val map = snap.value as Map<String, Any>?
                        val total = map!!["amount"]
                        ptotal = total.toString().toInt()
                    }
                    val dayEduratio= ptotal/30
                    val weekEduratio=ptotal/4
                    val monthEduratio=ptotal
                    personalref.child("dayEduratio").setValue(dayEduratio)
                    personalref.child("weekEduratio").setValue(weekEduratio)
                    personalref.child("monthEduratio").setValue(monthEduratio)
                }
                else{
                    personalref.child("dayEduratio").setValue(0)
                    personalref.child("weekEduratio").setValue(0)
                    personalref.child("monthEduratio").setValue(0)

                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun getMonthCharityRatio(){
        val query=budgetref.orderByChild("item").equalTo("Charity")
        query.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var ptotal=0
                    for(snap in snapshot.children){
                        val map = snap.value as Map<String, Any>?
                        val total = map!!["amount"]
                        ptotal = total.toString().toInt()
                    }
                    val dayCharatio= ptotal/30
                    val weekCharatio=ptotal/4
                    val monthCharatio=ptotal
                    personalref.child("dayCharatio").setValue(dayCharatio)
                    personalref.child("weekCharatio").setValue(weekCharatio)
                    personalref.child("monthCharatio").setValue(monthCharatio)
                }
                else{
                    personalref.child("dayCharatio").setValue(0)
                    personalref.child("weekCharatio").setValue(0)
                    personalref.child("monthCharatio").setValue(0)

                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


    }

    private fun getMonthAppRatio(){
        val query=budgetref.orderByChild("item").equalTo("Apparel")
        query.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var ptotal=0
                    for(snap in snapshot.children){
                        val map = snap.value as Map<String, Any>?
                        val total = map!!["amount"]
                        ptotal = total.toString().toInt()
                    }
                    val dayAppratio= ptotal/30
                    val weekAppratio=ptotal/4
                    val monthAppratio=ptotal
                    personalref.child("dayAppratio").setValue(dayAppratio)
                    personalref.child("weekAppratio").setValue(weekAppratio)
                    personalref.child("monthAppratio").setValue(monthAppratio)
                }
                else{
                    personalref.child("dayAppratio").setValue(0)
                    personalref.child("weekAppratio").setValue(0)
                    personalref.child("monthAppratio").setValue(0)

                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun getMonthHealthRatio(){
        val query=budgetref.orderByChild("item").equalTo("Health")
        query.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var ptotal=0
                    for(snap in snapshot.children){
                        val map = snap.value as Map<String, Any>?
                        val total = map!!["amount"]
                        ptotal = total.toString().toInt()
                    }
                    val dayHealratio= ptotal/30
                    val weekHealratio=ptotal/4
                    val monthHealratio=ptotal
                    personalref.child("dayHealratio").setValue(dayHealratio)
                    personalref.child("weekHealratio").setValue(weekHealratio)
                    personalref.child("monthHealratio").setValue(monthHealratio)
                }
                else{
                    personalref.child("dayHealratio").setValue(0)
                    personalref.child("weekHealratio").setValue(0)
                    personalref.child("monthHealratio").setValue(0)

                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun getMonthPerRatio(){
        val query=budgetref.orderByChild("item").equalTo("Personal")
        query.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var ptotal=0
                    for(snap in snapshot.children){
                        val map = snap.value as Map<String, Any>?
                        val total = map!!["amount"]
                        ptotal = total.toString().toInt()
                    }
                    val dayPerratio= ptotal/30
                    val weekPerratio=ptotal/4
                    val monthPerratio=ptotal
                    personalref.child("dayPerratio").setValue(dayPerratio)
                    personalref.child("weekPerratio").setValue(weekPerratio)
                    personalref.child("monthPerratio").setValue(monthPerratio)
                }
                else{
                    personalref.child("dayPerratio").setValue(0)
                    personalref.child("weekPerratio").setValue(0)
                    personalref.child("monthPerratio").setValue(0)

                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun getMonthOthRatio(){
        val query=budgetref.orderByChild("item").equalTo("Other")
        query.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var ptotal=0
                    for(snap in snapshot.children){
                        val map = snap.value as Map<String, Any>?
                        val total = map!!["amount"]
                        ptotal = total.toString().toInt()
                    }
                    val dayOthratio= ptotal/30
                    val weekOthratio=ptotal/4
                    val monthOthratio=ptotal
                    personalref.child("dayOthratio").setValue(dayOthratio)
                    personalref.child("weekOthratio").setValue(weekOthratio)
                    personalref.child("monthOthratio").setValue(monthOthratio)
                }
                else{
                    personalref.child("dayOthratio").setValue(0)
                    personalref.child("weekOthratio").setValue(0)
                    personalref.child("monthOthratio").setValue(0)

                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
}
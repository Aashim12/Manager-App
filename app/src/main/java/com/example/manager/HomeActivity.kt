package com.example.manager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_home.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.getValue

import androidx.annotation.NonNull

import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.FirebaseDatabase

import com.google.firebase.database.DatabaseReference
import org.joda.time.DateTime
import org.joda.time.Months
import org.joda.time.MutableDateTime
import org.joda.time.Weeks
import java.lang.NumberFormatException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class HomeActivity : AppCompatActivity() {
   private lateinit var mAuth:FirebaseAuth
   private lateinit var expenseRef:DatabaseReference
    private lateinit var personalRef:DatabaseReference
    private lateinit var budgetRef:DatabaseReference
    var onlineUserId:String=""
   var totalAmountMonth:Int?=0
    var totalAmountBudget:Int?=0
    var totalAmountBudgetB:Int?=0
    var totalAmountBudgetC:Int?=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
          val budgetTv=findViewById<TextView>(R.id.budget_TV)
         val totayspendingTv=findViewById<TextView>(R.id.today_TV)
        val budgetremaining=findViewById<TextView>(R.id.saving_TV)
        val weekspendingTv=findViewById<TextView>(R.id.week_TV)
        val monthspendingTv=findViewById<TextView>(R.id.month_TV)

      mAuth= FirebaseAuth.getInstance()
        onlineUserId=FirebaseAuth.getInstance().currentUser!!.uid
        budgetRef=FirebaseDatabase.getInstance().getReference("Budget").child(onlineUserId)
        expenseRef=FirebaseDatabase.getInstance().getReference("Expense").child(onlineUserId)
        personalRef=FirebaseDatabase.getInstance().getReference("Personal").child(onlineUserId)

        budgetcardview.setOnClickListener {
      val intent = Intent(this,BudgetActivity::class.java)
      startActivity(intent)
  }
        todaycardview.setOnClickListener {
            val intent=Intent(this,TodaySpendingActivity::class.java)
            startActivity(intent)
        }

        weekcardview.setOnClickListener {
            val intent=Intent(this,WeekSpendingActivity::class.java)
            intent.putExtra("type","week")
            startActivity(intent)
        }
        monthcardview.setOnClickListener {
            val intent=Intent(this,WeekSpendingActivity::class.java)
            intent.putExtra("type","month")
            startActivity(intent)
        }
        analyticscardview.setOnClickListener {
            val intent =Intent(this,ChooseAnalyticsActivity::class.java)
            startActivity(intent)
        }
        historycardview.setOnClickListener {
            val intent=Intent(this,HistoryActivity2::class.java)
            startActivity(intent)
        }

        budgetRef.addValueEventListener(object:ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && snapshot.childrenCount > 0) {
                        for (ds: DataSnapshot in snapshot.children) {
                           val map = ds.value as Map<String, Any>?
//                            val map = snapshot.getValue<Map<String, String>>()
//                                ?: error("map missing in firebase snapshot")
//
                           val total = map!!["amount"]
                            Log.d("Tag", "$total", null)
                            val ptotal = total.toString().toInt()
                            totalAmountBudgetB = totalAmountBudgetB!!.plus(ptotal)
                            totalAmountBudgetC = totalAmountBudgetB;
                             personalRef.child("budget").setValue(totalAmountBudgetC);
                        }
                    }
                else {

                      personalRef.child("budget").setValue(0)
//                        Toast.makeText(this@HomeActivity, "Please set a budget", Toast.LENGTH_LONG)
//                            .show()
                    }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        getBudgetAmount()
        getTodaySpentAmount()
       getWeekSpentAmount()
        getMonthSpentAmount()
        getSavings()

    }
    private fun getBudgetAmount() {
        budgetRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && snapshot.childrenCount > 0) {
                    for (ds in snapshot.children) {
                        val map = ds.value as Map<String, Any>?
                        val total = map!!["amount"]
                        val ptotal = total.toString().toInt()
                        totalAmountBudgetB!!.plus(ptotal)
                        totalAmountBudget = totalAmountBudgetB
                        budget_TV.setText("\u20B9 $totalAmountBudget")
                    }
                } else {
                    budget_TV.setText("\u20B9 0")
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun getTodaySpentAmount() {
        val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy")
        val cal: Calendar = Calendar.getInstance()
        val date: String = dateFormat.format(cal.time)
        val reference = FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query = reference.orderByChild("date").equalTo(date)
        query.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                  var totalamount = 0
                    for (ds in snapshot.children) {
                        val map = ds.value as Map<String, Any>?
                        val total = map!!["amount"]
                        val pTotal = total.toString().toInt()
                        totalamount += pTotal
                        today_TV.setText("\u20b9 $totalamount")
                    }
                    personalRef.child("today").setValue(totalamount)
                }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity, "Failed", Toast.LENGTH_SHORT).show();
            }

        })

    }
    private fun getWeekSpentAmount() {
        val dateformat = SimpleDateFormat("dd-MM-yyyy")
        val cal = Calendar.getInstance()
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()
        val weeks: Weeks = Weeks.weeksBetween(epoch,now)
        val value = weeks.weeks.toDouble()
       val  reference = FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query: Query = reference.orderByChild("week").equalTo(value)
     query.addValueEventListener(object :ValueEventListener{
         override fun onDataChange(snapshot: DataSnapshot) {
             var totalamount = 0
             for (ds in snapshot.children) {
                 val map = ds.value as Map<String, Any>?
                 val total = map!!["amount"]
                 val pTotal = total.toString().toInt()
                 totalamount += pTotal
                 week_TV.setText("\u20b9 $totalamount")
             }
           //  personalRef.child("week").setValue(totalamount)
         }

         override fun onCancelled(error: DatabaseError) {
             Toast.makeText(this@HomeActivity, "Failed", Toast.LENGTH_SHORT).show();
         }

     })

    }
    private fun getMonthSpentAmount() {
        val epoch = MutableDateTime()
        epoch.setDate(0)
        val now = DateTime()
        val months: Months = Months.monthsBetween(epoch,now)
        val value = months.months.toDouble()
        val  reference = FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserId)
        val query: Query = reference.orderByChild("month").equalTo(value)
        query.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalamount = 0
                for (ds in snapshot.children) {
                    val map = ds.value as Map<String, Any>?
                    val total = map!!["amount"]
                    val pTotal = total.toString().toInt()
                    totalamount += pTotal
                    month_TV.setText("\u20b9 $totalamount")
                }
                personalRef.child("month").setValue(totalamount)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity, "Failed", Toast.LENGTH_SHORT).show();
            }

        })

    }

    private fun getSavings() {
      personalRef.addValueEventListener(object :ValueEventListener{
          override fun onDataChange(snapshot: DataSnapshot) {
              if(snapshot.exists()){
                  var budget:Int
                  if(snapshot.hasChild("budget")){
                      budget = Integer.parseInt(snapshot.child("budget").getValue().toString())
                  }
                  else{
                      budget=0
                  }
                  var monthspending:Int
                  if(snapshot.hasChild("month")){
                      monthspending = Integer.parseInt(snapshot.child("month").getValue().toString())
                      Log.d("Tag","Success",null)
                  }
                  else{
                      monthspending=0
                      Log.d("Tag","Fail",null)
                  }
                  var saving = budget - monthspending
                  saving_TV.setText("\u20B9 $saving")
              }
          }
          override fun onCancelled(error: DatabaseError) {
              TODO("Not yet implemented")
          }

      })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.nav_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.account){
          val intent = Intent(this,AccountActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}
package com.example.manager.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.manager.Data
import com.example.manager.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.joda.time.DateTime
import org.joda.time.Months
import org.joda.time.MutableDateTime
import org.joda.time.Weeks
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TodaySpendingAdapter(var mydatalist: ArrayList<Data>, var mcontext: Context) : RecyclerView.Adapter<TodaySpendingAdapter.Viewholder>() {

    var postkey: String =""
    var itemupdate: String? =""
    var note: String? =""
    var amount:Int?=0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
          val view = LayoutInflater.from(mcontext).inflate(R.layout.retrive_layout,parent,false)
        return Viewholder(view)
     }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
                     val data = mydatalist.get(position)
                   holder.item.setText("item ${data.item}")
                      holder.amount.setText("Amount : ${data.amount}")
                  holder.date.setText("On : ${data.date}")
        holder.note.setText("Note : ${data.notes}")
        holder.image.setImageResource(R.drawable.ic_baseline_house_24)

        holder.itemView.setOnClickListener {
            postkey = data.id.toString()
            itemupdate = data.item
            amount = data.amount!!.toInt()
            note = data.notes
            updateData()
            Log.d("Tag","Success",null)
        }

    }

    private fun updateData() {
        val myDialog = AlertDialog.Builder(mcontext)
        val inflater = LayoutInflater.from(mcontext)
        val mView = inflater.inflate(R.layout.update_layout, null)
        myDialog.setView(mView)
        val alertDialog= myDialog.create()

        var mItem= mView.findViewById<TextView>(R.id.itemname)
        var mamount=mView.findViewById<EditText>(R.id.amount)
        var mnotes=mView.findViewById<EditText>(R.id.note)

        mItem.setText(itemupdate)
        mamount.setText("$amount")
      mnotes.setText(note)
        mnotes.setSelection(note!!.length)

        val delbtn = mView.findViewById<Button>(R.id.btndelete)
        val updbtn = mView.findViewById<Button>(R.id.btnupdate)

        //Updating a current entry
        updbtn.setOnClickListener {
            amount = Integer.parseInt(mamount.text.toString())
            note = mnotes.text.toString()
            val dateformat = SimpleDateFormat("dd-MM-yyyy")
            val cal = Calendar.getInstance()
            val date = dateformat.format(cal.time)
            val epoch = MutableDateTime()
            epoch.setDate(0)
            val now = DateTime()
            val months = Months.monthsBetween(epoch, now)
            val reference = FirebaseDatabase.getInstance().getReference("Expenses")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
            val weeks= Weeks.weeksBetween(epoch,now)

            val itemday=itemupdate.plus(date)
            val itemweek=itemupdate.plus(weeks.weeks)
            val itemmonth=itemupdate.plus(months.months)

            val data = Data(itemupdate, date, postkey,itemday,itemweek,itemmonth, note, amount, months.months)
            reference.child(postkey).setValue(data).addOnCompleteListener {
                Toast.makeText(mcontext, "Updated Succesfully ", Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                Toast.makeText(mcontext, "Failed", Toast.LENGTH_LONG).show()
            }

            alertDialog.dismiss()
        }
// Deleting a current entry
        delbtn.setOnClickListener {
            val reference = FirebaseDatabase.getInstance().getReference("Expenses")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)

            reference.child(postkey).removeValue().addOnCompleteListener {
                Toast.makeText(mcontext, "Entry Removed", Toast.LENGTH_LONG).show()



            }.addOnFailureListener {
                Toast.makeText(mcontext, "Failed", Toast.LENGTH_LONG).show()
            }

            alertDialog.dismiss()

        }
        alertDialog.show()
    }

    override fun getItemCount(): Int {
        return mydatalist.size
    }

    class Viewholder(itemview:View):RecyclerView.ViewHolder(itemview){

        var item = itemview.findViewById<TextView>(R.id.item)
        var amount = itemview.findViewById<TextView>(R.id.amount)
        var note = itemview.findViewById<TextView>(R.id.note)
        var image = itemview.findViewById<ImageView>(R.id.imageview)
        var date = itemview.findViewById<TextView>(R.id.date)
    }
}
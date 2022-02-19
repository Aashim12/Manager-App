package com.example.manager.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.manager.Data
import com.example.manager.R

class WeekSpendingAdapter(var mydatalist: ArrayList<Data>, var mcontext: Context): RecyclerView.Adapter<WeekSpendingAdapter.Viewholder>() {
    var postkey: String? =""
    var itemupdate: String? =""
    var note: String? =""
    var amount:Int?=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
       val inflater = LayoutInflater.from(mcontext).inflate(R.layout.retrive_layout,parent,false)
        return Viewholder(inflater)
    }
//In week adapter we won't be updating the transactions as we are doing it in today adapter, we will just be presenting the entries
    // made in the week
    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val data = mydatalist.get(position)
        holder.item.setText("item ${data.item}")
        holder.amount.setText("Amount : ${data.amount}")
        holder.date.setText("On : ${data.date}")
        holder.note.setText("Note : ${data.notes}")
        holder.image.setImageResource(R.drawable.ic_baseline_house_24)

        holder.itemView.setOnClickListener {
            postkey = data.id
            itemupdate = data.item
            amount = data.amount!!.toInt()
            note = data.notes

            Log.d("Tag","Success",null)
        }
    }

    override fun getItemCount(): Int {
       return mydatalist.size
    }
    class Viewholder(itemview: View): RecyclerView.ViewHolder(itemview){

        var item = itemview.findViewById<TextView>(R.id.item)
        var amount = itemview.findViewById<TextView>(R.id.amount)
        var note = itemview.findViewById<TextView>(R.id.note)
        var image = itemview.findViewById<ImageView>(R.id.imageview)
        var date = itemview.findViewById<TextView>(R.id.date)
    }


}
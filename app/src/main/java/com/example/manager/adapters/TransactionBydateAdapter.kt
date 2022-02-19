package com.example.manager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.manager.Data
import com.example.manager.R

class TransactionBydateAdapter(var datalist : ArrayList<Data>,var mcontext : Context) : RecyclerView.Adapter<TransactionBydateAdapter.Viewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(mcontext).inflate(R.layout.retrive_layout,parent,false)
        return Viewholder(view)

    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val data = datalist.get(position)
        holder.item.setText("item ${data.item}")
        holder.amount.setText("Amount : ${data.amount}")
        holder.date.setText("On : ${data.date}")
        holder.note.setText("Note : ${data.notes}")
        holder.image.setImageResource(R.drawable.ic_baseline_house_24)
    }

    override fun getItemCount(): Int {
        return datalist.size

    }
    class Viewholder(itemview: View): RecyclerView.ViewHolder(itemview){

        var item = itemview.findViewById<TextView>(R.id.item)
        var amount = itemview.findViewById<TextView>(R.id.amount)
        var note = itemview.findViewById<TextView>(R.id.note)
        var image = itemview.findViewById<ImageView>(R.id.imageview)
        var date = itemview.findViewById<TextView>(R.id.date)
    }

}
package com.example.manager.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.manager.News
import com.example.manager.R
class Newsadapter(private val listener: NewsItemclicked): RecyclerView.Adapter<NewsViewHolder>() {
    private val items : ArrayList<News> = ArrayList()   //  Arraylist<News> is used coz other news type is used instead of string
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.items,parent,false)
        val viewHolder= NewsViewHolder(view)
        view.setOnClickListener {
            listener.onItemclicked(items[viewHolder.adapterPosition])
        }
     return viewHolder
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
       val currentItem = items[position]
        holder.titleview.text =currentItem.title
        holder.author.text=currentItem.author
        Glide.with(holder.itemView.context,).load(currentItem.imageurl).into(holder.imageview)
    }

    override fun getItemCount(): Int {
       return items.size
        }
    fun updateNews(updateNews: ArrayList<News>){
        items.clear()
        items.addAll(updateNews)
        notifyDataSetChanged() // this function calls all the functions in adapter again tells adapter to updates its item
    }

}
class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
val titleview:TextView= itemView.findViewById(R.id.title)
val imageview:ImageView=itemView.findViewById(R.id.image)
 val author:TextView=itemView.findViewById((R.id.author))
}
interface NewsItemclicked{
    fun onItemclicked(item:News)
}
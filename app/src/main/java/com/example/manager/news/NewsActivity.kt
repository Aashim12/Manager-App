package com.example.manager.news

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
//import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.manager.News
import com.example.manager.R
import com.example.manager.databinding.ActivityMainBinding
//import com.example.sharenews.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_news.*

class NewsActivity : AppCompatActivity(), NewsItemclicked {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
private lateinit var mAdapter: Newsadapter // to make a member function we add 'm' front of it
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
         recyclerview.layoutManager = LinearLayoutManager(this)
        fetchdata()
     mAdapter = Newsadapter(this)
        recyclerview.adapter = mAdapter
    }

private fun fetchdata(){
val url = "https://newsapi.org/v2/top-headlines?country=in&category=business&apiKey=0b3a20df7bd840d0a1ed1847c9494ad6"
  val jsonObjectRequest= object: JsonObjectRequest(com.android.volley.Request.Method.GET
,url,null,{
                             val newsJSONArray=it.getJSONArray("articles")
                               val newsarray = ArrayList<News>()  // News type from data class
                                for (i in 0 until newsJSONArray.length()){
                                    val newsJSONObject=newsJSONArray.getJSONObject(i) // to get all data requried for each article
                                 val news = News(
                                     newsJSONObject.getString("title"),
                                       newsJSONObject.getString("author"),
                                       newsJSONObject.getString("url"),
                                     newsJSONObject.getString("urlToImage") // all data added in data class
                                 )
                                    newsarray.add(news)
                                }
          mAdapter.updateNews(newsarray)  // when we got newsarray we called update news fucntion to pass newsarray into adapter

      },
      {
             Toast.makeText(this,"News not found ",Toast.LENGTH_LONG).show()
      }


  )
  {
      override fun getHeaders(): MutableMap<String, String> {
          val headers = HashMap<String, String>()
          headers["User-Agent"]="Mozilla/5.0"
          return headers
      }
  }


// Access the RequestQueue through your singleton class.
      MySingleton.MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)


}



    override fun onItemclicked(item: News) {

        val builder =  CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(item.url))

    }


}

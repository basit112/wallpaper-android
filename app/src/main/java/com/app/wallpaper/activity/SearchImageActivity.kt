package com.app.wallpaper.activity

import android.content.Context
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.app.wallpaper.R
import com.bumptech.glide.Glide
import java.net.URL


class SearchImageActivity : AppCompatActivity() {

    private lateinit var no_search_connection_layout : View
    private lateinit var retry_connection_button: Button
    private lateinit var query:String
    private lateinit var linearLayout: LinearLayout
    private lateinit var wallpaper_full_display:ImageView

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_image)
        retry_connection_button = findViewById(R.id.retry_connection_button)
        linearLayout = findViewById(R.id.search_view_container)
        wallpaper_full_display = findViewById(R.id.wallpaper_full_display)

        if (!isNetworkConnected()){
            no_search_connection_layout.visibility = View.VISIBLE
            linearLayout.visibility = View.INVISIBLE


        }

        retry_connection_button.setOnClickListener {
            if (isNetworkConnected()){
                no_search_connection_layout.visibility = View.INVISIBLE
                query?.let {
                    Log.d("query" , it)
                    searchPhotos(it) }
                linearLayout.visibility = View.VISIBLE
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun searchPhotos(it: String) {
        Glide.with(this)
            .load(it)
            .error(getDrawable(R.drawable.placeholder_image))
            .into(wallpaper_full_display)
        val url = URL(it)
        val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
        wallpaper_full_display.setImageBitmap(bmp)
         //DownloadImageTask( findViewById(R.id.wallpaper_full_display))
         //   .execute(it);

    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
package com.dicoding.storyapp.helper.factory

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.local.UserPreference
import com.dicoding.storyapp.data.local.dataStore
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import com.dicoding.storyapp.widget.ImageBannerWidget
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class StackRemoteViewsFactory(private val context: Context) :
    RemoteViewsService.RemoteViewsFactory {

    private val apiService = ApiConfig.getApiService()
    private val mWidgetItems = ArrayList<ListStoryItem>()
    private val mWidgetBitmap = ArrayList<Bitmap>()


    override fun onCreate() {
    }

    override fun onDataSetChanged() {
        runBlocking {
            mWidgetItems.clear()
            mWidgetBitmap.clear()
            val token = UserPreference.getInstance(context.dataStore).getUserSession().first().token

            try {
                val response = apiService.getStories("0", "Bearer ${token!!}")
                Log.d("Widget", "Response: ${response.listStory}")

                response.listStory?.take(5)?.forEach { image ->
                    if (image != null) {
                        val bitmap = downloadBitmap(image.photoUrl)
                        if (bitmap != null) {
                            Log.d("Widget", "Download success: ${image.photoUrl}")
                            mWidgetItems.add(image)
                            mWidgetBitmap.add(bitmap)
                        } else {
                            Log.e("Widget", "Error Downloading image bitmap: ${image.photoUrl}")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("WidgetStory", e.toString())
            }
        }
    }

    override fun onDestroy() {
    }

    override fun getCount(): Int {
        return mWidgetItems.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_item)
        val bitmap = downloadBitmap(mWidgetItems[position].photoUrl)
        remoteViews.setImageViewBitmap(R.id.imageView, bitmap)
        val extras = bundleOf(
            ImageBannerWidget.EXTRA_ITEM to position
        )
        val fillIntent = Intent()
        fillIntent.putExtras(extras)
        remoteViews.setOnClickFillInIntent(R.id.imageView, fillIntent)
        return remoteViews
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    private fun downloadBitmap(url: String?): Bitmap? {
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val inputStream = connection.inputStream
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
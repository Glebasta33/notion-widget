package com.trusov.notionwidget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService

class WidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        Log.d("WidgetServiceTag", "onGetViewFactory")
        val appWidgetId = intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID)
        val data = arrayListOf(
            "one", "two", "three", "four",
            "five", "six", "seven", "eight", "nine", "ten"
        )
        return WidgetItemFactory(applicationContext, appWidgetId ?: 0, data)
    }

    class WidgetItemFactory(
        private val context: Context,
        private val appWidgetId: Int,
        private val data: List<String>
    ) : RemoteViewsFactory {
        override fun onCreate() {
            SystemClock.sleep(500)
        }

        override fun onDataSetChanged() {
        }

        override fun onDestroy() {
           // closer data source
        }

        override fun getCount(): Int {
            return data.size
        }

        override fun getViewAt(position: Int): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.example_widget_item)
            views.setTextViewText(R.id.example_widget_item_text, data[position])
            SystemClock.sleep(500)
            return views
        }

        @SuppressLint("RemoteViewLayout")
        override fun getLoadingView(): RemoteViews {
            return RemoteViews(context.packageName, R.layout.loading_widget)
        }

        override fun getViewTypeCount(): Int {
            return 1
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun hasStableIds(): Boolean {
            return true
        }

    }
}
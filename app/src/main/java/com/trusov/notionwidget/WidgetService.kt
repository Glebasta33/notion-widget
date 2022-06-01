package com.trusov.notionwidget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.trusov.notionwidget.data.local.NotesDao
import kotlinx.coroutines.*
import javax.inject.Inject

class WidgetService : RemoteViewsService() {

    @Inject
    lateinit var notesDao: NotesDao

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        (this.applicationContext as App).component.inject(this)
        val appWidgetId = intent?.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        return WidgetItemFactory(applicationContext, appWidgetId ?: 0, notesDao)
    }

    class WidgetItemFactory(
        private val context: Context,
        private val appWidgetId: Int,
        private val notesDao: NotesDao
    ) : RemoteViewsFactory {

        private lateinit var data: List<String>
        val scope = CoroutineScope(Dispatchers.IO)
        private lateinit var job: Job

        override fun onCreate() {
            Log.d("WidgetServiceTag", "onCreate")
            job = scope.launch {
                data = notesDao.getNotes().map { it.text }
                Log.d("WidgetServiceTag", "onCreate. data = ${data.toString()}")
            }
        }

        override fun onDataSetChanged() {
            Log.d("WidgetServiceTag", "onDataSetChanged")
        }

        override fun onDestroy() {
            Log.d("WidgetServiceTag", "onDestroy")
        }

        override fun getCount(): Int {
            return data.size
        }

        override fun getViewAt(position: Int): RemoteViews {
            Log.d("WidgetServiceTag", "getViewAt")
            val views = RemoteViews(context.packageName, R.layout.example_widget_item)
            scope.launch {
                job.join()
                Log.d("WidgetServiceTag", "getViewAt. data = ${data.toString()}")
                views.setTextViewText(R.id.example_widget_item_text, data[position])
            }
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
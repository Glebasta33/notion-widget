package com.trusov.notionwidget.data

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.trusov.notionwidget.R
import com.trusov.notionwidget.data.WidgetService.Companion.CONTENT

class ListProvider(
    private val context: Context,
    intent: Intent
) : RemoteViewsService.RemoteViewsFactory {

    private val content: String = intent.getStringExtra(CONTENT) ?: ""

    override fun onCreate() {}

    override fun onDataSetChanged() {}

    override fun onDestroy() {}

    override fun getCount(): Int {
        return 1
    }

    override fun getViewAt(position: Int): RemoteViews {
        val remoteView = RemoteViews(
            context.packageName, R.layout.row
        )
        Log.d("NewClassesTag", "ListProvider. content: ${content}")
        remoteView.apply {
            setTextViewText(R.id.tv_note_text, content)
        }
        return remoteView
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }
}
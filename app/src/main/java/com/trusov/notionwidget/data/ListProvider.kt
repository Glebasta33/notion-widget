package com.trusov.notionwidget.data

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.trusov.notionwidget.R
import com.trusov.notionwidget.data.WidgetService.Companion.CONTENT
import com.trusov.notionwidget.data.WidgetService.Companion.TEXT_SIZE
import com.trusov.notionwidget.presentation.ConfigActivity.Companion.SMALL_TEXT_SIZE

class ListProvider(
    private val context: Context,
    intent: Intent
) : RemoteViewsService.RemoteViewsFactory {

    private val content: String = intent.getStringExtra(CONTENT) ?: ""
    private val textSize: Int = intent.getIntExtra(TEXT_SIZE, SMALL_TEXT_SIZE)

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
        Log.d("NewClassesTag", "ListProvider.textSize: $textSize,  content: $content")
        remoteView.apply {
            setTextViewText(R.id.tv_note_text, content)
            setFloat(R.id.tv_note_text, "setTextSize", textSize.toFloat())
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
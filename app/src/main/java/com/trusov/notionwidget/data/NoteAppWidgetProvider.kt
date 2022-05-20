package com.trusov.notionwidget.data

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.trusov.notionwidget.R
import com.trusov.notionwidget.presentation.MainActivity

class NoteAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Log.d("NoteAppWidgetTag", "onUpdate")
        appWidgetIds?.forEach { appWidgetId ->
            currentAppWidgetId = appWidgetId
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                Intent(context, NoteContentReceiver::class.java).apply {
                    putExtra("test", "test from NoteAppWidgetProvider")
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            Log.d("NoteAppWidgetTag", "appWidgetId = $appWidgetId")
            val views = RemoteViews(context?.packageName, R.layout.widget_layout).apply {
                setOnClickPendingIntent(R.id.tv_note_text, pendingIntent)
            }
            appWidgetManager?.updateAppWidget(appWidgetId, views)
        }
    }

    companion object {
        var currentAppWidgetId = 0
    }

}
package com.trusov.notionwidget.data

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.trusov.notionwidget.App
import com.trusov.notionwidget.R
import com.trusov.notionwidget.data.local.NotesDao
import com.trusov.notionwidget.presentation.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class NoteAppWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var notesDao: NotesDao

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        appWidgetIds?.forEach { appWidgetId ->
            updateWidget(context, appWidgetManager, appWidgetId, "onUpdate")
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        (context?.applicationContext as App).component.inject(this)
        super.onReceive(context, intent)
        val widgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        if (intent?.action == ACTION_WIDGET_BACK || intent?.action == ACTION_WIDGET_NEXT) {

            CoroutineScope(Dispatchers.IO).launch {
                val notes = notesDao.getNotes()
                var text = ""
                if (intent.action == ACTION_WIDGET_BACK) {
                    if (index == 0) {
                        index = notes.size - 1
                    } else {
                        index--
                    }
                    text = notes[index].text
                }
                if (intent.action == ACTION_WIDGET_NEXT) {
                    if (index == (notes.size - 1)) {
                        index = 0
                    } else {
                        index++
                    }
                    text = notes[index].text
                }

                widgetId?.let { it ->
                    if (it != AppWidgetManager.INVALID_APPWIDGET_ID) {
                        updateWidget(
                            context,
                            AppWidgetManager.getInstance(context),
                            it,
                            text
                        )
                    }
                }
            }

        }
    }

    companion object {
        private const val ACTION_WIDGET_BACK = "com.trusov.notionwidget.widget_back"
        private const val ACTION_WIDGET_NEXT = "com.trusov.notionwidget.widget_next"
        private var index = 0

        private fun updateWidget(
            context: Context?,
            appWidgetManager: AppWidgetManager?,
            appWidgetId: Int,
            content: String
        ) {
            val views = RemoteViews(context?.packageName, R.layout.widget_layout).apply {
                setTextViewText(R.id.tv_note_text, content)
                setOnClickPendingIntent(
                    R.id.iv_menu,
                    PendingIntent.getActivity(
                        context,
                        1,
                        Intent(context, MainActivity::class.java),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                )
                setOnClickPendingIntent(
                    R.id.iv_back,
                    PendingIntent.getBroadcast(
                        context,
                        2,
                        Intent(context, NoteAppWidgetProvider::class.java).apply {
                            putExtra("back", "back")
                            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                            action = ACTION_WIDGET_BACK
                        },
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                )
                setOnClickPendingIntent(
                    R.id.iv_forward,
                    PendingIntent.getBroadcast(
                        context,
                        3,
                        Intent(context, NoteAppWidgetProvider::class.java).apply {
                            putExtra("forward", "forward")
                            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                            action = ACTION_WIDGET_NEXT
                        },
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }
            appWidgetManager?.updateAppWidget(appWidgetId, views)
        }
    }

}
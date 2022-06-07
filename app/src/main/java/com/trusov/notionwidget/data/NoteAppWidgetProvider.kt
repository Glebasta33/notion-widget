package com.trusov.notionwidget.data

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.util.Log
import android.widget.RemoteViews
import com.trusov.notionwidget.App
import com.trusov.notionwidget.R
import com.trusov.notionwidget.data.local.NoteDbModel
import com.trusov.notionwidget.data.local.NotesDao
import com.trusov.notionwidget.presentation.ConfigActivity
import com.trusov.notionwidget.presentation.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class NoteAppWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var notesDao: NotesDao

    override fun onUpdate(context: Context?, manager: AppWidgetManager?, ids: IntArray?) {
        super.onUpdate(context, manager, ids)
        ids?.forEach { id ->
            (context?.applicationContext as App).component.inject(this)
            CoroutineScope(Dispatchers.IO).launch {
                val notes = notesDao.getNotes()
                updateWidget(context, manager, id, notes[0].text)
                Log.d("myLogs", "onUpdate")
            }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        (context?.applicationContext as App).component.inject(this)
        super.onReceive(context, intent)
        val id = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: 0
        if (id != AppWidgetManager.INVALID_APPWIDGET_ID) {
            CoroutineScope(Dispatchers.IO).launch {
                val notes = notesDao.getNotes()
                val text = when (intent?.action) {
                    ACTION_WIDGET_BACK -> getPreviousNote(notes, id)
                    ACTION_WIDGET_NEXT -> getNextNote(notes, id)
                    else -> notes[indexes["$INDEX_KEY-$id"] ?: 0].text

                }
                val manager = AppWidgetManager.getInstance(context)
                updateWidget(context, manager, id, text)
            }
        }
        Log.d("myLogs", "onReceive. k: ${indexes.keys}. v: ${indexes.values}")
    }



    companion object {
        private const val ACTION_WIDGET_BACK = "com.trusov.notionwidget.widget_back"
        private const val ACTION_WIDGET_NEXT = "com.trusov.notionwidget.widget_next"
        private const val INDEX_KEY = "index_key"
        private val indexes = mutableMapOf<String, Int>()

        private fun updateWidget(
            context: Context?,
            appWidgetManager: AppWidgetManager?,
            appWidgetId: Int,
            content: String
        ) {
            val sp =
                context?.getSharedPreferences(ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE)
            val color = sp?.getInt("${ConfigActivity.WIDGET_COLOR}-$appWidgetId", 0) ?: Color.DKGRAY

            Log.d("myLogs", "updateWidget. color: $color, content: $content")
            val views = RemoteViews(context?.packageName, R.layout.widget_layout).apply {
                setTextViewText(R.id.tv_note_text, content)
                setInt(R.id.widget_layout, "setBackgroundResource", getBackgroundDrawable(color))
                setOnClickPendingIntent(
                    R.id.iv_menu,
                    PendingIntent.getActivity(
                        context,
                        appWidgetId,
                        Intent(context, MainActivity::class.java),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                )
                setOnClickPendingIntent(
                    R.id.iv_settings,
                    PendingIntent.getActivity(
                        context,
                        appWidgetId,
                        Intent(context, ConfigActivity::class.java).apply {
                            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                        },
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                )
                setOnClickPendingIntent(
                    R.id.iv_back,
                    PendingIntent.getBroadcast(
                        context,
                        appWidgetId,
                        Intent(context, NoteAppWidgetProvider::class.java).apply {
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
                        appWidgetId,
                        Intent(context, NoteAppWidgetProvider::class.java).apply {
                            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                            action = ACTION_WIDGET_NEXT
                        },
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }
            appWidgetManager?.updateAppWidget(appWidgetId, views)
        }

        private fun getBackgroundDrawable(color: Int): Int {
            return when(color) {
                Color.DKGRAY -> R.drawable.widget_background
                Color.GREEN -> R.drawable.widget_background_green
                Color.BLUE -> R.drawable.widget_background_blue
                else -> R.drawable.widget_background
            }
        }

        private fun getPreviousNote(notes: List<NoteDbModel>, id: Int): String {
            if (!indexes.containsKey("$INDEX_KEY-$id")) {
                indexes["$INDEX_KEY-$id"] = 0
            }
            var index = indexes["$INDEX_KEY-$id"] ?: 0
            if (index == 0) {
                index = notes.size - 1
            } else {
                index--
            }
            indexes["$INDEX_KEY-$id"] = index
            return notes[index].text
        }

        private fun getNextNote(notes: List<NoteDbModel>, id: Int): String {
            if (!indexes.containsKey("$INDEX_KEY-$id")) {
                indexes["$INDEX_KEY-$id"] = 0
            }
            var index = indexes["$INDEX_KEY-$id"] ?: 0
            if (index == (notes.size - 1)) {
                index = 0
            } else {
                index++
            }
            indexes["$INDEX_KEY-$id"] = index
            return notes[index].text
        }
    }

    override fun onDeleted(context: Context?, ids: IntArray?) {
        super.onDeleted(context, ids)
        ids?.forEach { id ->
            indexes.remove("$INDEX_KEY-$id")
        }
    }
}
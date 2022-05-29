package com.trusov.notionwidget.data

import android.app.IntentService
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.trusov.notionwidget.App
import com.trusov.notionwidget.R
import com.trusov.notionwidget.data.local.NoteDbModel
import com.trusov.notionwidget.data.local.NotesDao
import kotlinx.coroutines.*
import javax.inject.Inject

class WidgetLaunchService : IntentService(NAME) {

    @Inject
    lateinit var notesDao: NotesDao
    private lateinit var notes: List<NoteDbModel>
    private lateinit var job: Job

    override fun onCreate() {
        Log.d("WidgetLaunchServiceTag", "onCreate")
        super.onCreate()
        (this.applicationContext as App).component.inject(this)
        job = CoroutineScope(Dispatchers.IO).launch {

        }
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.d("WidgetLaunchServiceTag", "onHandleIntent")
        CoroutineScope(Dispatchers.Main).launch {
            notes = notesDao.getNotes()

            intent?.let {
                if (it.hasExtra("back")) {
                    if (index == 0) {
                        index = notes.size - 1
                    } else {
                        index--
                    }
                    val text = notes[index].text
                    Log.d("WidgetLaunchServiceTag", "back ${index} $text")
                    withContext(Dispatchers.Main) {
                        updateWidget(this@WidgetLaunchService, text)
                    }
                }

                if (it.hasExtra("forward")) {
                    if (index == (notes.size - 1)) {
                        index = 0
                    } else {
                        index++
                    }
                    val text = notes[index].text
                    Log.d("WidgetLaunchServiceTag", "forward ${index} $text")
                    withContext(Dispatchers.Main) {
                        updateWidget(this@WidgetLaunchService, text)
                    }
                }
            }
        }
    }

    private fun updateWidget(
        context: Context?,
        text: String
    ) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val remoteViews =
            RemoteViews(context?.packageName, R.layout.widget_layout).apply {
                setTextViewText(R.id.tv_note_text, text)
            }
        val thisWidget = ComponentName(context!!, NoteAppWidgetProvider::class.java)
        appWidgetManager.updateAppWidget(
            thisWidget,
            remoteViews
        )
    }


    companion object {
        private const val NAME = "WidgetLaunchService"
        private var index = 0
    }

}
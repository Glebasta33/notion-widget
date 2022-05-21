package com.trusov.notionwidget.data

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.trusov.notionwidget.App
import com.trusov.notionwidget.R
import com.trusov.notionwidget.data.local.NotesDao
import com.trusov.notionwidget.domain.use_case.GetPageBlocksUseCase
import com.trusov.notionwidget.domain.use_case.GetPageIdsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NoteContentReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notesDao: NotesDao

    override fun onReceive(context: Context?, intent: Intent?) {
        (context?.applicationContext as App).component.inject(this)
        CoroutineScope(Dispatchers.IO).launch {
            val notes = notesDao.getNotes()
            intent?.let {
                if (it.hasExtra("back")) {
                    if (index == 0) {
                        index = notes.size - 1
                    } else {
                        index--
                    }
                    val text = notes[index].text
                    Log.d("NoteContentReceiverTag", "back $index $text")
                    withContext(Dispatchers.Main) {
                        updateWidget(context, text)
                    }
                }

                if (it.hasExtra("forward")) {
                    if (index == (notes.size - 1)) {
                        index = 0
                    } else {
                        index++
                    }
                    val text = notes[index].text
                    Log.d("NoteContentReceiverTag", "forward $index $text")
                    withContext(Dispatchers.Main) {
                        updateWidget(context, text)
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
        appWidgetManager.partiallyUpdateAppWidget(
            NoteAppWidgetProvider.currentAppWidgetId,
            remoteViews
        )
    }

    companion object {
        private var index = 0
    }

}
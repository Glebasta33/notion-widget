package com.trusov.notionwidget.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.trusov.notionwidget.domain.use_case.GetPageBlocksUseCase
import com.trusov.notionwidget.domain.use_case.GetPageIdsUseCase
import javax.inject.Inject

class NoteContentReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            if (it.hasExtra("back")) {
                Log.d("NoteContentReceiverTag", "${it.getStringExtra("back")}")
            }
            if (it.hasExtra("forward")) {
                Log.d("NoteContentReceiverTag", "${it.getStringExtra("forward")}")
            }
        }

    }

}
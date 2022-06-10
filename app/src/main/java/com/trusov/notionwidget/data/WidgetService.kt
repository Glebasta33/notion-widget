package com.trusov.notionwidget.data

import android.content.Intent
import android.util.Log
import android.widget.RemoteViewsService

class WidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        Log.d("NewClassesTag", "WidgetService. intent: ${intent.action?.toString()}")
        return ListProvider(this.applicationContext, intent)
    }

    companion object {
        const val CONTENT = "content"
    }
}
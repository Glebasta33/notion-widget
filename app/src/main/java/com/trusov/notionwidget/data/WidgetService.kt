package com.trusov.notionwidget.data

import android.content.Intent
import android.util.Log
import android.widget.RemoteViewsService

class WidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ListProvider(this.applicationContext, intent)
    }

    companion object {
        const val CONTENT = "content"
        const val TEXT_SIZE = "text_size"
    }
}
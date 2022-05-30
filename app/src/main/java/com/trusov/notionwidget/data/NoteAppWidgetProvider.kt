package com.trusov.notionwidget.data

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import com.trusov.notionwidget.R
import com.trusov.notionwidget.WidgetService
import com.trusov.notionwidget.presentation.MainActivity

class NoteAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        appWidgetIds?.forEach { appWidgetId ->
            currentAppWidgetId = appWidgetId

            val serviceIntent = Intent(context, WidgetService::class.java)
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            serviceIntent.data = Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME))

            val views = setupWidgetLayout(context)
            views.apply{
                setRemoteAdapter(R.id.example_widget_stack_view, serviceIntent)
                setEmptyView(R.id.example_widget_stack_view, R.id.example_widget_empty_view)
            }
            appWidgetManager?.updateAppWidget(appWidgetId, views)
        }
    }

    private fun setupWidgetLayout(context: Context?): RemoteViews {
        return RemoteViews(context?.packageName, R.layout.widget_layout).apply {
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
                PendingIntent.getService(
                    context,
                    2,
                    Intent(context, WidgetLaunchService::class.java).apply {
                        putExtra("back", "back")
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            setOnClickPendingIntent(
                R.id.iv_forward,
                PendingIntent.getService(
                    context,
                    3,
                    Intent(context, WidgetLaunchService::class.java).apply {
                        putExtra("forward", "forward")
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        }
    }


    companion object {
        var currentAppWidgetId = 0
    }

}
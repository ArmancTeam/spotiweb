package com.project.lol.features

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.project.lol.R
import com.project.lol.service.MediaNotificationService
import com.project.lol.ui.MainActivity

class SpotilolWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, id)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_PLAY -> sendAction(context, MediaNotificationService.ACTION_PLAY_PAUSE)
            ACTION_NEXT -> sendAction(context, MediaNotificationService.ACTION_NEXT)
            ACTION_PREV -> sendAction(context, MediaNotificationService.ACTION_PREV)
            ACTION_UPDATE -> updateAll(context)
        }
    }

    companion object {
        const val ACTION_PLAY = "com.project.lol.WIDGET_PLAY"
        const val ACTION_NEXT = "com.project.lol.WIDGET_NEXT"
        const val ACTION_PREV = "com.project.lol.WIDGET_PREV"
        const val ACTION_UPDATE = "com.project.lol.WIDGET_UPDATE"

        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, SpotilolWidget::class.java))
            for (id in ids) updateAppWidget(context, manager, id)
        }

        private fun updateAppWidget(context: Context, manager: AppWidgetManager, id: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_player)
            // Title/artist would be updated from service; for now placeholders
            val openIntent = Intent(context, MainActivity::class.java)
            val openPending = PendingIntent.getActivity(context, 0, openIntent, PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.widget_title, openPending)

            val playIntent = Intent(context, SpotilolWidget::class.java).setAction(ACTION_PLAY)
            views.setOnClickPendingIntent(R.id.widget_play, PendingIntent.getBroadcast(context, 1, playIntent, PendingIntent.FLAG_IMMUTABLE))

            val nextIntent = Intent(context, SpotilolWidget::class.java).setAction(ACTION_NEXT)
            views.setOnClickPendingIntent(R.id.widget_next, PendingIntent.getBroadcast(context, 2, nextIntent, PendingIntent.FLAG_IMMUTABLE))

            val prevIntent = Intent(context, SpotilolWidget::class.java).setAction(ACTION_PREV)
            views.setOnClickPendingIntent(R.id.widget_prev, PendingIntent.getBroadcast(context, 3, prevIntent, PendingIntent.FLAG_IMMUTABLE))

            manager.updateAppWidget(id, views)
        }

        private fun sendAction(context: Context, action: String) {
            context.sendBroadcast(Intent(action).setPackage(context.packageName))
        }
    }
}

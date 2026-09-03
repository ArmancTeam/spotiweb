package com.project.lol.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.project.lol.R
import com.project.lol.service.MediaNotificationService

class SpotilolWidget : AppWidgetProvider() {

    companion object {

        private const val ACTION_WIDGET_PLAY_PAUSE =
            "com.project.lol.widget.PLAY_PAUSE"

        private const val ACTION_WIDGET_NEXT =
            "com.project.lol.widget.NEXT"

        private const val ACTION_WIDGET_PREV =
            "com.project.lol.widget.PREV"

        private const val ACTION_WIDGET_REPEAT =
            "com.project.lol.widget.REPEAT"

        private fun actionPendingIntent(
            context: Context,
            action: String,
            requestCode: Int
        ): PendingIntent {

            val intent = Intent(action).apply {
                setPackage(context.packageName)
            }

            return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
            )
        }

        private fun updateSingleWidget(
            context: Context,
            manager: AppWidgetManager,
            widgetId: Int
        ) {
            val views = RemoteViews(
                context.packageName,
                R.layout.widget_spotilol
            )

            views.setTextViewText(
                R.id.widget_title,
                "🎵 Spotilol"
            )

            views.setTextViewText(
                R.id.widget_song,
                "Şarkı adı"
            )

            views.setTextViewText(
                R.id.widget_artist,
                "Sanatçı"
            )

            views.setOnClickPendingIntent(
                R.id.widget_prev,
                actionPendingIntent(
                    context,
                    ACTION_WIDGET_PREV,
                    1001
                )
            )

            views.setOnClickPendingIntent(
                R.id.widget_play_pause,
                actionPendingIntent(
                    context,
                    ACTION_WIDGET_PLAY_PAUSE,
                    1002
                )
            )

            views.setOnClickPendingIntent(
                R.id.widget_next,
                actionPendingIntent(
                    context,
                    ACTION_WIDGET_NEXT,
                    1003
                )
            )

            views.setOnClickPendingIntent(
                R.id.widget_repeat,
                actionPendingIntent(
                    context,
                    ACTION_WIDGET_REPEAT,
                    1004
                )
            )

            val launchIntent =
                context.packageManager
                    .getLaunchIntentForPackage(context.packageName)

            if (launchIntent != null) {
                val launchPendingIntent =
                    PendingIntent.getActivity(
                        context,
                        1005,
                        launchIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or
                            PendingIntent.FLAG_IMMUTABLE
                    )

                views.setOnClickPendingIntent(
                    R.id.widget_container,
                    launchPendingIntent
                )
            }

            manager.updateAppWidget(widgetId, views)
        }

        fun updateWidget(context: Context) {
            val manager = AppWidgetManager.getInstance(context)

            val component = ComponentName(
                context,
                SpotilolWidget::class.java
            )

            manager.getAppWidgetIds(component).forEach { widgetId ->
                updateSingleWidget(context, manager, widgetId)
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { widgetId ->
            updateSingleWidget(
                context,
                appWidgetManager,
                widgetId
            )
        }
    }

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        when (intent.action) {

            ACTION_WIDGET_PLAY_PAUSE -> {
                sendMediaAction(
                    context,
                    MediaNotificationService.ACTION_PLAY_PAUSE
                )
            }

            ACTION_WIDGET_NEXT -> {
                sendMediaAction(
                    context,
                    MediaNotificationService.ACTION_NEXT
                )
            }

            ACTION_WIDGET_PREV -> {
                sendMediaAction(
                    context,
                    MediaNotificationService.ACTION_PREV
                )
            }

            ACTION_WIDGET_REPEAT -> {
                sendMediaAction(
                    context,
                    MediaNotificationService.ACTION_REPEAT
                )
            }
        }

        super.onReceive(context, intent)
    }

    private fun sendMediaAction(
        context: Context,
        action: String
    ) {
        val intent = Intent(action).apply {
            setPackage(context.packageName)
        }

        context.sendBroadcast(intent)
    }
}

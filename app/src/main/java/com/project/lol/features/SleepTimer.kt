package com.project.lol.features

import android.content.Context
import android.os.CountDownTimer
import android.webkit.WebView
import android.widget.Toast

object SleepTimer {
    private var timer: CountDownTimer? = null
    private var endTime: Long = 0L

    fun start(context: Context, minutes: Int, webView: WebView?) {
        cancel()
        if (minutes <= 0) return
        val millis = minutes * 60_000L
        endTime = System.currentTimeMillis() + millis
        timer = object : CountDownTimer(millis, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                webView?.evaluateJavascript("actPlayPause(false)", null)
                Toast.makeText(context, "Sleep timer finished – playback paused", Toast.LENGTH_LONG).show()
                cancel()
            }
        }.start()
        Toast.makeText(context, "Sleep timer set for $minutes min", Toast.LENGTH_SHORT).show()
    }

    fun cancel() {
        timer?.cancel()
        timer = null
        endTime = 0L
    }

    fun isRunning(): Boolean = timer != null

    fun remainingMinutes(): Int {
        if (endTime == 0L) return 0
        return ((endTime - System.currentTimeMillis()) / 60_000).toInt().coerceAtLeast(0)
    }
}

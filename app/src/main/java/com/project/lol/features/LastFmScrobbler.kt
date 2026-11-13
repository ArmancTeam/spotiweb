package com.project.lol.features

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.MessageDigest

object LastFmScrobbler {
    private const val TAG = "LastFmScrobbler"
    private const val API_KEY = "YOUR_API_KEY_HERE" // User should set this
    private const val API_SECRET = "YOUR_API_SECRET_HERE"

    fun isConfigured(prefs: android.content.SharedPreferences): Boolean {
        return prefs.getString("LastFmApiKey", null)?.isNotBlank() == true &&
               prefs.getString("LastFmSession", null)?.isNotBlank() == true
    }

    suspend fun scrobble(artist: String, title: String, album: String?, prefs: android.content.SharedPreferences) = withContext(Dispatchers.IO) {
        val apiKey = prefs.getString("LastFmApiKey", null) ?: return@withContext
        val session = prefs.getString("LastFmSession", null) ?: return@withContext
        try {
            val timestamp = (System.currentTimeMillis() / 1000).toString()
            val params = mutableMapOf(
                "method" to "track.scrobble",
                "artist" to artist,
                "track" to title,
                "timestamp" to timestamp,
                "api_key" to apiKey,
                "sk" to session
            )
            if (!album.isNullOrBlank()) params["album"] = album
            val sig = generateSignature(params, prefs.getString("LastFmApiSecret", API_SECRET) ?: API_SECRET)
            params["api_sig"] = sig
            params["format"] = "json"

            val postData = params.entries.joinToString("&") { "${it.key}=${URLEncoder.encode(it.value, "UTF-8")}" }
            val conn = URL("https://ws.audioscrobbler.com/2.0/").openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.outputStream.write(postData.toByteArray())
            val code = conn.responseCode
            Log.d(TAG, "Scrobble response: $code")
        } catch (e: Exception) {
            Log.w(TAG, "Scrobble failed", e)
        }
    }

    private fun generateSignature(params: Map<String, String>, secret: String): String {
        val sorted = params.toSortedMap()
        val str = sorted.entries.joinToString("") { it.key + it.value } + secret
        return MessageDigest.getInstance("MD5").digest(str.toByteArray()).joinToString("") { "%02x".format(it) }
    }
}

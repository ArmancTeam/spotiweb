package com.project.lol.features

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object PlayHistory {
    private const val PREFS = "spotilol_history"
    private const val KEY = "recent_tracks"
    private const val MAX = 50

    fun addTrack(context: Context, title: String, artist: String, album: String? = null) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val arr = try { JSONArray(prefs.getString(KEY, "[]")) } catch (e: Exception) { JSONArray() }
        val obj = JSONObject().apply {
            put("title", title)
            put("artist", artist)
            put("album", album ?: "")
            put("time", System.currentTimeMillis())
        }
        // Remove duplicates
        val filtered = JSONArray()
        for (i in 0 until arr.length()) {
            val item = arr.getJSONObject(i)
            if (item.optString("title") != title || item.optString("artist") != artist) {
                filtered.put(item)
            }
        }
        filtered.put(obj)
        // Keep last MAX
        val finalArr = JSONArray()
        val start = maxOf(0, filtered.length() - MAX)
        for (i in start until filtered.length()) {
            finalArr.put(filtered.get(i))
        }
        prefs.edit().putString(KEY, finalArr.toString()).apply()
    }

    fun getRecent(context: Context): List<Map<String, String>> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val arr = try { JSONArray(prefs.getString(KEY, "[]")) } catch (e: Exception) { JSONArray() }
        val list = mutableListOf<Map<String, String>>()
        for (i in arr.length() - 1 downTo 0) {
            val obj = arr.getJSONObject(i)
            list.add(mapOf(
                "title" to obj.optString("title"),
                "artist" to obj.optString("artist"),
                "album" to obj.optString("album")
            ))
        }
        return list
    }
}

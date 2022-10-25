package com.example.playgroundevotor

import android.content.Context
import android.content.SharedPreferences

class Prefs(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("PREFS_FILENAME", 0)

    var logs: String
        get() = prefs.getString("logs", "").toString()
        set(value) = prefs.edit().putString("logs", value).apply()
}
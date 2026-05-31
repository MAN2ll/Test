package com.securevault.ui.theme

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class ThemeMode(val key: String, val label: String, val emoji: String) {
    DARK("dark",   "Тёмная",   "🌙"),
    LIGHT("light", "Светлая",  "☀️"),
    SYSTEM("sys",  "Системная","📱")
}

object ThemeManager {
    private const val PREF_FILE = "sv_theme"
    private const val PREF_KEY  = "mode"

    private val _mode = MutableStateFlow(ThemeMode.DARK)
    val mode: StateFlow<ThemeMode> = _mode

    fun init(ctx: Context) {
        val prefs = prefs(ctx)
        val saved = prefs.getString(PREF_KEY, ThemeMode.DARK.key)
        _mode.value = ThemeMode.entries.firstOrNull { it.key == saved } ?: ThemeMode.DARK
    }

    fun setMode(ctx: Context, mode: ThemeMode) {
        _mode.value = mode
        prefs(ctx).edit().putString(PREF_KEY, mode.key).apply()
    }

    fun currentMode(): ThemeMode = _mode.value

    private fun prefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
}

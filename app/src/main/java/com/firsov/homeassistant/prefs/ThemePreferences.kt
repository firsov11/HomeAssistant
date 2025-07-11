package com.firsov.homeassistant.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object ThemePreferences {
    private val Context.dataStore by preferencesDataStore("theme_prefs")

    private val DARK_MODE = booleanPreferencesKey("dark_mode")

    fun isDarkThemeFlow(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[DARK_MODE] ?: false }

    suspend fun setDarkTheme(context: Context, enabled: Boolean) {
        context.dataStore.edit { it[DARK_MODE] = enabled }
    }
}

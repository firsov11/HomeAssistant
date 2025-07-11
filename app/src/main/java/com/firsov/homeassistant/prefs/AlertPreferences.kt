package com.firsov.homeassistant.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlertPreferences(private val context: Context) {

    companion object {
        private val Context.dataStore by preferencesDataStore(name = "alert_prefs")
        private val ALERT_ENABLED_KEY = booleanPreferencesKey("alert_enabled")

        // Статический Flow для удобства
        fun isAlertEnabledFlow(context: Context): Flow<Boolean> {
            return context.dataStore.data
                .map { prefs -> prefs[ALERT_ENABLED_KEY] ?: false }
        }
    }

    // Flow для отслеживания текущего состояния оповещений
    val alertEnabledFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[ALERT_ENABLED_KEY] ?: false }

    // Сохранение состояния оповещений
    suspend fun setAlertEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[ALERT_ENABLED_KEY] = enabled
        }
    }
}
package com.firsov.homeassistant

import AppNavigation
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.firsov.homeassistant.prefs.AlertPreferences
import com.firsov.homeassistant.prefs.ThemePreferences
import com.firsov.homeassistant.service.AlertService
import com.firsov.homeassistant.ui.components.RequestNotificationPermissionIfNeeded
import com.firsov.homeassistant.ui.theme.HomeAssistantTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val isDarkThemeState = ThemePreferences.isDarkThemeFlow(context).collectAsState(initial = false)
            val isDarkTheme = isDarkThemeState.value

            HomeAssistantTheme(darkTheme = isDarkTheme) {

                // ✅ Запуск службы один раз
                SideEffect {
                    startAlertServiceIfEnabled(context)
                }

                // 🔔 Разрешение на уведомления
                RequestNotificationPermissionIfNeeded()

                // 🌐 Навигация
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavigation(navController)
                }
            }
        }
    }

    private fun startAlertServiceIfEnabled(context: Context) {
        lifecycleScope.launch {
            val enabled = AlertPreferences.isAlertEnabledFlow(context).first()
            if (enabled) {
                val intent = Intent(context, AlertService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            }
        }
    }
}


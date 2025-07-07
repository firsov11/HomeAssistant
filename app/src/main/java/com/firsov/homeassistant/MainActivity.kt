package com.firsov.homeassistant

import AppNavigation
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.firsov.homeassistant.service.AlertService
import com.firsov.homeassistant.ui.theme.HomeAssistantTheme
import com.firsov.homeassistant.ui.components.RequestNotificationPermissionIfNeeded

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HomeAssistantTheme {
                val context = LocalContext.current

                // ⏯️ Запускаем AlertService при старте
                LaunchedEffect(Unit) {
                    val intent = Intent(context, AlertService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent)
                    } else {
                        context.startService(intent)
                    }
                }

                // 🔔 Запрашиваем разрешение на уведомления
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
}


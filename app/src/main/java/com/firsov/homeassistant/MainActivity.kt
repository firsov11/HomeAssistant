package com.firsov.homeassistant

import DevicesScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.firsov.homeassistant.ui.screens.SendDeviceDataScreen
import com.firsov.homeassistant.ui.theme.HomeAssistantTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeAssistantTheme {
                DevicesScreen()
            }
        }
    }
}

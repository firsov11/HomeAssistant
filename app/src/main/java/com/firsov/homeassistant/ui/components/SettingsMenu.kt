package com.firsov.homeassistant.ui.components

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.firsov.homeassistant.prefs.AlertPreferences
import com.firsov.homeassistant.prefs.ThemePreferences
import com.firsov.homeassistant.service.AlertService
import com.firsov.homeassistant.ui.theme.LocalExtraColors
import kotlinx.coroutines.launch


@Composable
fun SettingsMenu(
    context: Context,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }

    val alertPreferences = remember { AlertPreferences(context) }
    val alertEnabled by alertPreferences.alertEnabledFlow.collectAsState(initial = false)
    var switchChecked by remember { mutableStateOf(alertEnabled) }

    val themeEnabledFlow = remember { ThemePreferences.isDarkThemeFlow(context) }
    val isDarkTheme by themeEnabledFlow.collectAsState(initial = false)
    var darkThemeChecked by remember { mutableStateOf(isDarkTheme) }

    val extraColors = LocalExtraColors.current

    LaunchedEffect(alertEnabled) { switchChecked = alertEnabled }
    LaunchedEffect(isDarkTheme) { darkThemeChecked = isDarkTheme }

    Box {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Настройки",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(0.dp, 0.dp),
            modifier = Modifier
                .background(Color.Transparent)
                .padding(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = extraColors.darkGrayBackground,
                        shape = MaterialTheme.shapes.medium
                    )
            ) {
                MenuItem(
                    icon = Icons.Default.Science,
                    text = "Тест",
                    onClick = {
                        expanded = false
                        navController.navigate("send_test")
                    }
                )
                MenuItemWithSwitch(
                    icon = Icons.Default.Notifications,
                    text = "Оповещения",
                    checked = switchChecked,
                    onCheckedChange = { enabled ->
                        switchChecked = enabled
                        scope.launch {
                            alertPreferences.setAlertEnabled(enabled)
                            val intent = Intent(context, AlertService::class.java)
                            if (enabled) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    context.startForegroundService(intent)
                                } else {
                                    context.startService(intent)
                                }
                            } else {
                                context.stopService(intent)
                            }
                        }
                    }
                )
                MenuItemWithSwitch(
                    icon = Icons.Default.DarkMode,
                    text = "Тёмная тема",
                    checked = darkThemeChecked,
                    onCheckedChange = { enabled ->
                        darkThemeChecked = enabled
                        scope.launch {
                            ThemePreferences.setDarkTheme(context, enabled)
                        }
                    }
                )
            }
        }


    }
}


@Composable
fun MenuItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .padding(bottom = 4.dp), // 👈 Добавили одинаковый отступ снизу
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}



@Composable
fun MenuItemWithSwitch(
    icon: ImageVector,
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .padding(bottom = 4.dp), // 👈 Добавили такой же отступ
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}





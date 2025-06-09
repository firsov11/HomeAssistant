package com.firsov.homeassistant.ui.screens

import DeviceData
import DeviceType
import InfoRow
import RealtimeDatabaseViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.SensorsOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentilationScreen(viewModel: RealtimeDatabaseViewModel = viewModel()) {
    val devices by viewModel.devices.collectAsState()
    val ventilationDevices = devices
        .filter { it.type == DeviceType.VENTILATION }
        .sortedByDescending { it.human_time }

    var ventilationEnabled by remember { mutableStateOf(true) }

    // Слушаем значение включения радара из Firebase
    LaunchedEffect(Unit) {
        FirebaseDatabase.getInstance().getReference("device_control/Vent")
            .get().addOnSuccessListener { snapshot ->
                snapshot.getValue(Boolean::class.java)?.let {
                    ventilationEnabled = it
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ventilation devices") },
                actions = {
                    IconButton(
                        onClick = {
                            val newState = !ventilationEnabled
                            FirebaseDatabase.getInstance()
                                .getReference("device_control")
                                .child("Vent")
                                .setValue(newState)
                            ventilationEnabled = newState
                        }
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (ventilationEnabled) Icons.Default.Sensors else Icons.Outlined.SensorsOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (ventilationEnabled) "Отключить" else "Включить",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(ventilationDevices) { device ->
                    VentCard(device)
                }
            }
        }
    }
}


@Composable
fun VentCard(device: DeviceData) {
    val colorScheme = MaterialTheme.colorScheme

    val containerColor = if (device.vent) {
        colorScheme.primaryContainer
    } else {
        colorScheme.secondaryContainer
    }

    val contentColor = if (device.vent) {
        colorScheme.onPrimaryContainer
    } else {
        colorScheme.onSecondaryContainer
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        color = containerColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfoRow(
                icon = Icons.Default.QrCode2,
                iconColor = colorScheme.primary,
                text = "ID: ${device.device_id}"
            )
            InfoRow(
                icon = if (device.presence) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                iconColor = colorScheme.primary,
                text = "Вентиляция: ${if (device.presence) "Вкл" else "Выкл"}"
            )
            InfoRow(
                icon = Icons.Default.AccessTime,
                iconColor = colorScheme.primary,
                text = device.human_time
            )
        }
    }
}
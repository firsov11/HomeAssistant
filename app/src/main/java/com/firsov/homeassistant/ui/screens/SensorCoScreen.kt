package com.firsov.homeassistant.ui.screens

import DeviceData
import DeviceType
import InfoRow
import RealtimeDatabaseViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorCoScreen(viewModel: RealtimeDatabaseViewModel = viewModel()) {
    val devices by viewModel.devices.collectAsState()
    val coDevices = devices
        .filter { it.type == DeviceType.CO }
        .sortedByDescending { it.human_time }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MQ-5 devices") }
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
                items(coDevices) { device ->
                    CoCard(device)
                }
            }
        }
    }
}


@Composable
fun CoCard(device: DeviceData) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        color = colorScheme.secondaryContainer,
        contentColor = colorScheme.onSecondaryContainer,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfoRow(
                icon = Icons.Filled.QrCode2,
                iconColor = colorScheme.primary,
                text = "ID: ${device.device_id}"
            )
            InfoRow(
                icon = Icons.Filled.Speed,
                iconColor = colorScheme.primary,
                text = "Уровень угарного газа: ${device.co} ."
            )
            InfoRow(
                icon = Icons.Filled.AccessTime,
                iconColor = colorScheme.primary,
                text = device.human_time
            )
        }
    }
}
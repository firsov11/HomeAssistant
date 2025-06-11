package com.firsov.homeassistant.ui.screens

import DeviceType
import RealtimeDatabaseViewModel
import SensorData
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: RealtimeDatabaseViewModel = viewModel()
) {
    val devices by viewModel.devices.collectAsState(initial = emptyList())
    val deviceControl by viewModel.deviceControl.collectAsState()

    val radarDevices = devices.filter { it.type == DeviceType.RADAR }
    val latestHumanTime = radarDevices.maxByOrNull { it.human_time }?.human_time ?: ""
    val radarPresence = radarDevices.any { it.human_time == latestHumanTime && it.presence }

    val latestSensor = devices
        .filter { it.type == DeviceType.TEMPERATURE_HUMIDITY }
        .maxByOrNull { it.human_time }

    val latestSensorOut = devices
        .filter { it.type == DeviceType.TEMPERATURE_HUMIDITY_OUT }
        .maxByOrNull { it.human_time }

    val latestSensorPressure = devices
        .filter { it.type == DeviceType.PRESSURE }
        .maxByOrNull { it.human_time }

    val latestSensorCo = devices
        .filter { it.type == DeviceType.CO }
        .maxByOrNull { it.human_time }

    val radarButtonColor = when {
        !deviceControl.radar -> Color.Gray
        radarPresence -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.primary
    }

    val radarContentColor = if (!deviceControl.radar) {
        Color.DarkGray
    } else {
        MaterialTheme.colorScheme.onPrimaryContainer
    }

    val radarIcon = when {
        !deviceControl.radar -> Icons.Outlined.Block
        radarPresence -> Icons.Default.Visibility
        else -> Icons.Default.VisibilityOff
    }

    val ventIcon = if (deviceControl.vent) Icons.Default.Air else Icons.Outlined.Block

    val ventButtonColor = when {
        !deviceControl.vent -> Color.Gray
        else -> MaterialTheme.colorScheme.primaryContainer
    }

    val ventContentColor = if (!deviceControl.vent) {
        Color.DarkGray
    } else {
        MaterialTheme.colorScheme.onPrimaryContainer
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Домашний ассистент") },
                actions = {
                    TextButton(onClick = { navController.navigate("send_test") }) {
                        Text("Тест", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        }
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val maxBoxWidth = this.maxWidth
            val squareSize = maxBoxWidth / 2

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(2f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // --- В ДОМЕ ---
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "В доме",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                            )

                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier
                                        .width(squareSize)
                                        .height(squareSize)
                                        .padding(1.dp)
                                ) {
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        RVButton(
                                            size = squareSize / 2,
                                            icon = radarIcon,
                                            text = "Радар",
                                            onClick = { navController.navigate("radar") },
                                            buttonColor = radarButtonColor,
                                            contentColor = radarContentColor
                                        )

                                        RVButton(
                                            size = squareSize / 2,
                                            icon = ventIcon,
                                            text = "Вент",
                                            onClick = { navController.navigate("vent") },
                                            buttonColor = ventButtonColor,
                                            contentColor = ventContentColor
                                        )
                                    }

                                    SquareSensorButton(
                                        size = squareSize,
                                        data = listOf(
                                            SensorData(Icons.Filled.Speed, latestSensorCo?.co, "ppm", "CO")
                                        )
                                    ) {
                                        navController.navigate("co")
                                    }
                                }

                                SquareSensorButton(
                                    size = squareSize,
                                    data = listOf(
                                        SensorData(
                                            Icons.Filled.Thermostat,
                                            latestSensor?.temperature,
                                            "°C",
                                            "Температура"
                                        ),
                                        SensorData(
                                            Icons.Filled.WaterDrop,
                                            latestSensor?.humidity,
                                            "%",
                                            "Влажность"
                                        )
                                    )
                                ) {
                                    navController.navigate("sensor")
                                }
                            }
                        }
                    }

                    // --- НА УЛИЦЕ ---
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "На улице",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                            )

                            Row(modifier = Modifier.fillMaxWidth()) {
                                SquareSensorButton(
                                    size = squareSize,
                                    data = listOf(
                                        SensorData(
                                            Icons.Filled.Speed,
                                            latestSensorPressure?.pressure,
                                            " мм рт. ст",
                                            "Давление"
                                        )
                                    )
                                ) {
                                    navController.navigate("sensor_pressure")
                                }

                                SquareSensorButton(
                                    size = squareSize,
                                    data = listOf(
                                        SensorData(
                                            Icons.Filled.Thermostat,
                                            latestSensorOut?.tempout,
                                            "°C",
                                            "Температура"
                                        ),
                                        SensorData(
                                            Icons.Filled.WaterDrop,
                                            latestSensorOut?.humout,
                                            "%",
                                            "Влажность"
                                        )
                                    )
                                ) {
                                    navController.navigate("sensorout")
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Добро пожаловать!", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
fun RVButton(
    size: Dp,
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    buttonColor: Color,
    contentColor: Color
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(size)
            .height(size)
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = contentColor
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(32.dp),
                tint = contentColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                color = contentColor
            )
        }
    }
}




@Composable
fun SquareSensorButton(
    size: Dp,
    data: List<SensorData>,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(size)
            .height(size)
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            data.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.value?.let { " $it${item.unit}" } ?: " —",
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

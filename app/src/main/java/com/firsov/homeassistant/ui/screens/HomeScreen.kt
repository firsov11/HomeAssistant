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
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SensorsOff
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController


@Composable
fun HomeScreen(
    navController: NavController,
    hasPresence: Boolean,
    viewModel: RealtimeDatabaseViewModel = viewModel()
) {
    val devices by viewModel.devices.collectAsState(initial = emptyList())
    val latestSensor = devices
        .filter { it.type == DeviceType.TEMPERATURE_HUMIDITY }
        .maxByOrNull { it.human_time }

    val latestSensorPressure = devices
        .filter { it.type == DeviceType.PRESSURE }
        .maxByOrNull { it.human_time }

    val radarIcon = if (hasPresence) Icons.Filled.Sensors else Icons.Filled.SensorsOff


    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
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
                )
                {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "В доме",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                        )

                        Row(modifier = Modifier.fillMaxWidth()) {
                            SquareButton(
                                text = "Радар",
                                size = squareSize,
                                icon = radarIcon
                            ) {
                                navController.navigate("radar")
                            }

                            SquareSensorButton(
                                size = squareSize,
                                data = listOf(
                                    SensorData(Icons.Filled.Thermostat, latestSensor?.temperature, "°C", "Температура"),
                                    SensorData(Icons.Filled.WaterDrop, latestSensor?.humidity, "%", "Влажность")
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
                )
                {
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
                                    SensorData(Icons.Filled.Speed, latestSensorPressure?.pressure, " мм рт. ст", "Давление")
                                )
                            ) {
                                navController.navigate("sensor_pressure")
                            }

                            SquareSensorButton(
                                size = squareSize,
                                data = listOf(
                                    SensorData(Icons.Filled.Thermostat, latestSensor?.tempout, "°C", "Температура"),
                                    SensorData(Icons.Filled.WaterDrop, latestSensor?.humout, "%", "Влажность")
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

@Composable
fun SquareButton(
    text: String,
    size: Dp,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(size)
            .height(size)
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onBackground
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
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                fontSize = 14.sp
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








package com.firsov.homeassistant.ui.screens

import DeviceData
import DeviceType
import InfoRow
import RealtimeDatabaseViewModel
import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.firsov.homeassistant.BuildConfig
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorPressureScreen(viewModel: RealtimeDatabaseViewModel = viewModel()) {
    val apiKey = BuildConfig.OPEN_WEATHER_API_KEY


    val devices by viewModel.devices.collectAsState()
    val sensorDevices = devices
        .filter { it.type == DeviceType.PRESSURE }
        .sortedByDescending { it.human_time }

    val forecastData by viewModel.forecastPressure.collectAsState()
    val pressureHistory by viewModel.pressureHistory.collectAsState()

    val combinedData = remember(pressureHistory, forecastData) {
        pressureHistory + forecastData
    }

    LaunchedEffect(Unit) {
        viewModel.loadForecast(apiKey = apiKey)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BME280 devices") }
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
                item {
                    Text(
                        text = "Атмосферное давление",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    PressureChart(
                        data = combinedData,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                items(sensorDevices) { device ->
                    SensorCard(device)
                }
            }
        }
    }
}

@Composable
fun SensorCard(device: DeviceData) {
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
                text = "Атмосферное давление: ${device.pressure} мм рт. ст."
            )
            InfoRow(
                icon = Icons.Filled.AccessTime,
                iconColor = colorScheme.primary,
                text = device.human_time
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PressureChart(data: List<Pair<String, Float>>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        AndroidView(
            modifier = Modifier.padding(8.dp).fillMaxWidth().height(280.dp),
            factory = { context ->
                LineChart(context).apply {
                    description.isEnabled = false
                    setTouchEnabled(true)
                    setPinchZoom(true)
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    axisRight.isEnabled = false
                    legend.isEnabled = true
                    setBackgroundColor(Color.TRANSPARENT)
                    xAxis.granularity = 1f
                    xAxis.setDrawGridLines(false)
                }
            },
            update = { chart ->
                val splitIndex = data.indexOfFirst { it.first.contains("(прогноз)") }.takeIf { it >= 0 } ?: data.size
                val historyEntries = data.take(splitIndex).mapIndexed { i, pair -> Entry(i.toFloat(), pair.second) }
                val forecastEntries = data.drop(splitIndex).mapIndexed { i, pair -> Entry((i + splitIndex).toFloat(), pair.second) }

                val historySet = LineDataSet(historyEntries, "История").apply {
                    color = Color.BLUE
                    setCircleColor(Color.BLUE)
                    lineWidth = 2f
                    circleRadius = 4f
                    setDrawValues(false)
                    setDrawFilled(true)
                    fillAlpha = 80
                    fillColor = Color.CYAN
                }

                val forecastSet = LineDataSet(forecastEntries, "Прогноз").apply {
                    color = Color.DKGRAY
                    setCircleColor(Color.DKGRAY)
                    lineWidth = 2f
                    enableDashedLine(10f, 5f, 0f)
                    circleRadius = 4f
                    setDrawValues(false)
                    setDrawFilled(true)
                    fillAlpha = 40
                    fillColor = Color.LTGRAY
                }

                val combinedSets = mutableListOf<ILineDataSet>(historySet, forecastSet)

                // соединяющая линия
                if (historyEntries.isNotEmpty() && forecastEntries.isNotEmpty()) {
                    val lastHistory = historyEntries.last()
                    val firstForecast = forecastEntries.first()
                    val connectorSet = LineDataSet(listOf(lastHistory, firstForecast), "Переход").apply {
                        color = Color.RED
                        setDrawCircles(false)
                        lineWidth = 2f
                        setDrawValues(false)
                        setDrawFilled(true)
                        fillAlpha = 60
                        fillColor = Color.RED
                    }
                    combinedSets.add(connectorSet)
                }

                chart.data = LineData(combinedSets)

                val shortLabels = data.map { label ->
                    label.first.substringBefore(" ").replace("(прогноз)", "").trim()
                }
                chart.xAxis.valueFormatter = IndexAxisValueFormatter(shortLabels)
                chart.xAxis.labelRotationAngle = 0f

                chart.xAxis.removeAllLimitLines()
                if (splitIndex in 1 until data.size) {
                    chart.xAxis.addLimitLine(
                        LimitLine(splitIndex.toFloat(), "Прогноз").apply {
                            lineColor = Color.RED
                            lineWidth = 1.5f
                            textColor = Color.RED
                            textSize = 12f
                            labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
                        }
                    )
                }

                chart.invalidate()
            }
        )
    }
}







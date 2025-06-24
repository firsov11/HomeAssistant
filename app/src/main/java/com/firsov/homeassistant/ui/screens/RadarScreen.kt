import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import com.google.firebase.database.FirebaseDatabase
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.outlined.SensorsOff
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.material3.ExperimentalMaterial3Api
import kotlin.collections.sortedByDescending


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadarScreen(viewModel: RealtimeDatabaseViewModel = viewModel()) {
    val devices by viewModel.devices.collectAsState()
    val radarDevices = devices
        .filter { it.type == DeviceType.RADAR }
        .sortedByDescending { it.human_time }

    var radarEnabled by remember { mutableStateOf(true) }

    // Слушаем значение включения радара из Firebase
    LaunchedEffect(Unit) {
        FirebaseDatabase.getInstance().getReference("device_control/radar")
            .get().addOnSuccessListener { snapshot ->
                snapshot.getValue(Boolean::class.java)?.let {
                    radarEnabled = it
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LD2420 devices") },
                actions = {
                    IconButton(
                        onClick = {
                            val newState = !radarEnabled
                            FirebaseDatabase.getInstance()
                                .getReference("device_control")
                                .child("radar")
                                .setValue(newState)
                            radarEnabled = newState
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
                                    imageVector = if (radarEnabled) Icons.Default.Sensors else Icons.Outlined.SensorsOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (radarEnabled) "Отключить" else "Включить",
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
                items(radarDevices) { device ->
                    DeviceCard(device)
                }
            }
        }
    }
}


@Composable
fun DeviceCard(device: DeviceData) {
    val colorScheme = MaterialTheme.colorScheme

    val containerColor = if (device.radar_alert) {
        colorScheme.primaryContainer
    } else {
        colorScheme.secondaryContainer
    }

    val contentColor = if (device.radar_alert) {
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
                icon = if (device.radar_alert) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                iconColor = colorScheme.primary,
                text = "Присутствие: ${if (device.radar_alert) "Да" else "Нет"}"
            )
            InfoRow(
                icon = Icons.Default.AccessTime,
                iconColor = colorScheme.primary,
                text = device.human_time
            )
        }
    }
}



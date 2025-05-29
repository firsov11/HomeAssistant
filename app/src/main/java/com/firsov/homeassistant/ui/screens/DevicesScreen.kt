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
import androidx.compose.runtime.Composable
import androidx.compose.material3.ExperimentalMaterial3Api



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesScreen(viewModel: RealtimeDatabaseViewModel = viewModel()) {
    val devices by viewModel.devices.collectAsState()
    val radarDevices = devices.filter { it.device_id == "Radar" }

    var radarEnabled by remember { mutableStateOf(true) }

    // Слушаем значение включения радара из Firebase
    LaunchedEffect(Unit) {
        FirebaseDatabase.getInstance().getReference("device_control/Radar")
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
                                .child("Radar")
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

    val containerColor = if (device.presence) {
        colorScheme.primaryContainer
    } else {
        colorScheme.secondaryContainer
    }

    val contentColor = if (device.presence) {
        colorScheme.onPrimaryContainer
    } else {
        colorScheme.onSecondaryContainer
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = containerColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("📟 ID: ${device.device_id}", style = MaterialTheme.typography.titleMedium)
            Text("👤 Присутствие: ${if (device.presence) "Да" else "Нет"}")
            Text("🕒 Время: ${device.human_time}")
        }
    }
}



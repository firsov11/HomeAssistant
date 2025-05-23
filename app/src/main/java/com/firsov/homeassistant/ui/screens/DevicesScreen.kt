import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesScreen(viewModel: RealtimeDatabaseViewModel = viewModel()) {
    val devices by viewModel.devices.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("ESP32 Devices") })
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(devices) { device ->
                DeviceCard(device)
            }
        }
    }
}

@Composable
fun DeviceCard(device: DeviceData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (device.status == "online") MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("ID: ${device.id}", style = MaterialTheme.typography.titleMedium)
            Text("Status: ${device.status}")
            Text("Temperature: ${device.temperature}°C")
        }
    }
}

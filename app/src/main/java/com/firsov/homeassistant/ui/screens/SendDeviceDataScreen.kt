import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendDeviceDataScreen() {
    val databaseRef = FirebaseDatabase.getInstance().getReference("presence_logs")

    var deviceId by remember { mutableStateOf("RadarTest") }
    var presence by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Отправка тестовых данных") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = deviceId,
                onValueChange = { deviceId = it },
                label = { Text("ID устройства") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text("Присутствие:")
                Spacer(Modifier.width(8.dp))
                Switch(
                    checked = presence,
                    onCheckedChange = { presence = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val deviceData = mapOf(
                        "device_id" to deviceId,
                        "presence" to presence,
                        "type" to "RADAR",
                        "timestamp" to ServerValue.TIMESTAMP
                    )

                    databaseRef.push().setValue(deviceData)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("📤 Отправить в Firebase")
            }
        }
    }
}

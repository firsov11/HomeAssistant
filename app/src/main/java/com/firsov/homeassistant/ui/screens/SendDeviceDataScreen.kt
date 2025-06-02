import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendDeviceDataScreen() {
    val databaseRef = FirebaseDatabase.getInstance().getReference("presence_logs")
    val context = LocalContext.current

    var deviceId by remember { mutableStateOf("SensorTest") }
    var temperature by remember { mutableStateOf("") }
    var pressure by remember { mutableStateOf("") }

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

            OutlinedTextField(
                value = temperature,
                onValueChange = { temperature = it },
                label = { Text("Температура (°C)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = pressure,
                onValueChange = { pressure = it },
                label = { Text("Давление (мм рт. ст.)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val tempValue = temperature.toDoubleOrNull()
                    val humValue = pressure.toDoubleOrNull()

                    if (tempValue != null && humValue != null) {
                        val deviceData = mapOf(
                            "device_id" to deviceId,
                            "temperature" to tempValue,
                            "pressure" to humValue,
                            "type" to "PRESSURE",
                            "timestamp" to ServerValue.TIMESTAMP
                        )
                        databaseRef.push().setValue(deviceData)
                        Toast.makeText(context, "✅ Данные отправлены", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "⚠️ Введите корректные значения", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("📤 Отправить в Firebase")
            }
        }
    }
}

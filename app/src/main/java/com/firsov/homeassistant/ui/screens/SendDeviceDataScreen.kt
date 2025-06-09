import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendDeviceDataScreen(navController: NavController) {
    val databaseRef = FirebaseDatabase.getInstance().getReference("presence_logs")
    val context = LocalContext.current

    var selectedType by remember { mutableStateOf<DeviceType?>(null) }
    var expanded by remember { mutableStateOf(false) }

    var deviceId by remember { mutableStateOf("") }
    var temperature by remember { mutableStateOf("") }
    var tempout by remember { mutableStateOf("") }
    var humidity by remember { mutableStateOf("") }
    var humout by remember { mutableStateOf("") }
    var pressure by remember { mutableStateOf("") }
    var co by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Отправка тестовых данных") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            // Dropdown for device type
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = selectedType?.displayName ?: "",
                    onValueChange = {},
                    label = { Text("Тип устройства") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DeviceType.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.displayName) },
                            onClick = {
                                selectedType = type
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = tempout,
                onValueChange = { tempout = it },
                label = { Text("Температура out (°C)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = pressure,
                onValueChange = { pressure = it },
                label = { Text("Давление (мм рт. ст.)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = humidity,
                onValueChange = { humidity = it },
                label = { Text("Влажность (%)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = humout,
                onValueChange = { humout = it },
                label = { Text("Влажность out (%)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = co,
                onValueChange = { co = it },
                label = { Text("Угарный газ") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val deviceData = mutableMapOf<String, Any>(
                        "type" to (selectedType?.name ?: "UNKNOWN"),
                        "device_id" to deviceId,
                        "timestamp" to ServerValue.TIMESTAMP
                    )

                    temperature.toDoubleOrNull()?.let { deviceData["temperature"] = it }
                    tempout.toDoubleOrNull()?.let { deviceData["tempout"] = it }
                    humidity.toDoubleOrNull()?.let { deviceData["humidity"] = it }
                    humout.toDoubleOrNull()?.let { deviceData["humout"] = it }
                    pressure.toDoubleOrNull()?.let { deviceData["pressure"] = it }
                    co.toDoubleOrNull()?.let { deviceData["co"] = it }

                    databaseRef.push().setValue(deviceData)
                    Toast.makeText(context, "✅ Данные отправлены", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("📤 Отправить в Firebase")
            }
        }
    }
}

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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

    var radar_alert by remember { mutableStateOf(false) }
    var co_alert by remember { mutableStateOf(false) }
    var vent by remember { mutableStateOf(false) }

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


            if (selectedType == DeviceType.RADAR) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Присутствие обнаружено")
                    Switch(
                        checked = radar_alert,
                        onCheckedChange = { radar_alert = it }
                    )
                }
            }

            if (selectedType == DeviceType.CO) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Угарный газ обнаружен")
                    Switch(
                        checked = co_alert,
                        onCheckedChange = { co_alert = it }
                    )
                }
            }

            if (selectedType == DeviceType.VENTILATION) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Вентиляция включена")
                    Switch(
                        checked = vent,
                        onCheckedChange = { vent = it }
                    )
                }
            }

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

                    if (selectedType == DeviceType.RADAR) {
                        deviceData["radar_alert"] = radar_alert
                    }

                    if (selectedType == DeviceType.CO) {
                        deviceData["co_alert"] = co_alert
                    }

                    if (selectedType == DeviceType.VENTILATION) {
                        deviceData["vent"] = vent
                    }

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

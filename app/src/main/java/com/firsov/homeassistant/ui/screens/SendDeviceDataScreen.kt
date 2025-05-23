package com.firsov.homeassistant.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@Composable
fun SendDeviceDataScreen() {
    var deviceId by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var temperature by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    val dbRef = Firebase.database.reference

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = deviceId,
            onValueChange = { deviceId = it },
            label = { Text("Device ID (например, esp32_1)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = status,
            onValueChange = { status = it },
            label = { Text("Status (online/offline)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = temperature,
            onValueChange = { temperature = it },
            label = { Text("Temperature") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val temp = temperature.toDoubleOrNull()
                if (deviceId.isNotBlank() && status.isNotBlank() && temp != null) {
                    val data = mapOf(
                        "status" to status,
                        "temperature" to temp
                    )
                    dbRef.child("devices").child(deviceId).setValue(data)
                        .addOnSuccessListener {
                            message = "Данные успешно отправлены!"
                        }
                        .addOnFailureListener {
                            message = "Ошибка отправки: ${it.message}"
                        }
                } else {
                    message = "Проверь правильность ввода."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Отправить в RTDB")
        }

        if (message.isNotBlank()) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

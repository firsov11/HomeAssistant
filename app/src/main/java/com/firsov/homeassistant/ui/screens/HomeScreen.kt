package com.firsov.homeassistant.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController, hasPresence: Boolean) {
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
                Row(modifier = Modifier.fillMaxWidth()) {
                    SquareButton(
                        text = "Радар",
                        size = squareSize,
                        iconEmoji = if (hasPresence) "👁️‍🗨️" else "👁️"
                    ) {
                        navController.navigate("devices")
                    }

                    SquareButton(
                        text = "AHT20",
                        size = squareSize,
                        iconEmoji = "🌡️"
                    ) {
                        navController.navigate("sensor")
                    }
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    SquareButton("Кнопка 3", squareSize) {
                        // TODO
                    }
                    SquareButton("Кнопка 4", squareSize) {
                        // TODO
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
    iconEmoji: String? = null,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(size)
            .height(size)
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp) // скруглённые углы
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (iconEmoji != null) {
                Text(iconEmoji, fontSize = 20.sp)
            }
            Text(text, fontSize = 14.sp)
        }
    }
}





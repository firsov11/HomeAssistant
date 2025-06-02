import androidx.compose.ui.graphics.vector.ImageVector

data class SensorData(
    val icon: ImageVector,
    val value: Float?,
    val unit: String,
    val label: String
)

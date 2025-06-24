
data class DeviceData(
    val device_id: String,
    val radar_alert: Boolean = false,
    val vent: Boolean = false,
    val temperature: Float? = null,
    val humidity: Float? = null,
    val tempout: Float? = null,
    val humout: Float? = null,
    val pressure: Float? = null,
    val type: DeviceType,
    val human_time: String = "",
    val co: Float? = null,
    val co_alert: Boolean = false
)

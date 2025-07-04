
data class DeviceData(
    val device_id: String,
    val temperature: Float? = null,
    val humidity: Float? = null,
    val tempout: Float? = null,
    val humout: Float? = null,
    val pressure: Float? = null,
    val type: DeviceType,
    val human_time: String = "",
    val co: Float? = null,
    val co_a: Boolean = false,
    val radar_a: Boolean = false,
    val vent_a: Boolean = false,
    val timestamp: Long = 0L
)

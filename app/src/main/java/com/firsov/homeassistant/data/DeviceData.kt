data class DeviceData(
    val device_id: String,
    val presence: Boolean = false,
    val temperature: Float? = null,
    val humidity: Float? = null,
    val type: DeviceType,
    val human_time: String = ""
)





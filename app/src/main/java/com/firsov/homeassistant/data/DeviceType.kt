enum class DeviceType(val displayName: String) {
    RADAR("Радар"),
    TEMPERATURE_HUMIDITY("Температура и влажность"),
    PRESSURE("Давление"),
    TEMPERATURE_HUMIDITY_OUT("Температура и влажность (внешние)"),
    VENTILATION("Вентиляция"),
    CO("Угарный газ");

    override fun toString(): String = displayName
}
